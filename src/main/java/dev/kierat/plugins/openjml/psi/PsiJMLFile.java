package dev.kierat.plugins.openjml.psi;

import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.UnaryOperator;

public interface PsiJMLFile extends PsiFile, PsiImportHolder, PsiClassOwner {

    @Nullable PsiElement findElement(@NotNull PsiElement javaElement);

    @NotNull PsiElement findOrCreateElement(@NotNull PsiElement element,
                                            @NotNull UnaryOperator<PsiElement> jmlElementFactory);

}