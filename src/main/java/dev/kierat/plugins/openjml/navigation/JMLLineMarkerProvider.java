package dev.kierat.plugins.openjml.navigation;

import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.psi.*;
import dev.kierat.plugins.openjml.intention.AddJMLSpecificationIntention;
import dev.kierat.plugins.openjml.psi.PsiJMLSpecification;
import dev.kierat.plugins.openjml.utils.JMLElementUtil;
import dev.kierat.plugins.openjml.utils.JMLFileUtil;
import dev.kierat.plugins.openjml.utils.JMLSpecificationUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.event.MouseEvent;

public class JMLLineMarkerProvider implements LineMarkerProvider {

    @Override
    public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {
        if (!(element instanceof PsiMember
                && element instanceof PsiNameIdentifierOwner named
                && named.getNameIdentifier() != null)) {
            return null;
        }

        Module module = ModuleUtilCore.findModuleForPsiElement(element);
        if (module == null) { return null; }

        PsiFile jmlFile = JMLFileUtil.getJmlFile(module, element.getContainingFile());
        if (jmlFile == null) { return null; }

        PsiElement jmlElement = JMLElementUtil.findElement(jmlFile, element);
        if (jmlElement == null) { return null; }

        PsiJMLSpecification jmlSpec = JMLSpecificationUtil.getSpecification(jmlElement);
        if (jmlSpec == null) return null;

        return new LineMarkerInfo<>(
                named.getNameIdentifier(),
                element.getTextRange(),
                AllIcons.Nodes.Static,
                e -> jmlSpec.getPresentedText(),
                navigationHandler(jmlFile, jmlElement),
                GutterIconRenderer.Alignment.RIGHT,
                () -> "JML Specification"
        );
    }

    GutterIconNavigationHandler<PsiElement> navigationHandler(PsiFile file, PsiElement element) {
        return (MouseEvent e, PsiElement elt) -> AddJMLSpecificationIntention.openFileAtElement(file, element);
    }

}