package dev.kierat.plugins.openjml.settings;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.fields.ExpandableTextField;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class OpenJMLSettingsComponent {
    private final JPanel panel;
    private final TextFieldWithBrowseButton openJmlPathField;
    private final JCheckBox nullableByDefaultField;
    private final ExpandableTextField jmlOptionsField;

    public OpenJMLSettingsComponent() {
        openJmlPathField = new TextFieldWithBrowseButton();
        nullableByDefaultField = new JCheckBox();
        jmlOptionsField = new ExpandableTextField(this::parseJMLOptions, this::joinJMLOptions);
        JPanel jmlOptionsPanel = new JPanel(new BorderLayout());
        jmlOptionsPanel.add(jmlOptionsField, BorderLayout.CENTER);

        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        openJmlPathField.addBrowseFolderListener(
                null,
                descriptor.withTitle("Select OpenJML Home")
        );

        panel = FormBuilder.createFormBuilder()
                .addLabeledComponent("OpenJML home:", openJmlPathField)
                .addLabeledComponent("Nullable by default:", nullableByDefaultField)
                .addLabeledComponent("JML options:", jmlOptionsPanel)
                .addComponentFillVertically(new JPanel(), 1)
                .getPanel();
    }

    public JPanel getPanel() {
        return panel;
    }

    public String getOpenJmlPath() {
        return openJmlPathField.getText();
    }

    public void setOpenJmlPath(String path) {
        openJmlPathField.setText(path != null ? path : "");
    }

    public boolean isNullableByDefault() {
        return nullableByDefaultField.isSelected();
    }

    public void setNullableByDefault(boolean selected) {
        nullableByDefaultField.setSelected(selected);
    }

    public String getJmlOptions() {
        return jmlOptionsField.getText();
    }

    public void setJmlOptions(String text) {
        jmlOptionsField.setText(text);
    }

    private List<String> parseJMLOptions(String jmlOptions) {
        return Arrays.asList(jmlOptions.split(" +"));
    }

    private String joinJMLOptions(List<String> jmlOptions) {
        return String.join(" ", jmlOptions);
    }

}
