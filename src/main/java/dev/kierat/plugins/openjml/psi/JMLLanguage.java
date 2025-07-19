package dev.kierat.plugins.openjml.psi;

import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;


public class JMLLanguage extends Language {

  public static final JMLLanguage INSTANCE = new JMLLanguage();

  public static final String ID = "JML";
  public static final String SOURCE_ROOT_NAME = ID.toLowerCase(Locale.ROOT);

  protected JMLLanguage() {
    super(ID);
  }

  @Override
  public @NotNull String getDisplayName() {
    return ID;
  }
}
