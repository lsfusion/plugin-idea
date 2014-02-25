package com.simpleplugin.actions;

import com.intellij.codeInsight.unwrap.ScopeHighlighter;
import com.intellij.ide.structureView.StructureView;
import com.intellij.ide.util.FileStructurePopup;
import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.refactoring.IntroduceTargetChooser;
import com.intellij.refactoring.RefactoringActionHandler;
import com.intellij.refactoring.RefactoringBundle;
import com.intellij.refactoring.actions.BaseRefactoringAction;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import com.intellij.util.Function;
import com.simpleplugin.LSFBundle;
import com.simpleplugin.LSFIcons;
import com.simpleplugin.LSFLanguage;
import com.simpleplugin.classes.LSFClassSet;
import com.simpleplugin.classes.LSFValueClass;
import com.simpleplugin.classes.StructClassSet;
import com.simpleplugin.psi.LSFFile;
import com.simpleplugin.psi.LSFPropertyStatement;
import com.simpleplugin.psi.context.LSFExpression;
import com.simpleplugin.structure.LSFPropertyStatementTreeElement;
import com.simpleplugin.structure.LSFStructureViewNavigationHandler;
import com.simpleplugin.structure.LSFTreeBasedStructureViewBuilder;
import com.simpleplugin.typeinfer.InferResult;
import com.simpleplugin.util.LSFPsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.intellij.codeInsight.CodeInsightUtilCore.findElementInRange;

public class InsertCompositionAction extends BaseRefactoringAction {
    protected static final String REFACTORING_NAME = LSFBundle.message("insert.composition.title");

    public InsertCompositionAction() {
        Presentation presentation = getTemplatePresentation();
        presentation.setText(REFACTORING_NAME);
        presentation.setDescription("Inserts composition in the caret position");
        presentation.setIcon(LSFIcons.FILE);
    }

    @Override
    protected boolean isAvailableForLanguage(Language language) {
        return LSFLanguage.INSTANCE == language;
    }

    @Override
    protected boolean isAvailableInEditorOnly() {
        return true;
    }

    @Override
    protected boolean isEnabledOnElements(@NotNull PsiElement[] elements) {
        return false;
    }

    @Nullable
    @Override
    protected RefactoringActionHandler getHandler(@NotNull DataContext dataContext) {
        return new InsertCompositionActionHandler();
    }

    private static class InsertCompositionActionHandler implements RefactoringActionHandler {
        @Override
        public void invoke(@NotNull final Project project, final Editor editor, final PsiFile file, DataContext dataContext) {
            if (!(file instanceof LSFFile)) {
                return;
            }
            PsiDocumentManager.getInstance(project).commitAllDocuments();

            final LSFFile lsfFile = (LSFFile) file;
            final FileEditor fileEditor = PlatformDataKeys.FILE_EDITOR.getData(dataContext);
            final SelectionModel selectionModel = editor.getSelectionModel();
            LSFExpression selectedExpression = null;
            if (!selectionModel.hasSelection()) {
                final int offset = editor.getCaretModel().getOffset();
                final List<LSFExpression> expressions = LSFPsiUtils.collectExpressions(file, editor, offset);
                if (expressions.isEmpty()) {
                    selectionModel.selectLineAtCaret();
                } else if (expressions.size() == 1) {
                    selectedExpression = expressions.get(0);
                } else {
                    Pass<LSFExpression> selectionHandler = new Pass<LSFExpression>() {
                        public void pass(final LSFExpression selectedExpr) {
                            invokeImpl(lsfFile, project, editor, fileEditor, selectedExpr);
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

            invokeImpl(lsfFile, project, editor, fileEditor, selectedExpression);
        }

        protected void showErrorMessage(final Project project, Editor editor, String message) {
            CommonRefactoringUtil.showErrorHint(project, editor, message, REFACTORING_NAME, null);
        }

        protected void invokeImpl(final LSFFile file, final Project project, final Editor editor, final FileEditor fileEditor, final @NotNull LSFExpression expr) {
            InferResult inferResult = expr.inferParamClasses(null).finish();
            LSFClassSet classSet = expr.resolveInferredValueClass(inferResult);
            if (classSet != null && !(classSet instanceof StructClassSet)) {
                final LSFValueClass valueClass = classSet.getCommonClass();
                LSFStructureViewNavigationHandler navigationHandler = new LSFStructureViewNavigationHandler() {
                    @Override
                    public void navigate(LSFPropertyStatementTreeElement element, boolean requestFocus) {
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

        private void insertComposition(final Editor editor, final LSFExpression expr, final LSFValueClass valueClass, final LSFPropertyStatement composition) {
            ApplicationManager.getApplication().runWriteAction(new Runnable() {
                @Override
                public void run() {
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
                }
            });
        }

        @Override
        public void invoke(@NotNull Project project, @NotNull PsiElement[] elements, DataContext dataContext) {
            throw new IllegalStateException("Shouldn't be called");
        }
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
