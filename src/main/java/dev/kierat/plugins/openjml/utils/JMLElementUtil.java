package dev.kierat.plugins.openjml.utils;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassOwner;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiSubstitutor;
import com.intellij.psi.util.MethodSignature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.UnaryOperator;

public final class JMLElementUtil {

    private JMLElementUtil() {
        // do nothing
    }

    public static @Nullable PsiElement findElement(@NotNull PsiFile file, @NotNull PsiElement element) {
        if (element instanceof PsiFile) { return file; }

        element = getAsMember(element);

        PsiElement parent = findElement(file, element.getParent());
        return findChild(parent, element);
    }

    public static @Nullable PsiElement findChild(PsiElement parent, PsiElement psiElement) {
        if (parent instanceof PsiClassOwner file && psiElement instanceof PsiClass javaClass) {
            return findClassByName(file, javaClass.getName());
        } else if (parent instanceof PsiClass parentClass) {
            return switch (psiElement) {
                case PsiClass psiClass -> parentClass.findInnerClassByName(psiClass.getName(), false);
                case PsiMethod psiMethod -> findMethodBySignature(parentClass, psiMethod);
                case PsiField psiField -> parentClass.findFieldByName(psiField.getName(), false);
                default -> null;
            };
        }
        return null;
    }

    public static @Nullable PsiClass findClassByName(PsiClassOwner file, String name) {
        for (PsiClass jmlClass : file.getClasses()) {
            if (Objects.equals(jmlClass.getName(), name)) {
                return jmlClass;
            }
        }
        return null;
    }

    public static @NotNull PsiMember getAsMember(@NotNull PsiElement element) {
        PsiElement javaElement = element;
        while (!(javaElement instanceof PsiMember psiMember)) {
            if ((javaElement = javaElement.getParent()) == null) {
                throw new NoSuchElementException("No PsiMember parent found for element: " + element);
            }
        }
        return psiMember;
    }

    public static @NotNull PsiElement findOrCreateElement(@NotNull PsiFile file,
                                                          @NotNull PsiElement element,
                                                          @NotNull UnaryOperator<PsiElement> elementFactory) {
        if (element instanceof PsiFile) { return file; }

        element = getAsMember(element);

        PsiElement parent = findOrCreateElement(file, element.getParent(), elementFactory);
        return findOrCreateChild(parent, element, elementFactory);
    }

    public static PsiElement findOrCreateChild(@NotNull PsiElement parent,
                                               @NotNull PsiElement element,
                                               @NotNull UnaryOperator<PsiElement> elementFactory) {
        PsiElement jmlElement = JMLElementUtil.findChild(parent, element);
        if (jmlElement != null) { return jmlElement; }
        jmlElement = elementFactory.apply(element);
        parent.add(jmlElement);
        return jmlElement;
    }

    private static PsiMethod findMethodBySignature(PsiClass psiClass, PsiMethod javaMethod) {
        MethodSignature javaSignature = javaMethod.getSignature(PsiSubstitutor.EMPTY);
        for (PsiMethod jmlMethod : psiClass.findMethodsByName(javaMethod.getName(), false)) {
            MethodSignature jmlSignature = jmlMethod.getSignature(PsiSubstitutor.EMPTY);
            if (areMethodSignaturesEqual(jmlSignature, javaSignature)) {
                return jmlMethod;
            }
        }
        return null;
    }

    private static boolean areMethodSignaturesEqual(MethodSignature jmlSignature, MethodSignature javaSignature) {
        if (jmlSignature.isConstructor() != javaSignature.isConstructor()
                || jmlSignature.getParameterTypes().length != javaSignature.getParameterTypes().length) {
            return false;
        }
        for (int i = 0; i < jmlSignature.getParameterTypes().length; i++) {
            if (!jmlSignature.getParameterTypes()[i].getPresentableText(false)
                    .equals(javaSignature.getParameterTypes()[i].getPresentableText(false))) {
                return false;
            }
        }
        return true;
    }

}
