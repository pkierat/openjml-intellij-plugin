package dev.kierat.plugins.openjml.action.element;

import com.intellij.psi.PsiVariable;
import dev.kierat.plugins.openjml.action.type.PsiTypeMirrorAdapter;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;

class PsiVariableElementAdapter extends PsiElementAdapter<PsiVariable> implements VariableElement {

    protected PsiVariableElementAdapter(PsiVariable element) {
        super(element);
    }

    @Override
    public TypeMirror asType() {
        return PsiTypeMirrorAdapter.of(element.getType());
    }

    @Override
    public Name getSimpleName() {
        return new PsiNameAdapter(element.getName());
    }

    @Override
    public Object getConstantValue() {
        return element.computeConstantValue();
    }

    @Override
    public <R, P> R accept(ElementVisitor<R, P> v, P p) {
        return v.visitVariable(this, p);
    }

    @Override
    public String toString() {
        return element.getName();
    }
}
