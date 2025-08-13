package dev.kierat.plugins.openjml.utils;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import dev.kierat.plugins.openjml.psi.JMLFileType;
import dev.kierat.plugins.openjml.psi.PsiJMLFile;
import dev.kierat.plugins.openjml.utils.generator.JMLFileGenerator;
import dev.kierat.plugins.openjml.utils.generator.JMLGenerationConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.java.JavaSourceRootType;

import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElseGet;


public final class JMLFileUtil {

    public static final String JAVA = "java";
    public static final String JML = "jml";

    private JMLFileUtil() {
        // do nothing
    }

    public static @Nullable PsiJMLFile getJmlFile(@NotNull Module module, @NotNull PsiFile psiJavaFile) {
        VirtualFile javaSourceRoot = getJavaSourceRoot(module);
        VirtualFile jmlSourceRoot = getJmlSourceRoot(module);

        VirtualFile virtualFile = psiJavaFile.getVirtualFile();
        String fileName = getFileNameWithExtension(virtualFile, JML);
        Path jmlFilePath = getRelativePath(javaSourceRoot, virtualFile.getParent()).resolve(fileName);
        VirtualFile jmlFile = jmlSourceRoot.findFileByRelativePath(jmlFilePath.toString());
        return jmlFile == null ? null : (PsiJMLFile) PsiManager.getInstance(module.getProject()).findFile(jmlFile);
    }

    public static @Nullable PsiJavaFile getJavaFile(@NotNull Module module, @NotNull PsiFile psiFile) {
        VirtualFile javaSourceRoot = JMLFileUtil.getJavaSourceRoot(module);
        VirtualFile jmlSourceRoot = JMLFileUtil.getJmlSourceRoot(module);

        VirtualFile virtualFile = psiFile.getVirtualFile();
        String fileName = getFileNameWithExtension(virtualFile, JAVA);
        Path jmlFilePath = getRelativePath(jmlSourceRoot, virtualFile.getParent()).resolve(fileName);
        VirtualFile javaFile = javaSourceRoot.findFileByRelativePath(jmlFilePath.toString());
        return javaFile == null ? null : (PsiJavaFile) PsiManager.getInstance(module.getProject()).findFile(javaFile);
    }

    private static VirtualFile getJmlSourceRoot(Module module) {
        return getSourceRoot(module, JML)
                .or(() -> getSourceRoot(module, JAVA))
                .orElseThrow(() -> new NoSuchElementException("JML source root not found!"));
    }

    private static VirtualFile getJavaSourceRoot(Module module) {
        return getSourceRoot(module, JAVA)
                .orElseThrow(() -> new NoSuchElementException("JML source root not found!"));
    }

    private static Optional<VirtualFile> getSourceRoot(Module module, String name) {
        var moduleRootManager = ModuleRootManager.getInstance(module);
        var sourceRoots = moduleRootManager.getSourceRoots(JavaSourceRootType.SOURCE);
        return sourceRoots.stream()
                .filter(sourceRoot -> sourceRoot.getName().equals(name)).findFirst()
                .or(() -> sourceRoots.stream().findFirst());
    }

    public static PsiJMLFile createJmlFile(Module module, PsiJavaFile psiJavaFile) {
        String content = generateJmlFileContent(psiJavaFile);
        return createJmlFile(module, psiJavaFile, content);
    }

    private static @NotNull String generateJmlFileContent(PsiJavaFile psiJavaFile) {
        JMLGenerationConfig config = new JMLGenerationConfig(false);
        JMLFileGenerator visitor = new JMLFileGenerator(psiJavaFile.getPackageName(), config);
        return visitor.generate(psiJavaFile);
    }

    public static PsiJMLFile createJmlFile(Module module, PsiJavaFile psiJavaFile, String content) {
        Project project = module.getProject();
        VirtualFile javaSourceRoot = JMLFileUtil.getJavaSourceRoot(module);
        VirtualFile jmlSourceRoot = JMLFileUtil.getJmlSourceRoot(module);

        String specFileName = getFileNameWithExtension(psiJavaFile.getVirtualFile(), JML);
        PsiDirectory psiJmlSourceRoot = PsiManager.getInstance(project).findDirectory(jmlSourceRoot);
        Path relativePath = getRelativePath(javaSourceRoot, psiJavaFile.getVirtualFile().getParent());
        PsiDirectory specDir = createSubdirectories(psiJmlSourceRoot, relativePath);

        PsiJMLFile specFile = (PsiJMLFile) PsiFileFactory.getInstance(project)
                .createFileFromText(specFileName, JMLFileType.INSTANCE, content);
        specDir.add(specFile);

        Document document = requireNonNull(PsiDocumentManager.getInstance(project).getDocument(psiJavaFile));
        FileDocumentManager.getInstance().saveDocument(document);

        return specFile;
    }

    private static @NotNull Path getRelativePath(VirtualFile base, VirtualFile file) {
        return getRelativePath(requireNonNull(base), file, Path.of(""));
    }

    private static @NotNull Path getRelativePath(VirtualFile base, VirtualFile file, Path path) {
        if (file == null || base.getPath().equals(file.getPath())) {
            return path;
        }
        return getRelativePath(base, file.getParent(), Path.of(file.getName()).resolve(path));
    }

    private static @NotNull String getFileNameWithExtension(VirtualFile psiFile, String extension) {
        return psiFile.getNameWithoutExtension() + "." + extension;
    }

    private static @NotNull PsiDirectory createSubdirectories(PsiDirectory parent, Path relativePath) {
        if (relativePath == null) {
            return parent;
        }
        PsiDirectory psiDir = parent;
        for (Path segment : relativePath) {
            psiDir = createSubdirectory(psiDir, segment.getFileName().toString());
        }
        return psiDir;
    }

    private static @NotNull PsiDirectory createSubdirectory(PsiDirectory parent, String subdir) {
        return requireNonNullElseGet(parent.findSubdirectory(subdir), () -> parent.createSubdirectory(subdir));
    }

}
