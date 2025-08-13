package dev.kierat.plugins.openjml.utils.generator.adapter.element;

import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.Name;
import java.util.Objects;

record PsiNameAdapter(String content) implements Name {

    @Override
    public int length() {
        return content.length();
    }

    @Override
    public char charAt(int index) {
        return content.charAt(index);
    }

    @Override
    public @NotNull CharSequence subSequence(int start, int end) {
        return content.subSequence(start, end);
    }

    @Override
    public boolean contentEquals(CharSequence cs) {
        return Objects.equals(content, cs.toString());
    }

    @Override
    public @NotNull String toString() {
        return content;
    }
}
