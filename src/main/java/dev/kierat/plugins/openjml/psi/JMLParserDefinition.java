package dev.kierat.plugins.openjml.psi;

import com.intellij.lang.ASTFactory;
import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lang.java.JavaParserDefinition;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.LanguageLevelProjectExtension;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import dev.kierat.plugins.openjml.psi.impl.PsiJMLFileImpl;
import dev.kierat.plugins.openjml.psi.impl.PsiJMLSpecificationImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JMLParserDefinition extends ASTFactory implements ParserDefinition {

    public static final IFileElementType JML_FILE = new IFileElementType(JMLLanguage.INSTANCE);

    private final JavaParserDefinition javaParserDefinition = new JavaParserDefinition();

    @Override
    public @NotNull Lexer createLexer(@Nullable Project project) {
        return javaParserDefinition.createLexer(project);
    }

    private static LanguageLevel getLanguageLevel(@Nullable Project project) {
        return project != null
                ? LanguageLevelProjectExtension.getInstance(project).getLanguageLevel()
                : LanguageLevel.HIGHEST;
    }

    @Override
    public @NotNull PsiParser createParser(@NotNull Project project) {
        return new JMLParser(getLanguageLevel(project));
    }

    @Override
    public @NotNull IFileElementType getFileNodeType() {
        return JML_FILE;
    }

    @Override
    public @NotNull TokenSet getCommentTokens() {
        return javaParserDefinition.getCommentTokens();
    }

    @Override
    public @NotNull PsiJMLFile createFile(@NotNull FileViewProvider viewProvider) {
        return new PsiJMLFileImpl(viewProvider);
    }

    @Override
    public @NotNull PsiElement createElement(ASTNode node) {
        return javaParserDefinition.createElement(node);
    }

    @Override
    public @NotNull SpaceRequirements spaceExistenceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return javaParserDefinition.spaceExistenceTypeBetweenTokens(left, right);
    }

    @Override
    public @NotNull TokenSet getStringLiteralElements() {
        return javaParserDefinition.getStringLiteralElements();
    }

    @Override
    public LeafElement createLeaf(@NotNull IElementType type, @NotNull CharSequence text) {
        if ((type == JavaTokenType.END_OF_LINE_COMMENT && text.charAt(2) == '@')
                || (type == JavaTokenType.C_STYLE_COMMENT && text.charAt(2) == '@')) {
            return new PsiJMLSpecificationImpl(PsiJMLSpecification.Type.of(type), text);
        }
        return null;
    }
}
