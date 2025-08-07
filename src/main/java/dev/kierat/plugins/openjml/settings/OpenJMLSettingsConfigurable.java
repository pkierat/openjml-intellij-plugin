package dev.kierat.plugins.openjml.settings;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class OpenJMLSettingsConfigurable implements Configurable {

    private OpenJMLSettingsComponent component;

    @Override
    public @Nls String getDisplayName() {
        return "OpenJML";
    }

    @Override
    public @Nullable JComponent createComponent() {
        component = new OpenJMLSettingsComponent();
        return component.getPanel();
    }

    @Override
    public boolean isModified() {
        OpenJMLSettings settings = OpenJMLSettings.getInstance();
        return !component.getOpenJmlPath().equals(settings.getOpenJmlHome())
                || component.isNullableByDefault() != settings.isNullableByDefault();
    }

    @Override
    public void apply() {
        OpenJMLSettings settings = OpenJMLSettings.getInstance();
        settings.setOpenJmlHome(component.getOpenJmlPath());
        settings.setNullableByDefault(component.isNullableByDefault());
        settings.setJmlOptions(component.getJmlOptions());
    }

    @Override
    public void reset() {
        OpenJMLSettings settings = OpenJMLSettings.getInstance();
        component.setOpenJmlPath(settings.getOpenJmlHome());
        component.setNullableByDefault(settings.isNullableByDefault());
        component.setJmlOptions(settings.getJmlOptions());
    }

    @Override
    public void disposeUIResources() {
        component = null;
    }
}
