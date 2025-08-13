package dev.kierat.plugins.openjml.utils.generator.adapter.type;

import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeVisitor;
import java.lang.annotation.Annotation;
import java.util.List;

public class PsiNoTypeAdapter implements NoType {

    @NotNull
    private final PsiTypeKind kind;

    public PsiNoTypeAdapter(@NotNull PsiTypeKind kind) {
        this.kind = kind;
    }

    @Override
    public TypeKind getKind() {
        return kind.getKind();
    }

    @Override
    public List<? extends AnnotationMirror> getAnnotationMirrors() {
        return List.of();
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return null;
    }

    @Override
    public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType) {
        return null;
    }

    @Override
    public <R, P> R accept(TypeVisitor<R, P> v, P p) {
        return v.visitNoType(this, p);
    }
}
