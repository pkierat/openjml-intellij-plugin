package dev.kierat.plugins.openjml.action.element;

import com.intellij.psi.*;

import javax.lang.model.element.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class PsiElementAdapter<T extends PsiElement> implements Element {

    protected final T element;

    protected PsiElementAdapter(T element) {
        this.element = element;
    }

    public static <P extends PsiElement, E extends Element> E of(P element) {
        return PsiElementAdapterType.wrap(element);
    }

    @Override
    public ElementKind getKind() {
        return PsiElementKind.of(element).getKind();
    }

    @Override
    public Set<Modifier> getModifiers() {
        if (!(element instanceof PsiModifierListOwner pmlo)) {
            return Set.of();
        }
        PsiModifierList modifierList = pmlo.getModifierList();
        if (modifierList == null) {
            return Set.of();
        }
        return Arrays.stream(PsiModifier.MODIFIERS)
                .filter(modifierList::hasModifierProperty)
                .filter(modifierList::hasExplicitModifier)
                .map(s -> s.replace('-', '_').toUpperCase())
                .map(Modifier::valueOf)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Element getEnclosingElement() {
        PsiElement enclosingElement = element instanceof PsiMember member
                ? member.getContainingClass()
                : element.getParent();
        return enclosingElement == null ? null : PsiElementAdapter.of(enclosingElement);
    }

    @Override
    public List<? extends Element> getEnclosedElements() {
        return Arrays.stream(element.getChildren())
                .map(PsiElementAdapter::of)
                .map(Element.class::cast)
                .toList();
    }

    @Override
    public List<? extends AnnotationMirror> getAnnotationMirrors() {
        if (!(element instanceof PsiModifierListOwner annotated)) {
            return List.of();
        }
        return Arrays.stream(annotated.getAnnotations())
                .map(PsiAnnotationMirrorAdapter::new)
                .toList();
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        if (!(element instanceof PsiModifierListOwner annotated)) {
            return null;
        }
        return new PsiAnnotationAdapter<>(annotated.getAnnotation(annotationType.getName()), annotationType).wrap();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType) {
        if (!(element instanceof PsiModifierListOwner annotated)) {
            return (A[]) Array.newInstance(annotationType, 0);
        }
        return Arrays.stream(annotated.getAnnotations())
                .filter(anno -> anno.hasQualifiedName(annotationType.getName()))
                .map(anno -> new PsiAnnotationAdapter<>(anno, annotationType))
                .map(PsiAnnotationAdapter::wrap)
                .toArray(s -> (A[]) Array.newInstance(annotationType, s));
    }

}
