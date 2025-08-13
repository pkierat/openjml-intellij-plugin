package dev.kierat.plugins.openjml.hints;

import com.intellij.codeInsight.hints.FactoryInlayHintsCollector;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.codeInsight.hints.InlayGroup;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.InlayHintsProvider;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.codeInsight.hints.SettingsKey;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMember;
import dev.kierat.plugins.openjml.psi.PsiJMLSpecification;
import dev.kierat.plugins.openjml.utils.JMLSpecificationUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.JPanel;
import java.util.Arrays;

import static java.util.function.Predicate.not;

@SuppressWarnings("UnstableApiUsage")
public class JMLInlayHintsProvider implements InlayHintsProvider<NoSettings> {

    private static final SettingsKey<NoSettings> KEY = new SettingsKey<>("jml.inlay.hints");

    @NotNull
    @Override
    public SettingsKey<NoSettings> getKey() {
        return KEY;
    }

    @NotNull
    @Override
    public String getName() {
        return "JML specifications";
    }

    @Override
    public @NotNull String getPreviewText() {
        return """
                requires id != null;
                ensures \\result != null;
                public Product getById(Long id);
                """;
    }

    @Override
    public @NotNull NoSettings createSettings() {
        return new NoSettings();
    }

    @Override
    public @NotNull InlayHintsCollector getCollectorFor(@NotNull PsiFile file,
                                                        @NotNull Editor editor,
                                                        @NotNull NoSettings settings,
                                                        @NotNull InlayHintsSink sink) {

        return new FactoryInlayHintsCollector(editor) {
            @Override
            public boolean collect(@NotNull PsiElement element, @NotNull Editor editor, @NotNull InlayHintsSink sink) {
                if (!(element instanceof PsiMember member)) { return true; }

                JMLSpecificationUtil.findSpecification(member)
                        .map(PsiJMLSpecification::getPresentedText)
                        .filter(not(String::isBlank))
                        .ifPresent(jmlSpec -> addSpecificationBlock(element, editor, sink, jmlSpec));

                return true;
            }

            private void addSpecificationBlock(PsiElement element,
                                               Editor editor,
                                               InlayHintsSink sink,
                                               String jmlSpec) {

                int textOffset = element.getTextRange().getStartOffset();
                int lineNumber = editor.getDocument().getLineNumber(textOffset);
                int lineStart = editor.getDocument().getLineStartOffset(lineNumber);
                var indent = " ".repeat((textOffset - lineStart) * 2);

                Arrays.stream(jmlSpec.split("\n"))
                        .map(line -> getFactory().text(indent + line))
                        .forEach(text -> sink.addBlockElement(textOffset, false, true, 1, text));
            }
        };
    }


    @Override
    public @NotNull InlayGroup getGroup() {
        return InlayGroup.TYPES_GROUP;
    }

    @Override
    public boolean isVisibleInSettings() {
        return true;
    }

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull NoSettings noSettings) {
        return changeListener -> new JPanel();
    }

}
