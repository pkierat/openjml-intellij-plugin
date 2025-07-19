package dev.kierat.plugins.openjml.psi;

import com.intellij.openapi.fileTypes.LanguageFileType;
import icons.OpenJMLIcons;
import org.jetbrains.annotations.NotNull;
import dev.kierat.plugins.openjml.OpenJMLBundle;

import javax.swing.*;


public final class JMLFileType extends LanguageFileType {

  public static final JMLFileType INSTANCE = new JMLFileType();

  private JMLFileType() {
    super(JMLLanguage.INSTANCE);
  }

  @Override
  public @NotNull String getName() {
    return "JML";
  }

  @Override
  public @NotNull String getDescription() {
    return OpenJMLBundle.message("filetype.jml.specification.description");
  }

  @Override
  public @NotNull String getDefaultExtension() {
    return "jml";
  }

  @Override
  public Icon getIcon() {
    return OpenJMLIcons.OpenJML;
  }
}
