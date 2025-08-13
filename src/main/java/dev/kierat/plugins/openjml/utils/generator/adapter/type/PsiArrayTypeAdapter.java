package dev.kierat.plugins.openjml.utils.generator.adapter.type;

import com.intellij.psi.PsiArrayType;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import java.lang.annotation.Annotation;
import java.util.List;

class PsiArrayTypeAdapter extends PsiTypeMirrorAdapter implements ArrayType {

    protected final PsiArrayType type;

    public PsiArrayTypeAdapter(PsiArrayType type) {
        super(type);
        this.type = type;
    }

    @Override
    public TypeMirror getComponentType() {
        return PsiTypeMirrorAdapter.of(type.getComponentType());
    }

    @Override
    public TypeKind getKind() {
        return TypeKind.ARRAY;
    }

    @Override
    public <R, P> R accept(TypeVisitor<R, P> v, P p) {
        return v.visitArray(this, p);
    }

    // NOT IMPLEMENTED

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
}
