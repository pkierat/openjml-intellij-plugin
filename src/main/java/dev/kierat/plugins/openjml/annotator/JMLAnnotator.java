package dev.kierat.plugins.openjml.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import dev.kierat.plugins.openjml.psi.PsiJMLFile;
import org.jetbrains.annotations.NotNull;

public class JMLAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        PsiFile file = element.getContainingFile();
        if (!(file instanceof PsiJMLFile)) return;

        //TODO: implement me
    }

}