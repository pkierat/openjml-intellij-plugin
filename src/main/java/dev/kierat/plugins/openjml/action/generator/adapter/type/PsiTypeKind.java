package dev.kierat.plugins.openjml.action.type;

import com.intellij.psi.*;
import com.intellij.psi.util.PsiTypesUtil;

import javax.lang.model.type.TypeKind;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

import static com.intellij.psi.PsiTypes.*;

public enum PsiTypeKind {
    BOOLEAN(booleanType()),
    BYTE(byteType()),
    SHORT(shortType()),
    INT(intType()),
    LONG(longType()),
    CHAR(charType()),
    FLOAT(floatType()),
    DOUBLE(doubleType()),
    VOID(voidType()),
    NULL(nullType()),
    NONE(Objects::isNull),
    ARRAY(PsiArrayType.class::isInstance),
    DECLARED(PsiClassType.class::isInstance),
    TYPEVAR(PsiTypeVariable.class::isInstance),
    WILDCARD(PsiWildcardType.class::isInstance),
    EXECUTABLE(PsiMethod.class::isInstance),
    UNION(PsiDisjunctionType.class::isInstance),
    INTERSECTION(PsiIntersectionType.class::isInstance),
    PACKAGE(t -> false),
    MODULE(t -> false),
    ERROR(t -> false),
    OTHER(t -> true);

    private final Predicate<PsiType> condition;

    PsiTypeKind(PsiType type) {
        this(t -> PsiTypesUtil.compareTypes(t, type, false));
    }

    PsiTypeKind(Predicate<PsiType> condition) {
        this.condition = condition;
    }

    public TypeKind getKind() {
        return TypeKind.valueOf(name());
    }

    public static PsiTypeKind of(PsiType type) {
        return Arrays.stream(values())
                .filter(v -> v.condition.test(type))
                .findFirst()
                .orElseThrow();
    }


}
