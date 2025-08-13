package dev.kierat.plugins.openjml.utils.generator.adapter.element;

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
        return switch (value) {
            case PsiLiteralExpression literal -> literal.getValue();
            case PsiClassObjectAccessExpression object -> {
                PsiClass cls = Objects.requireNonNull(PsiTypesUtil.getPsiClass(object.getOperand().getType()));
                yield Class.forName(cls.getQualifiedName());
            }
            case PsiReferenceExpression ref -> {
                PsiElement resolved = ref.resolve();
                if (resolved instanceof PsiField constant) {
                    PsiExpression initializer = constant.getInitializer();
                    if (initializer instanceof PsiLiteralExpression literal) {
                        yield literal.getValue();
                    }
                    throw new UnsupportedOperationException("Unsupported field expression:" + constant);
                }
                throw new UnsupportedOperationException("Unsupported constant type: " + ref);
            }
            case PsiAnnotation anno -> throw new UnsupportedOperationException("not implemented");
            case PsiArrayInitializerMemberValue array -> throw new UnsupportedOperationException("not implemented");
            case null, default -> throw new UnsupportedOperationException("Unknown annotation value type");
        };
    }

}
