package dev.kierat.plugins.openjml.action.element;

import com.intellij.psi.PsiJavaModule;
import com.intellij.psi.PsiModifier;
import dev.kierat.plugins.openjml.action.type.PsiNoTypeAdapter;
import dev.kierat.plugins.openjml.action.type.PsiTypeKind;

import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.ModuleElement;
import javax.lang.model.element.Name;
import javax.lang.model.type.TypeMirror;
import java.util.List;

class PsiModuleElementAdapter extends PsiElementAdapter<PsiJavaModule> implements ModuleElement {

    protected PsiModuleElementAdapter(PsiJavaModule element) {
        super(element);
    }

    @Override
    public TypeMirror asType() {
        return new PsiNoTypeAdapter(PsiTypeKind.MODULE);
    }

    @Override
    public Name getQualifiedName() {
        return new PsiNameAdapter(element.getName());
    }

    @Override
    public boolean isOpen() {
        return element.hasModifierProperty(PsiModifier.OPEN);
    }

    @Override
    public boolean isUnnamed() {
        return element.getName().isEmpty();
    }

    @Override
    public List<? extends Directive> getDirectives() {
        return List.of(); // not implemented
    }

    @Override
    public Name getSimpleName() {
        return getQualifiedName();
    }

    @Override
    public <R, P> R accept(ElementVisitor<R, P> v, P p) {
        return v.visitModule(this, p);
    }
}
