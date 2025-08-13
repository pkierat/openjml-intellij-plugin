package dev.kierat.plugins.openjml.utils.generator.adapter.element;

import com.intellij.psi.PsiAnnotation;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;
import java.util.Map;

public class PsiAnnotationMirrorAdapter implements AnnotationMirror {

    private final PsiAnnotation annotation;

    public PsiAnnotationMirrorAdapter(PsiAnnotation annotation) {
        this.annotation = annotation;
    }

    @Override
    public DeclaredType getAnnotationType() {
        return null; // not implemented
    }

    @Override
    public Map<? extends ExecutableElement, ? extends AnnotationValue> getElementValues() {
        return Map.of(); // not implemented
    }
}
