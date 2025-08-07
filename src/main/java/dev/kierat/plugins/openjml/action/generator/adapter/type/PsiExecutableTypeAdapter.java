package dev.kierat.plugins.openjml.action.type;

import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.*;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class PsiExecutableTypeAdapter extends PsiTypeMirrorAdapter implements ExecutableType {

    private final PsiMethod method;

    public PsiExecutableTypeAdapter(@NotNull PsiMethod method) {
        super(new PsiMethodType(method.getAnnotations()));
        this.method = method;
    }

    @Override
    public TypeKind getKind() {
        return TypeKind.EXECUTABLE;
    }

    @Override
    public List<? extends TypeMirror> getParameterTypes() {
        List<TypeMirror> types = new ArrayList<>();
        for (PsiParameter param : method.getParameterList().getParameters()) {
            types.add(PsiTypeMirrorAdapter.of(param.getType()));
        }
        return types;
    }

    @Override
    public TypeMirror getReturnType() {
        return method.isConstructor()
                ? new PsiNoTypeAdapter(PsiTypeKind.VOID)
                : PsiTypeMirrorAdapter.of(method.getReturnType());
    }

    @Override
    public List<? extends TypeMirror> getThrownTypes() {
        List<TypeMirror> types = new ArrayList<>();
        for (PsiClassType ex : method.getThrowsList().getReferencedTypes()) {
            types.add(PsiTypeMirrorAdapter.of(ex));
        }
        return types;
    }

    @Override
    public List<? extends TypeVariable> getTypeVariables() {
        return Arrays.stream(method.getTypeParameters())
                .map(PsiTypeVariableAdapter::new).toList();
    }

    @Override
    public TypeMirror getReceiverType() {
        if (method.hasModifierProperty(PsiModifier.STATIC)) {
            return null;
        }
        return Optional.ofNullable(method.getContainingClass())
                .map(clazz -> JavaPsiFacade.getElementFactory(clazz.getProject()).createType(clazz))
                .map(PsiTypeMirrorAdapter::of)
                .orElse(null);
    }

    @Override
    public <R, P> R accept(TypeVisitor<R, P> visitor, P param) {
        return visitor.visitExecutable(this, param);
    }

    @Override
    public String toString() {
        return method.getSignature(PsiSubstitutor.EMPTY).toString();
    }

    // NOT IMPLEMENTED

    @Override
    public List<? extends AnnotationMirror> getAnnotationMirrors() {
        return List.of();
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return null;
    }

    @Override
    public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType) {
        return null;
    }

    static class PsiMethodType extends PsiType {

        protected PsiMethodType(PsiAnnotation @NotNull [] annotations) {
            super(annotations);
        }

        @Override
        public @NotNull String getPresentableText() {
            return getCanonicalText();
        }

        @Override
        public @NotNull String getCanonicalText() {
            return "<method type>";
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public boolean equalsToText(@NotNull @NonNls String text) {
            return false;
        }

        @Override
        public <A> A accept(@NotNull PsiTypeVisitor<A> visitor) {
            return visitor.visitType(this);
        }

        @Override
        public @Nullable GlobalSearchScope getResolveScope() {
            return null;
        }

        @Override
        public PsiType @NotNull [] getSuperTypes() {
            return PsiType.EMPTY_ARRAY;
        }
    }
}
