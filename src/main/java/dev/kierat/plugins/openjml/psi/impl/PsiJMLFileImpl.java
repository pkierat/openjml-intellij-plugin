package dev.kierat.plugins.openjml.psi.impl;

import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiJavaFileBaseImpl;
import dev.kierat.plugins.openjml.psi.JMLFileType;
import dev.kierat.plugins.openjml.psi.JMLLanguage;
import dev.kierat.plugins.openjml.psi.JMLParserDefinition;
import dev.kierat.plugins.openjml.psi.PsiJMLFile;
import dev.kierat.plugins.openjml.utils.JMLElementUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.UnaryOperator;

public class PsiJMLFileImpl extends PsiJavaFileBaseImpl implements PsiJMLFile {

    public PsiJMLFileImpl(@NotNull FileViewProvider viewProvider) {
        super(JMLParserDefinition.JML_FILE, JMLParserDefinition.JML_FILE, viewProvider);
    }

    @Override
    public @NotNull Language getLanguage() {
        return JMLLanguage.INSTANCE;
    }

    @Override
    public @NotNull FileType getFileType() {
        return JMLFileType.INSTANCE;
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        visitor.visitFile(this);
    }

    @Override
    public @Nullable PsiElement findElement(@NotNull PsiElement element) {
        return JMLElementUtil.findElement(this, element);
    }

    public @NotNull PsiElement findOrCreateElement(@NotNull PsiElement element,
                                                   @NotNull UnaryOperator<PsiElement> elementFactory) {
        return JMLElementUtil.findOrCreateElement(this, element, elementFactory);
    }

    @Override
    public String toString() {
        return "PsiJMLFile:" + getName();
    }

}