package dev.kierat.plugins.openjml.psi;

import com.intellij.lexer.LexerBase;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.java.lexer.JavaLexer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static dev.kierat.plugins.openjml.psi.JMLTokenTypes.*;

public class JMLAwareJavaLexer extends LexerBase {

    private final JavaLexer delegate;
    private IElementType currentTokenType;

    public JMLAwareJavaLexer(@NotNull LanguageLevel level) {
        this.delegate = new JavaLexer(level);
    }

    @Override
    public void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState) {
        delegate.start(buffer, startOffset, endOffset, initialState);
        updateCurrentTokenType();
    }

    @Override
    public int getState() {
        return delegate.getState();
    }

    @Override
    public @Nullable IElementType getTokenType() {
        return currentTokenType;
    }

    @Override
    public int getTokenStart() {
        return delegate.getTokenStart();
    }

    @Override
    public int getTokenEnd() {
        return delegate.getTokenEnd();
    }

    @Override
    public void advance() {
        delegate.advance();
        updateCurrentTokenType();
    }

    @Override
    public @NotNull CharSequence getBufferSequence() {
        return delegate.getBufferSequence();
    }

    @Override
    public int getBufferEnd() {
        return delegate.getBufferEnd();
    }

    private void updateCurrentTokenType() {
        IElementType tokenType = delegate.getTokenType();

        if (tokenType == JavaTokenType.END_OF_LINE_COMMENT) {
            String text = delegate.getBufferSequence().subSequence(
                    delegate.getTokenStart(), delegate.getTokenEnd()
            ).toString();

            if (text.startsWith("//@")) {
                currentTokenType = JML_LINE_COMMENT;
                return;
            }
        }

        if (tokenType == JavaTokenType.C_STYLE_COMMENT) {
            String text = delegate.getBufferSequence().subSequence(
                    delegate.getTokenStart(), delegate.getTokenEnd()
            ).toString();

            if (text.startsWith("/*@")) {
                currentTokenType = JML_BLOCK_COMMENT;
                return;
            }
        }

        currentTokenType = tokenType;
    }
}
