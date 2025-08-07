package dev.kierat.plugins.openjml.roots;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import dev.kierat.plugins.openjml.psi.JMLFileType;
import dev.kierat.plugins.openjml.psi.PsiJMLFile;
import org.jetbrains.annotations.NotNull;
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

    public static VirtualFile getJmlFile(Module module, PsiJavaFile psiJavaFile) {
        VirtualFile javaSourceRoot = getJavaSourceRoot(module);
        VirtualFile jmlSourceRoot = getJmlSourceRoot(module);

        String fileName = getFileNameWithExtension(psiJavaFile.getVirtualFile(), JML);
        Path jmlFilePath = getRelativePath(javaSourceRoot, psiJavaFile.getVirtualFile().getParent()).resolve(fileName);
        return jmlSourceRoot.findFileByRelativePath(jmlFilePath.toString());
    }

    public static VirtualFile getJavaFile(Module module, PsiJMLFile psiFile) {
        VirtualFile javaSourceRoot = JMLFileUtil.getJavaSourceRoot(module);
        VirtualFile jmlSourceRoot = JMLFileUtil.getJmlSourceRoot(module);

        String fileName = getFileNameWithExtension(psiFile.getVirtualFile(), JAVA);
        Path jmlFilePath = getRelativePath(jmlSourceRoot, psiFile.getVirtualFile().getParent()).resolve(fileName);
        return javaSourceRoot.findFileByRelativePath(jmlFilePath.toString());
    }

    private static VirtualFile getJmlSourceRoot(Module module) {
        return getSourceRoot(module, JML).or(() -> getSourceRoot(module, JAVA))
                .orElseThrow(() -> new NoSuchElementException("JML source root not found!"));
    }

    private static VirtualFile getJavaSourceRoot(Module module) {
        return getSourceRoot(module, JAVA)
                .orElseThrow(() -> new NoSuchElementException("JML source root not found!"));
    }

    private static Optional<VirtualFile> getSourceRoot(Module module, String name) {
        ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
        return moduleRootManager.getSourceRoots(JavaSourceRootType.SOURCE).stream()
                .filter(sourceRoot -> sourceRoot.getName().equals(name))
                .findFirst();
    }

    public static void createJmlFile(Project project, Module module, PsiJavaFile psiJavaFile, String content) {
        VirtualFile javaSourceRoot = JMLFileUtil.getJavaSourceRoot(module);
        VirtualFile jmlSourceRoot = JMLFileUtil.getJmlSourceRoot(module);

        String specFileName = getFileNameWithExtension(psiJavaFile.getVirtualFile(), JML);
        PsiDirectory psiJmlSourceRoot = PsiManager.getInstance(project).findDirectory(jmlSourceRoot);
        Path relativePath = getRelativePath(javaSourceRoot, psiJavaFile.getVirtualFile().getParent());
        PsiDirectory specDir = createSubdirectories(psiJmlSourceRoot, relativePath);

        PsiFile specFile = PsiFileFactory.getInstance(project)
                .createFileFromText(specFileName, JMLFileType.INSTANCE, content);
        specDir.add(specFile);

        Document document = requireNonNull(PsiDocumentManager.getInstance(project).getDocument(psiJavaFile));
        FileDocumentManager.getInstance().saveDocument(document);
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
