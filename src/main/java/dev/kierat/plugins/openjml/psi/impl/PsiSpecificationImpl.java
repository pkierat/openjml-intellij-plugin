package dev.kierat.plugins.openjml.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.psi.impl.source.tree.PsiCommentImpl;
import dev.kierat.plugins.openjml.psi.JMLLanguage;
import dev.kierat.plugins.openjml.psi.PsiSpecification;
import org.jetbrains.annotations.NotNull;

public class PsiSpecificationImpl extends PsiCommentImpl implements PsiSpecification {

    //TODO: Parse specifications

    @NotNull
    private final ASTNode node;

    public PsiSpecificationImpl(@NotNull ASTNode node) {
        super(node.getElementType(), node.getText());
        this.node = node;
    }

    @Override
    public @NotNull Language getLanguage() {
        return JMLLanguage.INSTANCE;
    }

}