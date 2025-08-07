package dev.kierat.plugins.openjml.psi;

import com.intellij.psi.tree.IElementType;

public interface JMLTokenTypes {
    IElementType JML_LINE_COMMENT = new IElementType("JML_LINE_COMMENT", JMLLanguage.INSTANCE);
    IElementType JML_BLOCK_COMMENT = new IElementType("JML_BLOCK_COMMENT", JMLLanguage.INSTANCE);
}