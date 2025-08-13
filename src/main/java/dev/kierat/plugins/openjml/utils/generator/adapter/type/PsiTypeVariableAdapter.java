package dev.kierat.plugins.openjml.utils.generator.adapter.type;

import com.intellij.psi.*;
import dev.kierat.plugins.openjml.utils.generator.adapter.element.PsiTypeParameterElementAdapter;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.TypeVisitor;
import java.lang.annotation.Annotation;
import java.util.List;

public class PsiTypeVariableAdapter extends PsiTypeMirrorAdapter implements TypeVariable {

    private final PsiTypeParameter element;

    public PsiTypeVariableAdapter(PsiTypeParameter parameter) {
        super(PsiTypes.voidType());
        this.element = parameter;
    }

    @Override
    public TypeKind getKind() {
        return TypeKind.TYPEVAR;
    }

    @Override
    public <R, P> R accept(TypeVisitor<R, P> v, P p) {
        return v.visitTypeVariable(this, p);
    }

    @Override
    public Element asElement() {
        return new PsiTypeParameterElementAdapter(element);
    }

    @Override
    public TypeMirror getUpperBound() {
        PsiClassType[] bounds = element.getExtendsListTypes();
        if (bounds.length == 0) {
            return null;
        } else if (bounds.length == 1) {
            return PsiTypeMirrorAdapter.of(bounds[0]);
        } else {
            PsiIntersectionType intersection = (PsiIntersectionType) PsiIntersectionType.createIntersection(bounds);
            return PsiTypeMirrorAdapter.of(intersection);
        }
    }

    @Override
    public TypeMirror getLowerBound() {
        return null;
    }

    @Override
    public String toString() {
        return element.getName();
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
