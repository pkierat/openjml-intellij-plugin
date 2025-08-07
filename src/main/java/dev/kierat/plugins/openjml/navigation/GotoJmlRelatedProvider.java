package dev.kierat.plugins.openjml.navigation;

import com.intellij.navigation.GotoRelatedItem;
import com.intellij.navigation.GotoRelatedProvider;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import dev.kierat.plugins.openjml.psi.PsiJMLFile;
import dev.kierat.plugins.openjml.roots.JMLFileUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class GotoJmlRelatedProvider extends GotoRelatedProvider {

    @Override
    public @NotNull List<? extends GotoRelatedItem> getItems(@NotNull DataContext context) {
        PsiFile file = CommonDataKeys.PSI_FILE.getData(context);
        if (file == null) { return Collections.emptyList(); }

        Project project = file.getProject();
        if (file instanceof PsiJavaFile javaFile) {
            return getRelatedItems(javaFile, project);
        } else if (file instanceof PsiJMLFile jmlFile) {
            return getRelatedItems(jmlFile, project);
        } else {
            return Collections.emptyList();
        }
    }

    private @NotNull List<GotoRelatedItem> getRelatedItems(PsiJavaFile javaFile, Project project) {
        Module module = ModuleUtilCore.findModuleForPsiElement(javaFile);

        VirtualFile jmlFile = JMLFileUtil.getJmlFile(module, javaFile);
        PsiFile psiJmlFile = PsiManager.getInstance(project).findFile(jmlFile);

        if (psiJmlFile == null) return Collections.emptyList();

        return List.of(new GotoRelatedItem(psiJmlFile, "JML Specification"));
    }

    private @NotNull List<GotoRelatedItem> getRelatedItems(PsiJMLFile jmlFile, Project project) {
        Module module = ModuleUtilCore.findModuleForPsiElement(jmlFile);

        VirtualFile javaFile = JMLFileUtil.getJavaFile(module, jmlFile);
        PsiFile psiJavaFile = PsiManager.getInstance(project).findFile(javaFile);

        if (psiJavaFile == null) return Collections.emptyList();

        return List.of(new GotoRelatedItem(psiJavaFile, "JML-Specified Classes"));
    }


}