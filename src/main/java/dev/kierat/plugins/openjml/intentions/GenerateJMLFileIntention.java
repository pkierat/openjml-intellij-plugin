package dev.kierat.plugins.openjml.intentions;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.preview.IntentionPreviewInfo;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;
import dev.kierat.plugins.openjml.psi.JMLFileType;
import dev.kierat.plugins.openjml.psi.JMLLanguage;
import org.jetbrains.annotations.NotNull;
import dev.kierat.plugins.openjml.OpenJMLBundle;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public final class GenerateJMLFileIntention implements IntentionAction {

    @Override
    public @NotNull String getText() {
        return OpenJMLBundle.message("intention.generate.jml.specification.name");
    }

    @Override
    public @NotNull String getFamilyName() {
        return OpenJMLBundle.message("intention.generate.jml.specification.name");
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile psiFile) {
        return isMavenLikeStructure(project, psiFile) &&
                Optional.of(psiFile)
                .filter(PsiJavaFile.class::isInstance)
                .flatMap(pf -> getPsiElementAtCursor(editor, pf))
                .flatMap(GenerateJMLFileIntention::findPsiClass)
                .isPresent();
    }

    private boolean isMavenLikeStructure(@NotNull Project project, PsiFile psiFile) {
        VirtualFile sourceRoot = findSourceRoot(project, psiFile.getOriginalFile().getVirtualFile());
        return sourceRoot.getPath().endsWith("src%1$smain%1$sjava".formatted(File.separator));
    }

    private static Optional<PsiClass> findPsiClass(PsiElement psiElement) {
        if (psiElement == null) {
            return Optional.empty();
        }
        return psiElement.getParent() instanceof PsiClass psiClass
                ? Optional.of(psiClass)
                : findPsiClass(psiElement.getParent());
    }

    private static Optional<PsiElement> getPsiElementAtCursor(Editor editor, PsiFile psiFile) {
        int offset = editor.getCaretModel().getOffset();
        return Optional.ofNullable(psiFile.findElementAt(offset));
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile psiFile) throws IncorrectOperationException {
        if (!(psiFile instanceof PsiJavaFile psiJavaFile)) return;

        try {
            VirtualFile javaFile = psiFile.getOriginalFile().getVirtualFile();
            if (javaFile == null) return;

            VirtualFile javaSourceRoot = findSourceRoot(project, javaFile);
            if (javaSourceRoot == null) return;

            String javaSourceRootPath = javaSourceRoot.getPath();

            Path relativeJavaFilePath = Path.of(javaFile.getPath().replaceFirst(javaSourceRootPath, ""));
            String specFileName = getSpecFileName(relativeJavaFilePath);

            VirtualFile jmlSourceRoot = createDirectories(javaSourceRoot.getParent(), Path.of(JMLLanguage.SOURCE_ROOT_NAME));
            VirtualFile specDir = createDirectories(jmlSourceRoot, relativeJavaFilePath.getParent());

            PsiDirectory psiSpecDir = PsiManager.getInstance(project).findDirectory(specDir);
            if (psiSpecDir == null) {
                throw new IllegalStateException("%s not found!".formatted(specDir));
            }

            getPsiElementAtCursor(editor, psiFile)
                    .flatMap(GenerateJMLFileIntention::findPsiClass)
                    .map(psiClass -> createJmlFileForClass(project, psiClass, psiSpecDir, specFileName))
                    .ifPresent(specFile -> FileEditorManager.getInstance(project).openFile(specFile.getVirtualFile()));
        } catch (IOException | UncheckedIOException e) {
            throw new IncorrectOperationException("Failed to create JML file", (Throwable) e);
        }
    }

    private static @NotNull String getSpecFileName(Path javaFilePath) {
        return javaFilePath.getFileName().toString()
                .replaceFirst("\\.java$", "." + JMLFileType.INSTANCE.getDefaultExtension());
    }

    private VirtualFile createDirectories(VirtualFile sourceRoot, Path relativePath) throws IOException {
        VirtualFile current = sourceRoot;
        for (Path pathSegment : relativePath) {
            VirtualFile next = current.findChild(pathSegment.toString());
            if (next == null) {
                next = current.createChildDirectory(this, pathSegment.toString());
            }
            current = next;
        }
        return current;
    }

    private VirtualFile findSourceRoot(Project project, VirtualFile file) {
        return ProjectRootManager.getInstance(project).getFileIndex().getSourceRootForFile(file);
    }

    private PsiFile createJmlFileForClass(Project project, PsiClass psiClass, PsiDirectory specDir, String specFileName) {
        PsiFile specFile = specDir.findFile(specFileName);
        if (specFile != null) return specFile;

        String content = generateJmlSpecForClass(psiClass);
        specFile = PsiFileFactory.getInstance(project)
                .createFileFromText(specFileName, JMLFileType.INSTANCE, content);
        specDir.add(specFile);
        return specFile;
    }

    private static @NotNull String generateJmlSpecForClass(PsiClass psiClass) {
        return "/*@ \\* Generated JML specification */";
    }

    private String getFileName(String fullPath) {
        return fullPath.substring(fullPath.lastIndexOf("/") + 1);
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }

    @Override
    public @NotNull IntentionPreviewInfo generatePreview(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        return IntentionPreviewInfo.EMPTY;
    }
}