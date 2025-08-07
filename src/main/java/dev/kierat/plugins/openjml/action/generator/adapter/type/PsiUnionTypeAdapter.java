package dev.kierat.plugins.openjml.action.type;

import com.intellij.psi.PsiDisjunctionType;
import com.intellij.psi.PsiType;
import org.jetbrains.annotations.NotNull;

import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.type.UnionType;
import java.util.List;

class PsiUnionTypeAdapter extends PsiTypeMirrorAdapter implements UnionType {

    private final @NotNull List<PsiType> disjunctions;

    public PsiUnionTypeAdapter(PsiDisjunctionType type) {
        super(type);
        disjunctions = type.getDisjunctions();
    }

    @Override
    public List<? extends TypeMirror> getAlternatives() {
        return disjunctions.stream()
                .map(PsiTypeMirrorAdapter::of)
                .toList();
    }

    @Override
    public <R, P> R accept(TypeVisitor<R, P> v, P p) {
        return v.visitUnion(this, p);
    }
}
