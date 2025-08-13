package dev.kierat.plugins.openjml.psi;

import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiComment;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;

public interface PsiJMLSpecification extends PsiComment {

    String getPresentedText();

    enum Type {
        LINE(JavaTokenType.END_OF_LINE_COMMENT, "//@ \n"),
        BLOCK(JavaTokenType.C_STYLE_COMMENT, "/*@\n  @ \n  @*/\n");

        public final IElementType commentType;
        public final String emptyText;

        Type(IElementType commentType, String emptyText) {
            this.commentType = commentType;
            this.emptyText = emptyText;
        }

        public static @NotNull Type of(@NotNull IElementType type) {
            for (Type value : values()) {
                if (value.commentType == type) {
                    return value;
                }
            }
            throw new NoSuchElementException("Unsupported comment type: " + type);
        }
    }

}
