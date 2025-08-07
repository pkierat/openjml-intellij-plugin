package dev.kierat.plugins.openjml.action.generator;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaFile;
import dev.kierat.plugins.jmlgen.*;
import dev.kierat.plugins.openjml.action.element.PsiTypeElementAdapter;
import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.Modifier;
import java.util.Arrays;
import java.util.List;

public class JmlStubGenerator extends JavaRecursiveElementVisitor {

    private final SpecBuilder specBuilder;

    public JmlStubGenerator(String packageName, JmlGenerationConfig config) {
        VisitorConfiguration visitorConfig = VisitorConfiguration.builder()
                .elementFilter(e -> config.isIncludeNonPublic() || e.getModifiers().contains(Modifier.PUBLIC))
                .build();
        specBuilder = new SpecBuilder(packageName, visitorConfig);
    }

    private static class SpecBuilder {

        private final StringBuilder spec = new StringBuilder();

        private final JavaFileVisitor visitor;


        public SpecBuilder(String packageName, VisitorConfiguration config) {
            this.visitor = new JavaFileVisitor(packageName, config);
        }

        public void buildSpec(@NotNull List<PsiClass> classes) {
            var elements = classes.stream()
                    .map(PsiTypeElementAdapter::new)
                    .toList();
            spec.append(visitor.visit(elements));
        }

        CharSequence getJml() {
            return spec;
        }
    }

    public String generate(PsiJavaFile psiJavaFile) {
        StringBuilder spec = new StringBuilder();
        PsiClass[] psiClasses = psiJavaFile.getClasses();
        specBuilder.buildSpec(Arrays.asList(psiClasses));
        spec.append(specBuilder.getJml());

        return spec.toString();
    }

}
