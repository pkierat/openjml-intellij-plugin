package dev.kierat.plugins.openjml.intention;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMember;
import dev.kierat.plugins.openjml.psi.PsiJMLSpecification;
import dev.kierat.plugins.openjml.psi.impl.PsiJMLSpecificationImpl;
import org.jetbrains.annotations.NotNull;

public class AddFieldSpecificationIntention extends AddJMLSpecificationIntention {

    @Override
    protected boolean isAvailableFor(@NotNull PsiMember psiMember) {
        return psiMember instanceof PsiField;
    }

    @Override
    protected @NotNull PsiJMLSpecification buildJmlSpecification(@NotNull PsiElement jmlElement) {
        return new PsiJMLSpecificationImpl(PsiJMLSpecification.Type.LINE);
    }

    @Override
    protected void insertJmlSpecification(@NotNull PsiElement jmlElement, @NotNull PsiJMLSpecification spec) {
        jmlElement.addBefore(spec, jmlElement.getFirstChild());
    }
}
