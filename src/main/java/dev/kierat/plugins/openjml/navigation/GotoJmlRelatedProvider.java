package dev.kierat.plugins.openjml.navigation;

import com.intellij.navigation.GotoRelatedItem;
import com.intellij.navigation.GotoRelatedProvider;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMember;
import dev.kierat.plugins.openjml.psi.PsiJMLFile;
import dev.kierat.plugins.openjml.utils.JMLElementUtil;
import dev.kierat.plugins.openjml.utils.JMLFileUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class GotoJmlRelatedProvider extends GotoRelatedProvider {

    @Override
    public @NotNull List<? extends GotoRelatedItem> getItems(@NotNull PsiElement psiElement) {
        if (!(psiElement instanceof PsiMember || psiElement.getParent() instanceof PsiMember)) {
            return List.of();
        }

        PsiFile file = psiElement.getContainingFile();
        Module module = ModuleUtilCore.findModuleForPsiElement(file);
        return switch (file) {
            case PsiJMLFile jmlFile -> getRelatedItems(module, jmlFile, psiElement);
            case PsiJavaFile javaFile -> getRelatedItems(module, javaFile, psiElement);
            default -> Collections.emptyList();
        };
    }

    @Override
    public @NotNull List<? extends GotoRelatedItem> getItems(@NotNull DataContext context) {
        PsiElement psiElement = CommonDataKeys.PSI_ELEMENT.getData(context);
        if (psiElement != null && (psiElement instanceof PsiMember || psiElement.getParent() instanceof PsiMember)) {
            return List.of();
        }

        PsiFile file = CommonDataKeys.PSI_FILE.getData(context);
        if (file == null) { return Collections.emptyList(); }

        Module module = ModuleUtilCore.findModuleForPsiElement(file);
        return switch (file) {
            case PsiJMLFile jmlFile -> getRelatedItems(module, jmlFile);
            case PsiJavaFile javaFile -> getRelatedItems(module, javaFile);
            default -> Collections.emptyList();
        };
    }

    private @NotNull List<GotoRelatedItem> getRelatedItems(Module module, PsiJavaFile javaFile) {
        PsiFile psiJmlFile = JMLFileUtil.getJmlFile(module, javaFile);
        if (psiJmlFile == null) return Collections.emptyList();
        return List.of(new GotoRelatedItem(psiJmlFile, "JML"));
    }

    private @NotNull List<GotoRelatedItem> getRelatedItems(Module module, PsiJavaFile javaFile, PsiElement javaElement) {
        PsiJMLFile jmlFile = JMLFileUtil.getJmlFile(module, javaFile);
        if (jmlFile == null) return Collections.emptyList();
        PsiElement jmlElement = jmlFile.findElement(javaElement);
        if (jmlElement == null) return Collections.emptyList();
        return List.of(new GotoRelatedItem(jmlElement, "JML"));
    }

    private @NotNull List<GotoRelatedItem> getRelatedItems(Module module, PsiJMLFile jmlFile) {
        PsiFile javaFile = JMLFileUtil.getJavaFile(module, jmlFile);
        if (javaFile == null) return Collections.emptyList();
        return List.of(new GotoRelatedItem(javaFile, "Java"));
    }

    private @NotNull List<GotoRelatedItem> getRelatedItems(Module module, PsiJMLFile jmlFile, PsiElement jmlElement) {
        PsiFile javaFile = JMLFileUtil.getJavaFile(module, jmlFile);
        if (javaFile == null) return Collections.emptyList();
        PsiElement javaElement = JMLElementUtil.findElement(javaFile, jmlElement);
        if (javaElement == null) return Collections.emptyList();
        return List.of(new GotoRelatedItem(javaElement, "Java"));
    }

}