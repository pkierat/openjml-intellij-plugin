package dev.kierat.plugins.openjml.utils.generator.adapter.element;

import com.intellij.psi.*;

import javax.lang.model.element.NestingKind;
import java.util.Arrays;
import java.util.function.Predicate;

public enum PsiNestingKind {
    TOP_LEVEL(NestingKind.TOP_LEVEL, psiClass -> psiClass.getContainingClass() == null && !isLocalClass(psiClass)),
    MEMBER(NestingKind.MEMBER, psiClass -> psiClass.getContainingClass() != null && !isLocalClass(psiClass)),
    LOCAL(NestingKind.LOCAL, PsiNestingKind::isLocalClass),
    ANONYMOUS(NestingKind.ANONYMOUS, PsiAnonymousClass.class::isInstance)
    ;

    private final NestingKind kind;
    private final Predicate<PsiClass> condition;

    PsiNestingKind(NestingKind kind, Predicate<PsiClass> condition) {
        this.kind = kind;
        this.condition = condition;
    }

    public NestingKind getKind() {
        return kind;
    }

    public static PsiNestingKind of(PsiClass psiClass) {
        return Arrays.stream(values())
                .filter(v -> v.condition.test(psiClass))
                .findFirst()
                .orElseThrow();
    }

    private static boolean isLocalClass(PsiClass psiClass) {
        PsiElement parent = psiClass.getParent();
        while (parent != null) {
            if (parent instanceof PsiMethod || parent instanceof PsiCodeBlock || parent instanceof PsiLambdaExpression) {
                return true;
            }
            parent = parent.getParent();
        }
        return false;
    }
}
