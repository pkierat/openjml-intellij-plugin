package dev.kierat.plugins.openjml.psi;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.lang.java.parser.JavaParser;
import com.intellij.lang.java.parser.JavaParserUtil;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public class JMLParser implements PsiParser {

    private final JavaParser javaParser = new JavaParser();
    private final LanguageLevel languageLevel;

    public JMLParser(LanguageLevel languageLevel) {
        this.languageLevel = languageLevel;
    }

    @Override
    public @NotNull ASTNode parse(@NotNull IElementType root, @NotNull PsiBuilder builder) {
        JavaParserUtil.setLanguageLevel(builder, languageLevel);
        PsiBuilder.Marker fileMarker = builder.mark();
        javaParser.getFileParser().parse(builder);

        while (!builder.eof()) {
            builder.advanceLexer();
        }

        fileMarker.done(root);
        return builder.getTreeBuilt();
    }
}
