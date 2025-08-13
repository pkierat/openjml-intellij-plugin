package dev.kierat.plugins.openjml.utils.generator.adapter.element;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTypesUtil;
import dev.kierat.plugins.openjml.utils.generator.adapter.type.PsiDeclaredTypeAdapter;
import dev.kierat.plugins.openjml.utils.generator.adapter.type.PsiNoTypeAdapter;
import dev.kierat.plugins.openjml.utils.generator.adapter.type.PsiTypeKind;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PsiTypeElementAdapter extends PsiElementAdapter<PsiClass> implements TypeElement {

    public PsiTypeElementAdapter(PsiClass element) {
        super(element);
    }

    @Override
    public NestingKind getNestingKind() {
        return PsiNestingKind.of(element).getKind();
    }

    @Override
    public Name getQualifiedName() {
        return new PsiNameAdapter(element.getQualifiedName());
    }

    @Override
    public TypeMirror getSuperclass() {
        PsiClass superClass = element.getSuperClass();
        if (superClass == null) {
            return new PsiNoTypeAdapter(PsiTypeKind.NONE);
        }
        return new PsiDeclaredTypeAdapter(PsiTypesUtil.getClassType(superClass));
    }

    @Override
    public List<? extends TypeMirror> getInterfaces() {
        return Arrays.stream(element.getInterfaces())
                .map(PsiTypesUtil::getClassType)
                .map(PsiDeclaredTypeAdapter::new)
                .toList();
    }

    @Override
    public List<? extends Element> getEnclosedElements() {
        return getMembers().stream()
                .map(PsiElementAdapter::of)
                .map(Element.class::cast)
                .toList();
    }

    private List<PsiElement> getMembers() {
        List<PsiElement> members = new ArrayList<>();
        members.addAll(List.of(element.getFields()));
        members.addAll(List.of(element.getMethods()));
        members.addAll(List.of(element.getInnerClasses()));
        return members;
    }

    @Override
    public List<? extends TypeParameterElement> getTypeParameters() {
        return List.of();
    }

    @Override
    public List<? extends RecordComponentElement> getRecordComponents() {
        if (element.isRecord()) {
            return Arrays.stream(element.getRecordComponents())
                    .map(PsiRecordComponentElementAdapter::new)
                    .toList();
        }
        return List.of();
    }

    @Override
    public List<? extends TypeMirror> getPermittedSubclasses() {
        return Arrays.stream(element.getPermitsListTypes())
                .map(PsiDeclaredTypeAdapter::new)
                .toList();
    }

    @Override
    public TypeMirror asType() {
        return new PsiDeclaredTypeAdapter(PsiTypesUtil.getClassType(element));
    }

    @Override
    public Name getSimpleName() {
        return new PsiNameAdapter(element.getName());
    }

    @Override
    public <R, P> R accept(ElementVisitor<R, P> v, P p) {
        return v.visitType(this, p);
    }
}
