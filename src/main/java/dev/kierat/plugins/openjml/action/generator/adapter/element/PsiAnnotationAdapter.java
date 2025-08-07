package dev.kierat.plugins.openjml.action.element;

import com.intellij.psi.*;
import com.intellij.psi.util.PsiTypesUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.Objects;

public record PsiAnnotationAdapter<A extends Annotation>(PsiAnnotation annotation, Class<A> annotationType) {

    public A wrap() {
        return annotationType.cast(Proxy.newProxyInstance(
                PsiAnnotationAdapter.class.getClassLoader(),
                new Class<?>[]{annotationType},
                (proxy, method, args) -> {
                    PsiAnnotationMemberValue attributeValue = annotation.findAttributeValue(method.getName());
                    return getValue(attributeValue);
                }));
    }

    private Object getValue(PsiAnnotationMemberValue value) throws ClassNotFoundException {
        if (value instanceof PsiLiteralExpression literal) {
            return literal.getValue();
        } else if (value instanceof PsiClassObjectAccessExpression object) {
            PsiClass cls = Objects.requireNonNull(PsiTypesUtil.getPsiClass(object.getOperand().getType()));
            return Class.forName(cls.getQualifiedName());
        } else if (value instanceof PsiReferenceExpression ref) {
            PsiElement resolved = ref.resolve();
            if (resolved instanceof PsiField constant) {
                PsiExpression initializer = constant.getInitializer();
                if (initializer instanceof PsiLiteralExpression literal) {
                    return literal.getValue();
                }
                throw new UnsupportedOperationException("Unsupported field expression:" + constant);
            }
            throw new UnsupportedOperationException("Unsupported constant type: " + ref);
        } else if (value instanceof PsiAnnotation anno) {
            throw new UnsupportedOperationException("not implemented");
        } else if (value instanceof PsiArrayInitializerMemberValue array) {
            throw new UnsupportedOperationException("not implemented");
        } else {
            throw new UnsupportedOperationException("Unknown annotation value type");
        }
    }

}
