package com.lsfusion.actions;

import com.intellij.codeInsight.unwrap.ScopeHighlighter;
import com.intellij.ide.structureView.StructureView;
import com.intellij.ide.util.FileStructurePopup;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilBase;
import com.intellij.refactoring.IntroduceTargetChooser;
import com.intellij.refactoring.RefactoringBundle;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import com.intellij.util.Function;
import com.lsfusion.LSFBundle;
import com.lsfusion.LSFIcons;
import com.lsfusion.lang.LSFLanguage;
import com.lsfusion.lang.classes.ConcatenateClassSet;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.classes.LSFValueClass;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.lang.psi.context.LSFExpression;
import com.lsfusion.lang.psi.declarations.LSFActionOrPropDeclaration;
import com.lsfusion.lang.typeinfer.InferExResult;
import com.lsfusion.lang.typeinfer.LSFExClassSet;
import com.lsfusion.structure.LSFActionOrPropertyStatementTreeElement;
import com.lsfusion.structure.LSFStructureViewNavigationHandler;
import com.lsfusion.structure.LSFTreeBasedStructureViewBuilder;
import com.lsfusion.util.LSFPsiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.intellij.codeInsight.CodeInsightUtilCore.findElementInRange;

public class InsertCompositionAction extends AnAction {
    protected static final String REFACTORING_NAME = LSFBundle.message("insert.composition.title");

    public InsertCompositionAction() {
        initPresentation();
    }
    
    protected void initPresentation() {
        Presentation presentation = getTemplatePresentation();
        presentation.setText(REFACTORING_NAME);
        presentation.setDescription("Inserts composition in the caret position");
        presentation.setIcon(LSFIcons.FILE);    
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(CommonDataKeys.PROJECT);
        Editor editor = e.getData(CommonDataKeys.EDITOR);

        assert editor != null && project != null;

        final PsiFile psiFile = PsiUtilBase.getPsiFileInEditor(editor, project);
        if (psiFile instanceof LSFFile) {
            invoke(project, editor, (LSFFile)psiFile, e.getDataContext());
        }
    }

    @Override
    public void update(AnActionEvent e) {
        DataContext dataContext = e.getDataContext();
        e.getPresentation().setEnabled(false);
        Project project = CommonDataKeys.PROJECT.getData(dataContext);
        if (project == null) return;

        Editor editor = CommonDataKeys.EDITOR.getData(dataContext);
        if (editor == null) return;

        final PsiFile psiFile = PsiUtilBase.getPsiFileInEditor(editor, project);
        if (psiFile == null || psiFile.getLanguage() != LSFLanguage.INSTANCE) return;

        if (!ApplicationManager.getApplication().isUnitTestMode() && !editor.getContentComponent().isShowing()) return;
        e.getPresentation().setEnabled(true);
    }

    protected boolean isFastAction() {
        return false;
    }

    public void invoke(@NotNull final Project project, final Editor editor, final LSFFile file, DataContext dataContext) {
        if (file == null) {
            return;
        }
        PsiDocumentManager.getInstance(project).commitAllDocuments();

        final FileEditor fileEditor = PlatformDataKeys.FILE_EDITOR.getData(dataContext);
        final SelectionModel selectionModel = editor.getSelectionModel();
        LSFExpression selectedExpression = null;
        if (!selectionModel.hasSelection()) {
            final int offset = editor.getCaretModel().getOffset();
            final List<LSFExpression> expressions = LSFPsiUtils.collectExpressions(file, editor, offset);
            if (expressions.isEmpty()) {
                selectionModel.selectLineAtCaret();
            } else if (isFastAction() || expressions.size() == 1) {
                selectedExpression = expressions.get(0);
            } else {
                Pass<LSFExpression> selectionHandler = new Pass<>() {
                    public void pass(final LSFExpression selectedExpr) {
                        invokeImpl(file, project, editor, fileEditor, selectedExpr);
                    }
                };
                IntroduceTargetChooser.showChooser(editor, expressions, selectionHandler, ExpressionRenderer.get(), "Expression to wrap:", -1, ScopeHighlighter.NATURAL_RANGER);
                return;
            }
        }

        if (selectedExpression == null) {
            selectedExpression = findElementInRange(file, selectionModel.getSelectionStart(), selectionModel.getSelectionEnd(), LSFExpression.class, LSFLanguage.INSTANCE);
        }
        if (selectedExpression == null) {
            showErrorMessage(project, editor, RefactoringBundle.message("selected.block.should.represent.an.expression"));
            return;
        }

        invokeImpl(file, project, editor, fileEditor, selectedExpression);
    }

    protected void showErrorMessage(final Project project, Editor editor, String message) {
        CommonRefactoringUtil.showErrorHint(project, editor, message, REFACTORING_NAME, null);
    }

    protected void invokeImpl(final LSFFile file, final Project project, final Editor editor, final FileEditor fileEditor, final @NotNull LSFExpression expr) {
        InferExResult inferResult = expr.inferParamClasses(null).finishEx();
        LSFClassSet classSet = LSFExClassSet.fromEx(expr.resolveInferredValueClass(inferResult));
//        InferResult inferResult = expr.inferParamClasses(null).finish();
//        LSFClassSet classSet = expr.resolveInferredValueClass(inferResult);
        if (classSet != null && !(classSet instanceof ConcatenateClassSet)) {
            final LSFValueClass valueClass = classSet.getCommonClass();
            LSFStructureViewNavigationHandler navigationHandler = new LSFStructureViewNavigationHandler() {
                @Override
                public <T extends LSFActionOrPropDeclaration> void navigate(LSFActionOrPropertyStatementTreeElement<T> element, boolean requestFocus) {
                    insertComposition(editor, expr, valueClass, element.getElement());
                }
            };

            StructureView structureView = new LSFTreeBasedStructureViewBuilder(file, valueClass, navigationHandler).createStructureView(fileEditor, project);
            FileStructurePopup popup = new FileStructurePopup(project, fileEditor, structureView, true);
            popup.setTitle(LSFBundle.message("inser.composition.selection.popup.title"));
            popup.show();
            return;
        }

        showErrorMessage(project, editor, LSFBundle.message("insert.composition.cant.determine.type"));
    }

    private <T extends LSFActionOrPropDeclaration> void insertComposition(final Editor editor, final LSFExpression expr, final LSFValueClass valueClass, final T composition) {
        ApplicationManager.getApplication().runWriteAction(() -> {
            Document document = editor.getDocument();

            RangeMarker rangeMarker = document.createRangeMarker(expr.getTextRange());
            rangeMarker.setGreedyToLeft(false);
            rangeMarker.setGreedyToRight(false);

            List<LSFClassSet> paramClasses = composition.resolveParamClasses();
            int newCaretPosition;
            if (paramClasses == null || paramClasses.size() == 1) {
                String newText = composition.getDeclName() + "(" + expr.getText() + ")";
                document.replaceString(rangeMarker.getStartOffset(), rangeMarker.getEndOffset(), newText);
                newCaretPosition = rangeMarker.getEndOffset();
            } else {
                int exprIndex = -1;
                String commaPrefix = "";
                String commaPostfix = "";
                for (int i = 0; i < paramClasses.size(); i++) {
                    LSFValueClass paramClass = paramClasses.get(i).getCommonClass();
                    if (valueClass.equals(paramClass)) {
                        exprIndex = i;
                    } else {
                        if (exprIndex == -1) {
                            //доп. проверка, если почему-то не нашли параметра с соотв. классом
                            if (i <= paramClasses.size() - 1) {
                                commaPrefix += ", ";
                            }
                        } else {
                            commaPostfix += ", ";
                        }
                    }
                }

                document.insertString(rangeMarker.getStartOffset(), composition.getDeclName() + "(" + commaPrefix);
                document.insertString(rangeMarker.getEndOffset(), commaPostfix + ")");

                if (exprIndex != 0) {
                    newCaretPosition = rangeMarker.getStartOffset() - commaPrefix.length();
                } else {
                    newCaretPosition = rangeMarker.getEndOffset() + 2; // i.e. + ", ".length
                }
            }

            editor.getCaretModel().moveToOffset(newCaretPosition);
            rangeMarker.dispose();
        });
    }

    public static class ExpressionRenderer implements Function<LSFExpression, String> {
        private static ExpressionRenderer instance = new ExpressionRenderer();

        public static ExpressionRenderer get() {
            return instance;
        }

        @Override
        public String fun(LSFExpression psiExpression) {
            return render(psiExpression);
        }

        public static String render(LSFExpression expression) {
            String text = expression.getText();
            assert text != null;

            text = text.replace('\n', ' ').replaceAll("\\s+", " ");
            if (text.length() > 100) {
                text = text.substring(0, 100);
            }
            return text;
        }
    }
}
