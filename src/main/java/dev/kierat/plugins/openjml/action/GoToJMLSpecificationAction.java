package dev.kierat.plugins.openjml.action;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;
import dev.kierat.plugins.openjml.action.generator.JmlGenerationConfig;
import dev.kierat.plugins.openjml.action.generator.JmlStubGenerator;
import dev.kierat.plugins.openjml.roots.JMLFileUtil;
import org.jetbrains.annotations.NotNull;
import dev.kierat.plugins.openjml.OpenJMLBundle;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public final class GoToJMLSpecificationAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        Project project = e.getProject();
        if (project != null && file != null) {
            PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
            invoke(project, psiFile);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        boolean available = false;
        if (project != null && file != null) {
            PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
            available = isAvailable(project, psiFile);
        }
        e.getPresentation().setEnabledAndVisible(available);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    public boolean isAvailable(Project project, PsiFile psiFile) {
        if (project == null || psiFile == null) { return false; }
        return isMavenLikeStructure(project, psiFile) &&
                Optional.of(psiFile)
                        .filter(PsiJavaFile.class::isInstance)
                        .isPresent();
    }

    private boolean isMavenLikeStructure(@NotNull Project project, PsiFile psiFile) {
        VirtualFile sourceRoot = findJavaSourceRoot(project, psiFile.getOriginalFile().getVirtualFile());
        return sourceRoot.getPath().endsWith("src/main/java");
    }

    private void invoke(@NotNull Project project, PsiFile psiFile) throws IncorrectOperationException {
        if (!(psiFile instanceof PsiJavaFile psiJavaFile)) return;

        Module module = ModuleUtilCore.findModuleForPsiElement(psiJavaFile);

        VirtualFile specFile = JMLFileUtil.getJmlFile(module, psiJavaFile);
        if (specFile == null && shouldCreateSpecFile(project)) {
            specFile = WriteAction.computeAndWait(() -> {
                generateJmlSpec(project, module, psiJavaFile);
                return JMLFileUtil.getJmlFile(module, psiJavaFile);
            });
        }
        if (specFile != null) {
            FileEditorManager.getInstance(project).openFile(specFile, true);
        }
    }

    private void generateJmlSpec(Project project, Module module, PsiJavaFile psiJavaFile) {
        String content = generateJmlSpec(psiJavaFile);
        JMLFileUtil.createJmlFile(project, module, psiJavaFile, content);
    }

    private VirtualFile findJavaSourceRoot(Project project, VirtualFile file) {
        return ProjectRootManager.getInstance(project).getFileIndex().getSourceRootForFile(file);
    }

    private static boolean shouldCreateSpecFile(Project project) {
        AtomicBoolean response = new AtomicBoolean();
        ApplicationManager.getApplication().invokeAndWait(() -> {
            int result = Messages.showYesNoDialog(project,
                    OpenJMLBundle.message("intention.goto.jml.specification.create.text"),
                    OpenJMLBundle.message("intention.goto.jml.specification.create.title"),
                    Messages.getQuestionIcon()
            );
            response.set(result == Messages.YES);
        });
        return response.get();
    }

    private static @NotNull String generateJmlSpec(PsiJavaFile psiJavaFile) {
        JmlGenerationConfig config = new JmlGenerationConfig(false);
        JmlStubGenerator visitor = new JmlStubGenerator(psiJavaFile.getPackageName(), config);
        return visitor.generate(psiJavaFile);
    }

}