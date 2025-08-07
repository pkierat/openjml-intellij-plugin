package dev.kierat.plugins.openjml.inspection;

import com.intellij.codeInsight.daemon.impl.HighlightVisitor;
import com.intellij.codeInsight.daemon.impl.analysis.HighlightInfoHolder;
import com.intellij.codeInspection.ex.GlobalInspectionContextBase;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.psi.*;
import dev.kierat.plugins.openjml.psi.PsiJMLFile;
import org.jetbrains.annotations.NotNull;

public class JMLHighlightVisitor extends JavaElementVisitor implements HighlightVisitor {

    @Override
    public boolean analyze(@NotNull PsiFile file,
                           boolean updateWholeFile,
                           @NotNull HighlightInfoHolder holder,
                           @NotNull Runnable highlight) {

        if (updateWholeFile) {
            GlobalInspectionContextBase.assertUnderDaemonProgress();
            highlight.run();
            ProgressManager.checkCanceled();
        } else {
            highlight.run();
        }
        return true;
    }

    @Override
    public void visit(@NotNull PsiElement element) {
        element.accept(this);
    }

    @Override
    public @NotNull JMLHighlightVisitor clone() {
        return new JMLHighlightVisitor();
    }

    @Override
    public boolean suitableForFile(@NotNull PsiFile file) {
        return file instanceof PsiJMLFile;
    }
}