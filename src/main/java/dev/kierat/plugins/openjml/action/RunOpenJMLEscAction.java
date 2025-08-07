package dev.kierat.plugins.openjml.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.ui.Messages;
import dev.kierat.plugins.openjml.settings.OpenJMLSettings;
import org.jetbrains.annotations.NotNull;

public class RunOpenJMLEscAction extends AnAction {

    public RunOpenJMLEscAction() {
        super("Run OpenJML ESC", "Run OpenJML ESC on selected file(s)", AllIcons.Actions.Execute);
    }

    @Override
    public void update(AnActionEvent e) {
        VirtualFile[] files = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);
        e.getPresentation().setEnabledAndVisible(files != null && files.length > 0);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        VirtualFile[] files = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);
        Project project = e.getProject();

        if (files == null || project == null) return;

        OpenJMLSettings settings = OpenJMLSettings.getInstance();
        String openJmlHome = settings.getOpenJmlHome();
        if (openJmlHome == null || openJmlHome.isBlank()) {
            Messages.showErrorDialog(project, "OpenJML path is not set in settings.", "OpenJML Error");
            ShowSettingsUtil.getInstance().showSettingsDialog(project, "OpenJML");
            return;
        }

        OpenJMLTool.Builder toolBuilder = OpenJMLTool.ESC.configure(settings);

        for (VirtualFile file : files) {
            if (file.isDirectory()) {
                toolBuilder.withDir(file.getPath());
            } else {
                toolBuilder.withSourceFile(file.getPath());
            }
        }

        try {
            toolBuilder.get().execute(e, e.getDataContext(), 0L, null);
        } catch (Exception ex) {
            Messages.showErrorDialog(project, "Failed to run OpenJML:\n" + ex.getMessage(), "OpenJML Error");
        }
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

}
