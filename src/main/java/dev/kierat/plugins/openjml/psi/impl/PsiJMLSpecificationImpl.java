package dev.kierat.plugins.openjml.psi.impl;

import com.intellij.lang.Language;
import com.intellij.psi.impl.source.tree.PsiCommentImpl;
import dev.kierat.plugins.openjml.psi.JMLLanguage;
import dev.kierat.plugins.openjml.psi.PsiJMLSpecification;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;

public class PsiJMLSpecificationImpl extends PsiCommentImpl implements PsiJMLSpecification {

    //TODO: Parse specifications

    public PsiJMLSpecificationImpl(@NotNull Type type) {
        super(type.commentType, type.emptyText);
    }

    public PsiJMLSpecificationImpl(@NotNull Type type, CharSequence text) {
        super(type.commentType, text);
    }

    @Override
    public @NotNull Language getLanguage() {
        return JMLLanguage.INSTANCE;
    }

    @Override
    public String getPresentedText() {
        return Arrays.stream(getText().split("\n"))
                .map(line -> line.replaceFirst("(^ *@\\*/|^ */\\*@ ?|^ *@ ?|//@ ?)", ""))
                .filter(not(String::isBlank))
                .collect(joining("\n"));
    }

    @Override
    public String toString() {
        return "PsiJMLSpecification(" + getElementType() + ")";
    }
}