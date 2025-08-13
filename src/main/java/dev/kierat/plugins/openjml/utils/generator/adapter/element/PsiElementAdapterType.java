package dev.kierat.plugins.openjml.utils.generator.adapter.element;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiModifierListOwner;

import javax.lang.model.element.*;
import java.util.Arrays;
import java.util.function.Function;

public enum PsiElementAdapterType {
    MODULE(ModuleElement.class, PsiModuleElementAdapter::new),
    PACKAGE(PackageElement.class, PsiPackageElementAdapter::new),
    TYPE(TypeElement.class, PsiTypeElementAdapter::new),
    VARIABLE(VariableElement.class, PsiVariableElementAdapter::new),
    EXECUTABLE(ExecutableElement.class, PsiExecutableElementAdapter::new),
    TYPE_PARAMETER(TypeParameterElement.class, PsiTypeParameterElementAdapter::new),
    RECORD_COMPONENT(RecordComponentElement.class, PsiRecordComponentElementAdapter::new),
    ;

    private final Class<? extends Element> elementType;
    private final Function<? extends PsiElement, ? extends Element> factory;

    <T extends Element, U extends PsiElement & PsiModifierListOwner, V extends PsiElementAdapter<U>> PsiElementAdapterType(Class<T> elementType, Function<U, V> factory) {
        this.elementType = elementType;
        this.factory = factory;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Element, U extends PsiElement, V extends PsiElementAdapter<U>> T wrap(U element) {
        PsiElementAdapterType adapterType = findFor(element);
        return (T) ((Function<U, V>) adapterType.factory).apply(element);
    }

    private static PsiElementAdapterType findFor(PsiElement element) {
        PsiElementKind kind = PsiElementKind.of(element);
        return Arrays.stream(values())
                .filter(type -> kind.elementType() == type.elementType)
                .findFirst()
                .orElseThrow();
    }
}
