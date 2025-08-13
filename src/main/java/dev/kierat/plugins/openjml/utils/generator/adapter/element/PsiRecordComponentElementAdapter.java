package dev.kierat.plugins.openjml.utils.generator.adapter.element;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiRecordComponent;
import com.intellij.psi.PsiType;
import dev.kierat.plugins.openjml.utils.generator.adapter.type.PsiTypeMirrorAdapter;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.util.*;

public class PsiRecordComponentElementAdapter extends PsiElementAdapter<PsiRecordComponent> implements RecordComponentElement {

    public PsiRecordComponentElementAdapter(PsiRecordComponent element) {
        super(element);
    }

    @Override
    public TypeMirror asType() {
        return PsiTypeMirrorAdapter.of(element.getType());
    }

    @Override
    public ExecutableElement getAccessor() {
        String accessorName = element.getName();
        PsiType expectedType = element.getType();
        PsiClass psiClass = Objects.requireNonNull(element.getContainingClass());

        return Arrays.stream(psiClass.findMethodsByName(accessorName, false))
                .filter(method -> method.getParameterList().isEmpty())
                .filter(method -> method.getReturnType() != null)
                .filter(method -> method.getReturnType().equals(expectedType))
                .map(PsiExecutableElementAdapter::new)
                .findFirst().orElseThrow();
    }

    @Override
    public Name getSimpleName() {
        return new PsiNameAdapter(element.getName());
    }

    @Override
    public Set<Modifier> getModifiers() {
        return super.getModifiers();
    }

    @Override
    public <R, P> R accept(ElementVisitor<R, P> v, P p) {
        return v.visitRecordComponent(this, p);
    }

}
