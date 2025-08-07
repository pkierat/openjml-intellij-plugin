package dev.kierat.plugins.openjml.action.type;

import com.intellij.psi.PsiIntersectionType;

import javax.lang.model.type.IntersectionType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import java.util.Arrays;
import java.util.List;

class PsiIntersectionTypeAdapter extends PsiTypeMirrorAdapter implements IntersectionType {

    public PsiIntersectionTypeAdapter(PsiIntersectionType type) {
        super(type);
    }

    @Override
    public TypeKind getKind() {
        return TypeKind.INTERSECTION;
    }

    @Override
    public List<? extends TypeMirror> getBounds() {
        return Arrays.stream(type.getSuperTypes())
                .map(PsiTypeMirrorAdapter::of)
                .toList();
    }

    @Override
    public <R, P> R accept(TypeVisitor<R, P> v, P p) {
        return v.visitIntersection(this, p);
    }
}
