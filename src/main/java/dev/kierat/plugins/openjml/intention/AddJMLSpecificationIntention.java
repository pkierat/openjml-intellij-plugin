package dev.kierat.plugins.openjml.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.preview.IntentionPreviewInfo;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMember;
import com.intellij.psi.impl.source.DummyHolderFactory;
import com.intellij.psi.impl.source.SourceTreeToPsiMap;
import com.intellij.psi.impl.source.tree.TreeElement;
import com.intellij.util.IncorrectOperationException;
import dev.kierat.plugins.openjml.OpenJMLBundle;
import dev.kierat.plugins.openjml.action.GoToJMLSpecificationAction;
import dev.kierat.plugins.openjml.psi.PsiJMLFile;
import dev.kierat.plugins.openjml.psi.PsiJMLSpecification;
import dev.kierat.plugins.openjml.utils.JMLElementFactory;
import dev.kierat.plugins.openjml.utils.JMLFileUtil;
import dev.kierat.plugins.openjml.utils.JMLSpecificationUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;

public abstract class AddJMLSpecificationIntention implements IntentionAction {

    protected abstract @NotNull PsiJMLSpecification buildJmlSpecification(@NotNull PsiElement jmlElement);

    protected abstract void insertJmlSpecification(@NotNull PsiElement jmlElement, @NotNull PsiJMLSpecification spec);

    protected boolean isAvailableFor(@NotNull PsiMember psiMember) {
        return !JMLSpecificationUtil.hasSpecification(psiMember);
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        Module module = ModuleUtilCore.findModuleForPsiElement(file);
        if (module == null) {
            return false;
        }

        int offset = editor.getCaretModel().getOffset();
        PsiMember element = getMember(file.findElementAt(offset));
        return element != null && isAvailableFor(element);
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        Module module = requireNonNull(ModuleUtilCore.findModuleForPsiElement(file));
        int offset = editor.getCaretModel().getOffset();
        PsiElement javaElement = getMember(file.findElementAt(offset));

        PsiJMLFile jmlFile = getOrCreateJmlFile(module, file);
        if (jmlFile == null) { return; }

        PsiElement jmlElement = findOrCreateJmlElement(jmlFile, javaElement);
        insertJmlSpecification(jmlElement);

        openFileAtElement(jmlFile, jmlElement);

    }

    private PsiElement findOrCreateJmlElement(PsiJMLFile jmlFile, PsiElement javaElement) {
        if (javaElement instanceof PsiJavaFile) { return jmlFile; }
        return WriteAction.computeAndWait(() ->
                jmlFile.findOrCreateElement(javaElement, new JMLElementFactory()::createJmlElement)
        );
    }

    private static @Nullable PsiJMLFile getOrCreateJmlFile(Module module, PsiFile file) {
        return switch (file) {
            case PsiJMLFile psiJMLFile -> psiJMLFile;
            case PsiJavaFile psiJavaFile -> {
                PsiJMLFile jmlFile = JMLFileUtil.getJmlFile(module, psiJavaFile);
                if (jmlFile == null && GoToJMLSpecificationAction.shouldCreateJmlFile(module.getProject())) {
                    WriteAction.runAndWait(() -> JMLFileUtil.createJmlFile(module, psiJavaFile));
                    jmlFile = JMLFileUtil.getJmlFile(module, psiJavaFile);
                }
                yield jmlFile;
            }
            default -> null;
        };
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }

    @Override
    public @NotNull IntentionPreviewInfo generatePreview(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        return IntentionPreviewInfo.EMPTY;
    }

    @Override
    public @IntentionName @NotNull String getText() {
        return OpenJMLBundle.message("intention.add.jml.specification.name");
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return OpenJMLBundle.message("intention.add.jml.specification.name");
    }

    private PsiMember getMember(PsiElement element) {
        return switch (element) {
            case null -> null;
            case PsiMember psiMember -> psiMember;
            default -> element.getParent() instanceof PsiMember psiMember ? psiMember : null;
        };
    }

    private void insertJmlSpecification(@NotNull PsiElement jmlElement) {
        if (JMLSpecificationUtil.hasSpecification(jmlElement)) { return; }
        PsiJMLSpecification jmlSpec = buildJmlSpecification(jmlElement);
        TreeElement contentElement = (TreeElement) SourceTreeToPsiMap.psiElementToTree(jmlSpec);
        DummyHolderFactory.createHolder(jmlElement.getManager(), contentElement, jmlElement);
        WriteAction.runAndWait(() -> insertJmlSpecification(jmlElement, jmlSpec));
    }

    public static void openFileAtElement(@NotNull PsiFile jmlFile,
                                         @NotNull PsiElement jmlElement) {
        FileEditorManager editorManager = FileEditorManager.getInstance(jmlFile.getProject());
        editorManager.openFile(jmlFile.getVirtualFile(), true);
        Editor editor = editorManager.getSelectedTextEditor();
        if (editor != null) {
            editor.getCaretModel().moveToOffset(jmlElement.getTextOffset());
            editor.getScrollingModel().scrollToCaret(ScrollType.CENTER);
        }
    }
}
