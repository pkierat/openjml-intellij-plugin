package dev.kierat.plugins.openjml.action.type;

import com.intellij.psi.PsiPrimitiveType;
import org.jetbrains.annotations.NotNull;

import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeVisitor;

class PsiPrimitiveTypeAdapter extends PsiTypeMirrorAdapter implements PrimitiveType {

    private final @NotNull String name;

    protected PsiPrimitiveTypeAdapter(PsiPrimitiveType type) {
        super(type);
        this.name = type.getName();
    }

    @Override
    public <R, P> R accept(TypeVisitor<R, P> v, P p) {
        return v.visitPrimitive(this, p);
    }

    @Override
    public String toString() {
        return name;
    }
}
