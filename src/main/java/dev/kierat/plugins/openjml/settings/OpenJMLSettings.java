package dev.kierat.plugins.openjml.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.*;
import org.jetbrains.annotations.NotNull;

@State(
        name = "OpenJMLSettings",
        storages = @Storage("openjml.xml")
)
public class OpenJMLSettings implements PersistentStateComponent<OpenJMLSettings.State> {

    public static class State {

        public String openJmlHome = "";
        public boolean nullableByDefault = false;
        public String jmlOptions = "";
    }

    private State state = new State();

    public static OpenJMLSettings getInstance() {
        return ApplicationManager.getApplication().getService(OpenJMLSettings.class);
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state = state;
    }

    public String getOpenJmlHome() {
        return state.openJmlHome;
    }

    public void setOpenJmlHome(String path) {
        state.openJmlHome = path;
    }

    public boolean isNullableByDefault() {
        return state.nullableByDefault;
    }

    public void setNullableByDefault(boolean nullableByDefault) {
        state.nullableByDefault = nullableByDefault;
    }

    public String getJmlOptions() {
        return state.jmlOptions;
    }

    public void setJmlOptions(String jmlOptions) {
        state.jmlOptions = jmlOptions;
    }
}
