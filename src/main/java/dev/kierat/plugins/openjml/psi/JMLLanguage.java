package dev.kierat.plugins.openjml.psi;

import com.intellij.lang.Language;
import com.intellij.lang.java.JavaLanguage;
import org.jetbrains.annotations.NotNull;


public class JMLLanguage extends Language {

    public static final JMLLanguage INSTANCE = new JMLLanguage();

    public static final String ID = "JML";

    protected JMLLanguage() {
        super(JavaLanguage.INSTANCE, ID);
    }

    @Override
    public @NotNull String getDisplayName() {
        return ID;
    }

}
