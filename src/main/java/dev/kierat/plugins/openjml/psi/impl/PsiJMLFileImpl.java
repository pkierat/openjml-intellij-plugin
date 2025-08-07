package dev.kierat.plugins.openjml.psi.impl;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiFileImpl;
import com.intellij.psi.util.PsiTreeUtil;
import dev.kierat.plugins.openjml.psi.JMLFileType;
import dev.kierat.plugins.openjml.psi.JMLParserDefinition;
import dev.kierat.plugins.openjml.psi.PsiJMLFile;
import org.jetbrains.annotations.NotNull;

public class PsiJMLFileImpl extends PsiFileImpl implements PsiJMLFile {

    public PsiJMLFileImpl(@NotNull FileViewProvider viewProvider) {
        super(JMLParserDefinition.JML_FILE, JMLParserDefinition.JML_FILE, viewProvider);
    }

    @Override
    public @NotNull FileType getFileType() {
        return JMLFileType.INSTANCE;
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        visitor.visitFile(this);
    }

    public @NotNull PsiClass[] getClasses() {
        return PsiTreeUtil.getChildrenOfType(this, PsiClass.class);
    }

    public PsiPackageStatement getPackageStatement() {
        return PsiTreeUtil.getChildOfType(this, PsiPackageStatement.class);
    }

    public String getPackageName() {
        PsiPackageStatement statement = getPackageStatement();
        return statement != null ? statement.getPackageName() : "";
    }

    public PsiImportList getImportList() {
        return PsiTreeUtil.getChildOfType(this, PsiImportList.class);
    }

    @Override
    public String toString() {
        return "PsiJMLFile:" + getName();
    }

}