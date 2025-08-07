package dev.kierat.plugins.openjml.action.type;

import com.intellij.psi.PsiClassType;
import dev.kierat.plugins.openjml.action.element.PsiTypeElementAdapter;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

public class PsiDeclaredTypeAdapter extends PsiTypeMirrorAdapter implements DeclaredType {

    protected final PsiClassType type;

    public PsiDeclaredTypeAdapter(PsiClassType type) {
        super(type);
        this.type = type;
    }

    @Override
    public TypeKind getKind() {
        return TypeKind.DECLARED;
    }

    @Override
    public Element asElement() {
        return new PsiTypeElementAdapter(type.resolve());
    }

    @Override
    public TypeMirror getEnclosingType() {
        return asElement().getEnclosingElement().asType();
    }

    @Override
    public <R, P> R accept(TypeVisitor<R, P> v, P p) {
        return v.visitDeclared(this, p);
    }

    @Override
    public List<? extends TypeMirror> getTypeArguments() {
        return Arrays.stream(type.getParameters())
                .map(PsiTypeMirrorAdapter::of)
                .toList();
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
