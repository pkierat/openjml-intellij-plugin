package dev.kierat.plugins.openjml.action.element;

import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import dev.kierat.plugins.openjml.action.type.PsiExecutableTypeAdapter;
import dev.kierat.plugins.openjml.action.type.PsiTypeMirrorAdapter;

import javax.lang.model.element.*;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class PsiExecutableElementAdapter extends PsiElementAdapter<PsiMethod> implements ExecutableElement {

    protected PsiExecutableElementAdapter(PsiMethod element) {
        super(element);
    }

    @Override
    public ExecutableType asType() {
        return new PsiExecutableTypeAdapter(element);
    }

    @Override
    public List<? extends TypeParameterElement> getTypeParameters() {
        return Arrays.stream(element.getTypeParameters())
                .map(PsiTypeParameterElementAdapter::new)
                .toList();
    }

    @Override
    public TypeMirror getReturnType() {
        return PsiTypeMirrorAdapter.of(element.getReturnType());
    }

    @Override
    public List<? extends VariableElement> getParameters() {
        return Arrays.stream(element.getParameterList().getParameters())
                .map(PsiVariableElementAdapter::new)
                .toList();
    }

    @Override
    public TypeMirror getReceiverType() {
        if (element.hasModifierProperty(PsiModifier.STATIC)) {
            return null;
        }
        return Optional.ofNullable(element.getContainingClass())
                .map(clazz -> JavaPsiFacade.getElementFactory(clazz.getProject()).createType(clazz))
                .map(PsiTypeMirrorAdapter::of)
                .orElse(null);
    }

    @Override
    public boolean isVarArgs() {
        return element.isVarArgs();
    }

    @Override
    public boolean isDefault() {
        return element.getModifierList().hasModifierProperty(PsiModifier.DEFAULT);
    }

    @Override
    public Name getSimpleName() {
        return new PsiNameAdapter(element.getName());
    }

    @Override
    public List<? extends TypeMirror> getThrownTypes() {
        return Arrays.stream(element.getThrowsList().getReferencedTypes())
                .map(PsiTypeMirrorAdapter::of)
                .toList();
    }

    @Override
    public <R, P> R accept(ElementVisitor<R, P> v, P p) {
        return v.visitExecutable(this, p);
    }

    @Override
    public AnnotationValue getDefaultValue() {
        return null; // not implemented
    }
}
