package dev.kierat.plugins.openjml.utils.generator.adapter.type;

import com.intellij.psi.*;
import dev.kierat.plugins.openjml.utils.generator.adapter.element.PsiAnnotationMirrorAdapter;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public abstract class PsiTypeMirrorAdapter implements TypeMirror {

    protected final PsiType type;

    protected PsiTypeMirrorAdapter(PsiType type) {
        this.type = type;
    }

    public static PsiTypeMirrorAdapter of(PsiType type) {
        if (type instanceof PsiPrimitiveType ppt) { return new PsiPrimitiveTypeAdapter(ppt); }
        else if (type instanceof PsiArrayType pat) { return new PsiArrayTypeAdapter(pat); }
        else if (type instanceof PsiClassType pct) { return new PsiDeclaredTypeAdapter(pct); }
        else if (type instanceof PsiIntersectionType pit) { return new PsiIntersectionTypeAdapter(pit); }
        else if (type instanceof PsiDisjunctionType pdt) { return new PsiUnionTypeAdapter(pdt); }
        else if (type instanceof PsiWildcardType pwt) { return new PsiWildcardTypeAdapter(pwt); }
        else { return null; }
    }

    @Override
    public TypeKind getKind() {
        return PsiTypeKind.of(type).getKind();
    }

    @Override
    public List<? extends AnnotationMirror> getAnnotationMirrors() {
        return Arrays.stream(type.getAnnotations())
                .map(PsiAnnotationMirrorAdapter::new)
                .toList();
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return null; // not implemented
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType) {
        return (A[]) Array.newInstance(annotationType, 0); // not implemented
    }

}