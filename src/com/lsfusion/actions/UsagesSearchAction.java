package com.lsfusion.actions;

import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.TargetElementUtil;
import com.intellij.codeInsight.actions.BaseCodeInsightAction;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.IndexNotReadyException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopupStep;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.psi.PsiCompiledElement;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.awt.RelativePoint;
import com.lsfusion.lang.psi.declarations.LSFObjectInputParamDeclaration;
import com.lsfusion.lang.psi.declarations.LSFPropertyDrawDeclaration;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public abstract class UsagesSearchAction extends BaseCodeInsightAction implements CodeInsightActionHandler {
    public static final String PROPERTY_USAGES = "Property";
    public static final String PROPERTY_DRAW_USAGES = "Property Draw";
    public static final String OBJECT_USAGES = "Object";
    public static final String PARAMETER_USAGES = "Parameter";

    public static String propertyUsagesSearchMode;
    public static PsiElement sourceElement;

    private AnActionEvent event;

    @Override
    public void actionPerformed(AnActionEvent e) {
        propertyUsagesSearchMode = null;
        sourceElement = null;
        event = e;
        super.actionPerformed(e);
    }

    @NotNull
    @Override
    protected CodeInsightActionHandler getHandler() {
        return this;
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        PsiDocumentManager.getInstance(project).commitAllDocuments();

        try {
            int offset = editor.getCaretModel().getOffset();
            PsiElement element = findSourceElement(project, editor, offset);

            if (element == null) {
                return;
            }

            LSFPropertyDrawDeclaration propDrawDecl = PsiTreeUtil.getParentOfType(element, LSFPropertyDrawDeclaration.class);
            if (propDrawDecl == null) {
                LSFObjectInputParamDeclaration objectDecl = PsiTreeUtil.getParentOfType(element, LSFObjectInputParamDeclaration.class);
                if (objectDecl == null) {
                    getPlatformAction().actionPerformed(event);
                } else {
                    showChoicePopup(element, parameterUsagesAlternatives);
                }
            } else {
                showChoicePopup(element, propertyUsagesAlternatives);
            }
        } catch (IndexNotReadyException e) {
            DumbService.getInstance(project).showDumbModeNotification("Navigation is not available here during index update");
        }
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }

    public PsiElement findSourceElement(Project project, Editor editor, int offset) {
        if (TargetElementUtil.inVirtualSpace(editor, offset)) {
            return null;
        }

        final Document document = editor.getDocument();
        PsiFile file = PsiDocumentManager.getInstance(project).getPsiFile(document);
        if (file == null) return null;

        if (file instanceof PsiCompiledElement) {
            PsiElement mirror = ((PsiCompiledElement) file).getMirror();
            if (mirror instanceof PsiFile) file = (PsiFile) mirror;
        }

        return file.findElementAt(TargetElementUtil.adjustOffset(file, document, offset));
    }

    private List<String> propertyUsagesAlternatives = Arrays.asList(PROPERTY_DRAW_USAGES, PROPERTY_USAGES);
    private List<String> parameterUsagesAlternatives = Arrays.asList(OBJECT_USAGES, PARAMETER_USAGES);
    
    private void showChoicePopup(PsiElement element, List<String> alternatives) {
        RelativePoint rp = JBPopupFactory.getInstance().guessBestPopupLocation(event.getDataContext());
        Component component = event.getData(PlatformDataKeys.CONTEXT_COMPONENT);
        JBPopupFactory
                .getInstance()
                .createListPopup(createListPopupStep(element, event, component, alternatives))
                .show(rp);
    }

    private ListPopupStep createListPopupStep(final PsiElement source, final AnActionEvent e, final Component component, List<String> alternatives) {
        return new BaseListPopupStep<String>("Choose element type", alternatives.toArray(new String[0])) {
            @NotNull
            @Override
            public String getTextFor(String item) {
                return item;
            }

            @Override
            public PopupStep onChosen(String item, boolean finalChoice) {
                propertyUsagesSearchMode = item;
                sourceElement = source;

                return doFinalStep(() -> getPlatformAction().actionPerformed(new AnActionEvent(
                        e.getInputEvent(),
                        DataManager.getInstance().getDataContext(component), 
                        e.getPlace(), e.getPresentation(), 
                        e.getActionManager(), 
                        e.getModifiers()
                )));
            }
        };
    }
    
    protected abstract AnAction getPlatformAction();
}
