package dev.kierat.plugins.openjml.action.type;

import com.intellij.psi.PsiType;
import com.intellij.psi.PsiWildcardType;

import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.type.WildcardType;

class PsiWildcardTypeAdapter extends PsiTypeMirrorAdapter implements WildcardType {

    private final PsiType extendsBound;
    private final PsiType superBound;

    public PsiWildcardTypeAdapter(PsiWildcardType type) {
        super(type);
        this.extendsBound = type.getExtendsBound();
        this.superBound = type.getSuperBound();
    }

    @Override
    public TypeMirror getExtendsBound() {
        return PsiTypeMirrorAdapter.of(extendsBound);
    }

    @Override
    public TypeMirror getSuperBound() {
        return PsiTypeMirrorAdapter.of(superBound);
    }

    @Override
    public <R, P> R accept(TypeVisitor<R, P> v, P p) {
        return v.visitWildcard(this, p);
    }
}
