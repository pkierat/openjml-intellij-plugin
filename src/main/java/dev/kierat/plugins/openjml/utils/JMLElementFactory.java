package dev.kierat.plugins.openjml.utils;

import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.source.tree.java.PsiJavaTokenImpl;
import org.jetbrains.annotations.NotNull;

public class JMLElementFactory {

    public PsiElement createJmlElement(PsiElement javaElement) {
        return switch (javaElement) {
            case PsiField psiField -> createField(psiField);
            case PsiMethod psiMethod -> createMethod(psiMethod);
            case PsiClass psiClass -> createClass(psiClass);
            default -> javaElement.copy();
        };
    }

    private PsiElement createClass(PsiClass psiClass) {
        PsiClass copy = (PsiClass) psiClass.copy();
        copy.accept(new JavaElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (element instanceof PsiMember) {
                    element.delete();
                }
            }
        });
        return copy;
    }

    private PsiElement createMethod(PsiMethod psiMethod) {
        PsiMethod copy = (PsiMethod) psiMethod.copy();
        copy.accept(new JavaElementVisitor() {
            @Override
            public void visitCodeBlock(@NotNull PsiCodeBlock block) {
                block.getParent().addAfter(block, new PsiJavaTokenImpl(JavaTokenType.SEMICOLON, ";"));
                block.delete();
            }
        });
        return copy;
    }

    private static @NotNull PsiElement createField(PsiField psiField) {
        PsiField copy = (PsiField) psiField.copy();
        psiField.accept(new JavaElementVisitor() {
            @Override
            public void visitExpression(@NotNull PsiExpression expression) {
                expression.getPrevSibling().delete();
                expression.delete();
            }
        });
        return copy;
    }

}
