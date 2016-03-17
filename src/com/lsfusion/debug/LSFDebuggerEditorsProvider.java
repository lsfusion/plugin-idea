package com.lsfusion.debug;

import com.intellij.lang.Language;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaCodeFragment;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.XExpression;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.evaluation.EvaluationMode;
import com.intellij.xdebugger.impl.breakpoints.XExpressionImpl;
import com.lsfusion.lang.LSFFileType;
import com.lsfusion.lang.LSFLanguage;
import com.lsfusion.lang.psi.LSFCodeFragment;
import com.lsfusion.lang.psi.LSFFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.java.debugger.JavaDebuggerEditorsProvider;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class LSFDebuggerEditorsProvider extends JavaDebuggerEditorsProvider {
    @NotNull
    @Override
    public FileType getFileType() {
        return LSFFileType.INSTANCE;
    }

    @NotNull
    @Override
    public Collection<Language> getSupportedLanguages(@NotNull Project project, @Nullable XSourcePosition sourcePosition) {
        Set<Language> langs = new HashSet<>(super.getSupportedLanguages(project, sourcePosition));
        langs.add(LSFLanguage.INSTANCE);
        langs.add(JavaLanguage.INSTANCE);
        return langs;
    }

    @NotNull
    @Override
    public XExpression createExpression(@NotNull Project project, @NotNull Document document, @Nullable Language language, @NotNull EvaluationMode mode) {
        if (language != LSFLanguage.INSTANCE) {
            return super.createExpression(project, document, language, mode);
        }

        PsiDocumentManager.getInstance(project).commitDocument(document);
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
        if (psiFile != null && psiFile instanceof LSFFile) {
            return new XExpressionImpl(psiFile.getText(), language, ((JavaCodeFragment)psiFile).importsToString(), mode);
        }
        return XDebuggerUtil.getInstance().createExpression(document.getText(), language, null, mode);
    }

    @Override
    protected PsiFile createExpressionCodeFragment(@NotNull Project project, @NotNull XExpression expression, @Nullable PsiElement context, boolean isPhysical) {
        if (expression.getLanguage() == LSFLanguage.INSTANCE) {
            LSFCodeFragment fragment = new LSFCodeFragment(expression.getMode() == EvaluationMode.EXPRESSION, project, context, expression.getExpression());
            fragment.forceResolveScope(GlobalSearchScope.allScope(project));
            return fragment;
        } else {
            return super.createExpressionCodeFragment(project, expression, context, isPhysical);
        }
    }
}
