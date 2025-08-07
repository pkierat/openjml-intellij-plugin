package dev.kierat.plugins.openjml.action.element;

import com.intellij.psi.PsiPackage;
import dev.kierat.plugins.openjml.action.type.PsiNoTypeAdapter;
import dev.kierat.plugins.openjml.action.type.PsiTypeKind;

import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.type.TypeMirror;

class PsiPackageElementAdapter extends PsiElementAdapter<PsiPackage> implements PackageElement {

    protected PsiPackageElementAdapter(PsiPackage element) {
        super(element);
    }

    @Override
    public TypeMirror asType() {
        return new PsiNoTypeAdapter(PsiTypeKind.PACKAGE);
    }

    @Override
    public Name getQualifiedName() {
        return new PsiNameAdapter(element.getQualifiedName());
    }

    @Override
    public Name getSimpleName() {
        return new PsiNameAdapter(element.getName());
    }

    @Override
    public boolean isUnnamed() {
        return element.getQualifiedName().isEmpty();
    }

    @Override
    public <R, P> R accept(ElementVisitor<R, P> v, P p) {
        return v.visitPackage(this, p);
    }
}
