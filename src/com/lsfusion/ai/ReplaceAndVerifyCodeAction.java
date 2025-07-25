package com.lsfusion.ai;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.command.undo.UndoManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.FileStatus;
import com.intellij.openapi.vcs.FileStatusManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtilBase;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.util.messages.MessageBusConnection;
import com.lsfusion.actions.ShowErrorsAction;
import com.lsfusion.lang.LSFFileType;
import com.lsfusion.lang.LSFReferenceAnnotator;
import com.lsfusion.lang.meta.MetaChangeDetector;
import com.lsfusion.lang.psi.LSFFile;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

import static java.lang.Math.min;

public class ReplaceAndVerifyCodeAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
//        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();

        if (project != null && editor != null) {
            Document document = editor.getDocument();
            SelectionModel selectionModel = editor.getSelectionModel();

            int startLine = document.getLineNumber(selectionModel.getSelectionStart());
            int endLine = document.getLineNumber(selectionModel.getSelectionEnd());
            String textToInsert = "testProp = DATA LOCAL BOOLEAN ();            \n" +
                    "notTestProp = TRUE IF NOT testProp();   \n" +
                    "FORM test3                       \n" +
                    "    OBJECTS params = (dtFrom = DATE, dtTo = DATE) PANEL,\n" +
                    "            ds = Class1 PANEL\n" +
                    "    PROPERTIES objFrom = VALUE(dtFrom), objTo = VALUE(dtTo), testProp()\n" +
                    "    PROPERTIES(ds) SELECTOR name, n2 = name, n3 = name\n" +
                    ";";

            performReplace(project, editor, startLine, endLine, textToInsert);
        }

    }

    public void performReplace(Project project, Editor editor, int startLine, int endLine, String textToInsert) {
//        final PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
        VirtualFile originalVF = editor.getVirtualFile();
        if (originalVF.getFileType() instanceof LSFFileType) {
            Document document = editor.getDocument();

            LSFFile originalLsfFile = (LSFFile) PsiUtilBase.getPsiFileInEditor(editor, project);

            // Разворачиваем все метакоды с диалогом подтверждения, как в ShowErrorsAction
//            boolean enabledMeta = MetaChangeDetector.getInstance(project).getMetaEnabled();
//            if (!enabledMeta) {
//                if(JOptionPane.showConfirmDialog(JOptionPane.getRootFrame(),
//                        "Meta code is disabled. You must enable meta before showing errors", "Errors search",
//                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
//                    ActionUtil.performActionDumbAwareWithCallbacks(new MetaCodeEnableAction(), e);
//                    enabledMeta = MetaChangeDetector.getInstance(project).getMetaEnabled();
//                }
//            }
//            if(enabledMeta) {}


            boolean hadEnabledMetaCode = originalLsfFile.hasEnabledMetaCode();

            // Ненужная проверка, если заставляем всё разворачивать. Аналогичные ниже
            if (hadEnabledMetaCode) {
                // если получится сделать через VirtualFile, то сворачивать, конечно, лучше сразу в нём
                MetaChangeDetector.getInstance(project).reprocessFile(originalLsfFile, false);
            }

            // замена в текущем редакторе. работает, только вероятно всё же нужно развернуть сперва все метакоды
//                ApplicationManager.getApplication().invokeLater(() -> {
//
//                    WriteCommandAction.runWriteCommandAction(editor.getProject(), () -> {
//                        int lastLine = document.getLineCount() - 1;
//                        replaceLines(editor, min(startLine, lastLine), min(endLine, lastLine), textToInsert);
//                    });
//
//                    if (hadEnabledMetaCode) {
//                        MetaChangeDetector.getInstance(project).reprocessFile(originalLsfFile, true);
//                    }
//
////                    PsiDocumentManager.getInstance(project).commitAllDocuments();
//                    DaemonCodeAnalyzer.getInstance(project).restart();
//
//                    MessageBusConnection connection = project.getMessageBus().connect();
//                    connection.subscribe(DaemonCodeAnalyzer.DAEMON_EVENT_TOPIC, new DaemonCodeAnalyzer.DaemonListener() {
//                        @Override
//                        public void daemonFinished() {
//                            findErrors(project);
//                            connection.disconnect();
//                        }
//                    });
//                });




            // Попытка сделать замену в VirtualFile или типа того
            ApplicationManager.getApplication().invokeLater(() -> {
                Document newDocument = new DocumentImpl(document.getCharsSequence());
                String originalText = document.getText();

                WriteCommandAction.runWriteCommandAction(editor.getProject(), () -> {
                    int lastLine = newDocument.getLineCount() - 1;
                    replaceLines(newDocument, min(startLine, lastLine), min(endLine, lastLine), textToInsert);

                    LightVirtualFile newVF = new LightVirtualFile(
                            originalVF.getName(),
                            originalVF.getFileType(),
                            newDocument.getCharsSequence()
                    );


                    // 1. VirtualFile не изменяет
//                    ApplicationManager.getApplication().runWriteAction(() -> {
//                        document.setText(newVF.getContent().toString());
//                    });
//                    VirtualFileManager.getInstance().refreshWithoutFileWatcher(true);


                    // 2. так меняется содержимое в редакторе
//                    VirtualFileManager vfManager = VirtualFileManager.getInstance();
//                    VirtualFile vFile = vfManager.refreshAndFindFileByUrl(originalVF.getUrl());
//                    if (vFile != null) {
//                        try {
//                            vFile.setBinaryContent(newVF.contentsToByteArray());
//                        } catch (IOException ex) {
//                            throw new RuntimeException(ex);
//                        }
////                        vFile.setBinaryContent(newContent.getBytes(StandardCharsets.UTF_8));
//                    }




                    // возможно следует пробовать создать полностью новую виртуальную систему:
//                    TempFileSystem tempFs = TempFileSystem.getInstance();
//                    tempFs.createIfNotExists();



                    // DaemonCodeAnalyzer по идее может сработать если из нового файла просто создан PsiFile
                    PsiManager psiManager = PsiManager.getInstance(project);
                    LSFFile newLsfFile = (LSFFile) psiManager.findFile(newVF);


                    // разворачиваем метакоды в файле
                    MetaChangeDetector.getInstance(project).reprocessFile(newLsfFile, true);

                    // scope для сбора ошибок. предполагаю, что нужно исключить оригинальный файл и включить новый
                    // в таком виде там VirtualFile, аналогичный по содержимому оригиналу
                    GlobalSearchScope newScope = GlobalSearchScope.projectScope(project)
                            .uniteWith(GlobalSearchScope.fileScope(project, newVF))
                            .uniteWith(GlobalSearchScope.projectScope(project).intersectWith(GlobalSearchScope.notScope(GlobalSearchScope.fileScope(project, originalVF))));


                    // запускаем анализатор и подписываемся на окончание анализа
//                    PsiDocumentManager.getInstance(project).commitAllDocuments();
                    // c этими 2 строками почему-то не срабатывает DaemonListener
//                    DaemonCodeAnalyzer.getInstance(project).setHighlightingEnabled(originalLsfFile, false);
//                    DaemonCodeAnalyzer.getInstance(project).setHighlightingEnabled(newLsfFile, true);
                    DaemonCodeAnalyzer.getInstance(project).restart();
                    // слушаем окончание анализа кода
                    MessageBusConnection connection = project.getMessageBus().connect();
                    connection.subscribe(DaemonCodeAnalyzer.DAEMON_EVENT_TOPIC, new DaemonCodeAnalyzer.DaemonListener() {
                        @Override
                        public void daemonFinished() {
                            // собираем ошибки после окончания работы анализатора
//                            findErrors(project);
                            findErrors(project, newScope);
                            connection.disconnect();
                        }
                    });

                    // при необходимости возвращаем состояние

                    // 1.
//                    ApplicationManager.getApplication().runWriteAction(() -> {
//                        document.setText(originalText);
//                    });


                    // 2.
//                    if (vFile != null) {
//                        try {
//                            vFile.setBinaryContent(originalVF.contentsToByteArray());
//                        } catch (IOException ex) {
//                            throw new RuntimeException(ex);
//                        }
//                    }
                });

                // опять же возможно не нужно
                if (hadEnabledMetaCode) {
                    MetaChangeDetector.getInstance(project).reprocessFile(originalLsfFile, true);
                }
            });
        }
    }

    private String findErrors(Project project) {
        return findErrors(project, GlobalSearchScope.projectScope(project));
    }

    private String findErrors(Project project, GlobalSearchScope scope) {
        Collection<VirtualFile> files = FileTypeIndex.getFiles(LSFFileType.INSTANCE, scope);
        LSFReferenceAnnotator annotator = new LSFReferenceAnnotator(true, false, true);

        for (VirtualFile vfile : files) {
            if (FileStatusManager.getInstance(project).getStatus(vfile) == FileStatus.IGNORED) {
                continue;
            }
            PsiFile lsfFile = PsiManager.getInstance(project).findFile(vfile);
            if (lsfFile != null) {
                annotator.annotateRecursively(lsfFile, null);
            }
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (ShowErrorsAction.ErrorMessage errorMessage : annotator.getStringMessages()) {
            if (errorMessage != null) {
                stringBuilder.append("Error in ")
                        .append(errorMessage.moduleName)
                        .append(" module (")
                        .append(errorMessage.filePositionText)
                        .append("): ")
                        .append(errorMessage.message)
                        .append("\n");
            }
        }
        System.out.println(stringBuilder);
        return stringBuilder.toString();
    }

    public void replaceLines(Editor editor, int startLine, int endLine, String text) {
        int startOffset = editor.getDocument().getLineStartOffset(startLine);
        int endOffset = editor.getDocument().getLineEndOffset(endLine);
        replaceText(editor, startOffset, endOffset, text);
    }

    public void replaceLines(Document document, int startLine, int endLine, String text) {
        int startOffset = document.getLineStartOffset(startLine);
        int endOffset = document.getLineEndOffset(endLine);
        replaceText(document, startOffset, endOffset, text);
    }

    public void replaceText(Editor editor, int start, int end, String text) {
        editor.getDocument().replaceString(start, end, text);
        editor.getCaretModel().moveToOffset(start + text.length());
    }

    public void replaceText(Document document, int start, int end, String text) {
        document.replaceString(start, end, text);
    }

    public void undoLastChange(Project project) {
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor != null) {
            undoLastChange(project, editor);
        }
    }

    public void undoLastChange(Project project, Editor editor) {
        ApplicationManager.getApplication().invokeLater(() -> {
            UndoManager undoManager = UndoManager.getInstance(project);
            if (editor instanceof FileEditor) {
                if (undoManager.isUndoAvailable((FileEditor) editor)) {
                    undoManager.undo((FileEditor) editor);
                }
            }
        });
    }
}
