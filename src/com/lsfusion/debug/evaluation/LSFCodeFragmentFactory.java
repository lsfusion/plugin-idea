package com.lsfusion.debug.evaluation;

import com.intellij.debugger.engine.evaluation.CodeFragmentKind;
import com.intellij.debugger.engine.evaluation.DefaultCodeFragmentFactory;
import com.intellij.debugger.engine.evaluation.TextWithImports;
import com.intellij.debugger.engine.evaluation.expression.EvaluatorBuilder;
import com.intellij.debugger.engine.evaluation.expression.EvaluatorBuilderImpl;
import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaCodeFragment;
import com.intellij.psi.JavaCodeFragmentFactory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.JavaDummyHolder;
import com.lsfusion.debug.DebugUtils;
import com.lsfusion.lang.LSFFileType;
import com.lsfusion.lang.LSFLanguage;
import com.lsfusion.lang.psi.LSFCodeFragment;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import com.lsfusion.lang.psi.references.LSFNamespaceReference;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LSFCodeFragmentFactory extends DefaultCodeFragmentFactory {

    private LSFFile getContextLSFFile(PsiElement context) {
        if(context == null)
            return null;

        PsiFile file = context.getContainingFile();
        if(file instanceof LSFFile)
            return (LSFFile) file;

        PsiElement fileContext = file.getContext();
        if(fileContext instanceof LSFFile)
            return (LSFFile) fileContext;

        return null;
    }

    // todo: clean up after the end of support for IDEA 2024.2
    // IDEA 2024.2-
    @Override
    public JavaCodeFragment createCodeFragment(TextWithImports item, PsiElement context, Project project) {
        return createPsiCodeFragmentImpl(item, context, project);
    }

    // IDEA 2024.3+
    public JavaCodeFragment createPsiCodeFragmentImpl(TextWithImports item, PsiElement context, @NotNull Project project) {
        if (isContextAccepted(context)) {
            String text = item.getText();
            if (text == null) {
                return null;
            }

            String namespace = "null";
            String priorities = "null";
            String require = "null";
            LSFFile lsfFile = getContextLSFFile(context);
            if(lsfFile != null) {
                LSFModuleDeclaration moduleDecl = lsfFile.getModuleDeclaration();
                if(moduleDecl != null) {
                    namespace = "\"" + moduleDecl.getNamespace() + "\"";
                    List<LSFNamespaceReference> priorityRefs = moduleDecl.getPriorityRefs();
                    if(priorityRefs != null && !priorityRefs.isEmpty()) {
                        priorities = "";
                        for (LSFNamespaceReference priorityRef : priorityRefs)
                            priorities = (priorities.isEmpty() ? "" : priorities + ",") + priorityRef.getText();
                        priorities = "\"" + priorities + "\"";
                    }
                    require = "\"" + moduleDecl.getGlobalName() + "\"";
                }
            }
            
            String evalMethod = item.getKind() == CodeFragmentKind.EXPRESSION ? "eval" : "evalAction";

            String evalString = "\"" + StringEscapeUtils.escapeJava(text) + "\"";
            String javaEvalString = DebugUtils.ACTION_DEBUGGER_FQN + ".getInstance()" +
                                    "." + evalMethod + "(action, context," + namespace + ", " + require + ", " + priorities + ", " + evalString + ")";
            return JavaCodeFragmentFactory.getInstance(project).createCodeBlockCodeFragment(javaEvalString, null, true);
        } else {
            // For IDEA 2024.3+ should be: return super.createPsiCodeFragmentImpl(item, context, project);
            return super.createCodeFragment(item, context, project);
        }
    }

    // IDEA 2024.2-
    @Override
    public JavaCodeFragment createPresentationCodeFragment(TextWithImports item, PsiElement context, Project project) {
        return createPresentationPsiCodeFragmentImpl(item, context, project);
    }

    // IDEA 2024.3+
    protected JavaCodeFragment createPresentationPsiCodeFragmentImpl(@NotNull TextWithImports item, PsiElement context, @NotNull Project project) {
        return new LSFCodeFragment(item.getKind() == CodeFragmentKind.EXPRESSION, project, context, item.getText());
    }

    @Override
    public boolean isContextAccepted(PsiElement context) {
        if (context != null) {
            Language language = context.getLanguage();
            if (LSFLanguage.INSTANCE.equals(language)) {
                return true;
            }

            //magic for: com.intellij.debugger.ui.impl.watch.WatchItemDescriptor.getEvaluationCode()
            //           com.intellij.debugger.impl.PositionUtil.getContextElement() возвращает сгенерированный java-элемент в качестве контекста
            PsiFile containingFile = context.getContainingFile();
            if (containingFile instanceof JavaDummyHolder) {
                PsiElement fileContext = containingFile.getContext();
                return fileContext != null && fileContext.getContainingFile() instanceof LSFFile;
            }
        }
        return false;
    }

    @Override
    public @NotNull LanguageFileType getFileType() {
        return LSFFileType.INSTANCE;
    }

    @Override
    public EvaluatorBuilder getEvaluatorBuilder() {
        return EvaluatorBuilderImpl.getInstance();
    }
}
