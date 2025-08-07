package dev.kierat.plugins.openjml.action;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.tools.FilterInfo;
import com.intellij.tools.Tool;
import com.intellij.util.containers.ContainerUtil;
import dev.kierat.plugins.openjml.settings.OpenJMLSettings;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum OpenJMLTool {
    CHECK("CHECK", "JML Syntax Check", "--check"),
    ESC("ESC", "Extended Static Check", "--esc"),
    RAC("RAC", "Runtime Assertion Check", "--rac"),
    ;

    private static final String GROUP_NAME = "OpenJML";

    private static final List<String> OUTPUT_FILTERS = Arrays.asList(
            "$FILE_PATH$\\:$LINE$\\:$COLUMN$\\:", "$FILE_PATH$\\:$LINE$\\:"
    );
    private static final List<String> COMMON_OPTIONS = Arrays.asList(
            "--source-path", "$Sourcepath$",
            "--class-path", "$Classpath$"
    );

    private final String name;
    private final String description;
    private final String[] args;

    OpenJMLTool(String name, String description, String... args) {
        this.name = name;
        this.description = description;
        this.args = args;
    }

    @NotNull Builder configure(@NotNull OpenJMLSettings settings, String... extraArgs) {
        Tool tool = new Tool();
        tool.setName(name);
        tool.setDescription(description);
        tool.setGroup(GROUP_NAME);
        tool.setUseConsole(true);
        tool.setShowConsoleOnStdOut(true);
        tool.setShowConsoleOnStdErr(true);
        tool.setFilesSynchronizedAfterRun(true);
        tool.setEnabled(true);

        String openJmlHome = settings.getOpenJmlHome();
        tool.setWorkingDirectory(StringUtil.nullize(FileUtil.toSystemIndependentName(openJmlHome)));
        tool.setProgram(String.format("%s/openjml", openJmlHome));

        List<String> parameters = new ArrayList<>();
        parameters.addAll(Arrays.asList(args));
        parameters.addAll(COMMON_OPTIONS);
        parameters.addAll(Arrays.asList(extraArgs));
        if (settings.isNullableByDefault()) {
            parameters.add("--nullable-by-default");
        }
        parameters.add(settings.getJmlOptions());
        tool.setParameters(String.join(" ", parameters));

        FilterInfo[] filters = ContainerUtil.map2Array(OUTPUT_FILTERS, FilterInfo.class, s -> new FilterInfo(s, "", ""));
        tool.setOutputFilters(filters);

        return new BuilderImpl(tool);
    }

    public interface Builder {

        Builder withDir(@NotNull String dir);

        Builder withSourceFile(@NotNull String sourceFile);

        Tool get();
    }

    private static class BuilderImpl implements Builder {

        private final Tool tool;

        public BuilderImpl(Tool tool) {
            this.tool = tool;
        }

        @Override
        public BuilderImpl withDir(@NotNull String dir) {
            tool.setParameters(tool.getParameters() + " --dir " + dir);
            return this;
        }

        @Override
        public BuilderImpl withSourceFile(@NotNull String sourceFile) {
            tool.setParameters(tool.getParameters() + " " + sourceFile);
            return this;
        }

        @Override
        public Tool get() {
            return tool;
        }
    }
}
