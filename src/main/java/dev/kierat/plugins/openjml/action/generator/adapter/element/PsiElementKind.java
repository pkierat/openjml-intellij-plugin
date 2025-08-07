package dev.kierat.plugins.openjml.action.element;

import com.intellij.psi.*;

import javax.lang.model.element.*;
import java.util.Arrays;
import java.util.function.Predicate;

import static java.util.function.Predicate.not;

public enum PsiElementKind {
    MODULE(ModuleElement.class, PsiJavaModule.class),
    PACKAGE(PackageElement.class, PsiPackage.class),

    RECORD(TypeElement.class, PsiClass.class, PsiClass::isRecord),
    ENUM(TypeElement.class, PsiClass.class, PsiClass::isEnum),
    ANNOTATION_TYPE(TypeElement.class, PsiClass.class, PsiClass::isAnnotationType),
    INTERFACE(TypeElement.class, PsiClass.class, PsiClass::isInterface),
    CLASS(TypeElement.class, PsiClass.class, PsiElementKind::isClass),

    ENUM_CONSTANT(VariableElement.class, PsiEnumConstant.class),
    FIELD(VariableElement.class, PsiField.class),
    LOCAL_VARIABLE(VariableElement.class, PsiLocalVariable.class),
    EXCEPTION_PARAMETER(VariableElement.class, PsiParameter.class, pp -> pp.getParent() instanceof PsiCatchSection),
    PARAMETER(VariableElement.class, PsiParameter.class, pp -> !(pp.getParent() instanceof PsiCatchSection)),
    RESOURCE_VARIABLE(VariableElement.class, PsiResourceVariable.class),
    BINDING_VARIABLE(VariableElement.class, PsiPatternVariable.class),

    RECORD_COMPONENT(RecordComponentElement.class, PsiRecordComponent.class),

    METHOD(ExecutableElement.class, PsiMethod.class, not(PsiMethod::isConstructor)),
    CONSTRUCTOR(ExecutableElement.class, PsiMethod.class, PsiMethod::isConstructor),
    STATIC_INIT(ExecutableElement.class, PsiClassInitializer.class, pci -> pci.hasModifierProperty(PsiModifier.STATIC)),
    INSTANCE_INIT(ExecutableElement.class, PsiClassInitializer.class, pci -> !pci.hasModifierProperty(PsiModifier.STATIC)),

    TYPE_PARAMETER(TypeParameterElement.class, PsiTypeParameter.class),

    OTHER(Element.class, PsiElement.class);

    private final Class<? extends Element> elementType;
    private final Predicate<PsiElement> condition;

    <T> PsiElementKind(Class<? extends Element> elementType, Class<T> clazz) {
        this(elementType, clazz, clazz::isInstance);
    }

    <T> PsiElementKind(Class<? extends Element> elementType, Class<T> clazz, Predicate<T> condition) {
        this.elementType = elementType;
        this.condition = e -> clazz.isInstance(e) && condition.test(clazz.cast(e));
    }

    public ElementKind getKind() {
        return ElementKind.valueOf(name());
    }

    public static PsiElementKind of(PsiElement element) {
        return Arrays.stream(values())
                .filter(v -> v.condition.test(element))
                .findFirst()
                .orElseThrow();
    }

    static boolean isClass(PsiClass element) {
        return !(element.isInterface() || element.isRecord() || element.isEnum() || element.isAnnotationType());
    }

    public Class<? extends Element> elementType() {
        return elementType;
    }
}
