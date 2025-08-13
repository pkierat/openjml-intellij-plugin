package dev.kierat.plugins.openjml.utils.generator;

public class JMLGenerationConfig {
    private final boolean includeNonPublic;

    public JMLGenerationConfig(boolean includeNonPublic) {
        this.includeNonPublic = includeNonPublic;
    }

    public boolean isIncludeNonPublic() {
        return includeNonPublic;
    }
}
