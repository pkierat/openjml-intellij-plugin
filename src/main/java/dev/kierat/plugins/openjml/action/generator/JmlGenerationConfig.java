package dev.kierat.plugins.openjml.action.generator;

public class JmlGenerationConfig {
    private final boolean includeNonPublic;

    public JmlGenerationConfig(boolean includeNonPublic) {
        this.includeNonPublic = includeNonPublic;
    }

    public boolean isIncludeNonPublic() {
        return includeNonPublic;
    }
}
