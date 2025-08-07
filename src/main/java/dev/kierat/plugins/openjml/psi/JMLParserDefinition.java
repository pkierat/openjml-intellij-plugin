package dev.kierat.plugins.openjml.psi;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lang.java.JavaParserDefinition;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.LanguageLevelProjectExtension;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.psi.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import dev.kierat.plugins.openjml.psi.impl.PsiJMLFileImpl;
import dev.kierat.plugins.openjml.psi.impl.PsiSpecificationImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JMLParserDefinition implements ParserDefinition {

    public static final IFileElementType JML_FILE = new IFileElementType(JMLLanguage.INSTANCE);

    private final JavaParserDefinition javaParserDefinition = new JavaParserDefinition();

    @Override
    public @NotNull Lexer createLexer(@Nullable Project project) {
        LanguageLevel level = getLanguageLevel(project);
        return createLexer(level);
    }

    private static LanguageLevel getLanguageLevel(@Nullable Project project) {
        return project != null
                ? LanguageLevelProjectExtension.getInstance(project).getLanguageLevel()
                : LanguageLevel.HIGHEST;
    }

    public static @NotNull Lexer createLexer(@NotNull LanguageLevel level) {
        return new JMLAwareJavaLexer(level);
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
        return TokenSet.create(
                JavaTokenType.END_OF_LINE_COMMENT,
                JavaTokenType.C_STYLE_COMMENT,
                JMLTokenTypes.JML_LINE_COMMENT,
                JMLTokenTypes.JML_BLOCK_COMMENT
        );
    }

    @Override
    public @NotNull PsiJMLFile createFile(@NotNull FileViewProvider viewProvider) {
        return new PsiJMLFileImpl(viewProvider);
    }

    @Override
    public @NotNull PsiElement createElement(@NotNull ASTNode node) {
        //TODO: Make the parser call this method
        IElementType type = node.getElementType();
        if (type == JMLTokenTypes.JML_LINE_COMMENT || type == JMLTokenTypes.JML_BLOCK_COMMENT) {
            return new PsiSpecificationImpl(node);
        } else {
            PsiElement element = javaParserDefinition.createElement(node);
            if (element instanceof PsiClass psiClass) {
                return PsiSpecificationOwner.of(psiClass, PsiClass.class, null);
            } else if (element instanceof PsiField psiField) {
                return PsiSpecificationOwner.of(psiField, PsiField.class, null);
            } else if (element instanceof PsiMethod psiMethod) {
                return PsiSpecificationOwner.of(psiMethod, PsiMethod.class, null);
            } else {
                return element;
            }
        }
    }

    @Override
    public @NotNull SpaceRequirements spaceExistenceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return javaParserDefinition.spaceExistenceTypeBetweenTokens(left, right);
    }

    @Override
    public @NotNull TokenSet getStringLiteralElements() {
        return javaParserDefinition.getStringLiteralElements();
    }
}
