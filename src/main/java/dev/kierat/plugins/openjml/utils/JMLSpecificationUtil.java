package dev.kierat.plugins.openjml.utils;

import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.psi.PsiElement;
import dev.kierat.plugins.openjml.psi.PsiJMLSpecification;

import java.util.Optional;

public final class JMLSpecificationUtil {

    private JMLSpecificationUtil() {
        // does nothing
    }

    public static boolean hasSpecification(PsiElement element) {
        for (PsiElement child : element.getChildren()) {
            if (child instanceof PsiJMLSpecification) {
                return true;
            }
        }
        return false;
    }

    public static PsiJMLSpecification getSpecification(PsiElement jmlElement) {
        for (PsiElement child : jmlElement.getChildren()) {
            if (child instanceof PsiJMLSpecification spec) {
                return spec;
            }
        }
        return null;
    }

    public static Optional<PsiJMLSpecification> findSpecification(PsiElement javaElement) {
        return Optional.ofNullable(ModuleUtilCore.findModuleForPsiElement(javaElement))
                .map(module -> JMLFileUtil.getJmlFile(module, javaElement.getContainingFile()))
                .map(jmlFile -> JMLElementUtil.findElement(jmlFile, javaElement))
                .map(JMLSpecificationUtil::getSpecification);
    }

}
