package dev.kierat.plugins.openjml.utils.generator.adapter.element;

import com.intellij.psi.PsiTypeParameter;
import dev.kierat.plugins.openjml.utils.generator.adapter.type.PsiTypeVariableAdapter;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;

public class PsiTypeParameterElementAdapter extends PsiElementAdapter<PsiTypeParameter> implements TypeParameterElement {

    public PsiTypeParameterElementAdapter(PsiTypeParameter element) {
        super(element);
    }

    @Override
    public TypeMirror asType() {
        return new PsiTypeVariableAdapter(element);
    }

    @Override
    public Element getGenericElement() {
        return null;
    }

    @Override
    public List<? extends TypeMirror> getBounds() {
        return List.of();
    }

    @Override
    public Name getSimpleName() {
        return null;
    }

    @Override
    public <R, P> R accept(ElementVisitor<R, P> v, P p) {
        return v.visitTypeParameter(this, p);
    }
}
