package dev.kierat.plugins.openjml.psi;

import com.intellij.psi.PsiElement;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public interface PsiSpecificationOwner extends PsiElement {

    PsiJMLSpecification getSpecification();

    static <E extends PsiElement> E of(E element, Class<E> elementInterface, PsiJMLSpecification specification) {

        return elementInterface.cast(Proxy.newProxyInstance(
                PsiSpecificationOwner.class.getClassLoader(),
                new Class<?>[]{ elementInterface },
                new PsiSpecificationAwareElementImpl(element, specification))
        );
    }

    class PsiSpecificationAwareElementImpl implements InvocationHandler {

        private final PsiElement psiElement;
        private final PsiJMLSpecification psiSpecification;

        public PsiSpecificationAwareElementImpl(PsiElement psiElement, PsiJMLSpecification psiSpecification) {
            this.psiElement = psiElement;
            this.psiSpecification = psiSpecification;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("getSpecification")) {
                return psiSpecification;
            } else {
                return method.invoke(psiElement, args);
            }
        }
    }

}
