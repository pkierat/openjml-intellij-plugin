package dev.kierat.plugins.openjml.formatting;

import com.intellij.formatting.FormattingContext;
import com.intellij.formatting.FormattingModel;
import com.intellij.formatting.FormattingModelBuilder;
import com.intellij.lang.java.JavaFormattingModelBuilder;
import org.jetbrains.annotations.NotNull;

public class JMLFormattingModelBuilder implements FormattingModelBuilder {

    private final JavaFormattingModelBuilder delegate = new JavaFormattingModelBuilder();

    @Override
    public @NotNull FormattingModel createModel(@NotNull FormattingContext formattingContext) {
        return delegate.createModel(formattingContext);
    }
}
