package com.lsfusion.actions;

import com.intellij.lang.ASTFactory;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.lang.LSFElementGenerator;
import com.lsfusion.lang.LSFFileType;
import com.lsfusion.lang.LSFParserDefinition;
import com.lsfusion.lang.meta.MetaChangeDetector;
import com.lsfusion.lang.meta.MetaTransaction;
import com.lsfusion.lang.psi.*;
import com.lsfusion.util.BaseUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class FormRefactoring extends com.intellij.openapi.actionSystem.AnAction {
//    static private void changeStatement(MetaTransaction transaction, LSFAddComponentStatement add) {
//        ASTNode addStatementNode = add.getNode();
//        ASTNode keyNode = addStatementNode.getFirstChildNode();
//
//        ASTNode moveNode = ASTFactory.leaf(LSFTypes.MOVE, "MOVE");
//        if (transaction != null) {
//            transaction.regChange(Collections.singletonList(moveNode), keyNode, MetaTransaction.Type.REPLACE);
//        }
//        addStatementNode.replaceChild(keyNode, moveNode);
//
//    }

    static public void changeCode(final Project project) {
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            public void run() {
                CommandProcessor.getInstance().runUndoTransparentAction(new Runnable() {
                    public void run() {
                        MetaChangeDetector.getInstance(project).setMetaEnabled(false, false);

                        MetaTransaction transaction = new MetaTransaction();
                        for (VirtualFile lsfFile : FileTypeIndex.getFiles(LSFFileType.INSTANCE, GlobalSearchScope.projectScope(project))) {
                            PsiFile psiFile = PsiManager.getInstance(project).findFile(lsfFile);
                            if (psiFile instanceof LSFFile) {
                                LSFFile file = (LSFFile) psiFile;
                                for (PsiElement e : file.getStatements()) {
                                    recChangeCode(transaction, e);
                                }
                            }
                        }
                        transaction.apply();
//                        MetaChangeDetector.getInstance(project).setMetaEnabled(true, true);
                    }
                });
            }
        });
    }
    
    static void recChangeCode(MetaTransaction transaction, PsiElement element) {
        if (element instanceof LSFActionPropertyDefinitionBody) {
            LSFActionPropertyDefinitionBody statement = (LSFActionPropertyDefinitionBody) element;
            LSFFormActionPropertyDefinitionBody formAction = statement.getFormActionPropertyDefinitionBody();
            if(formAction != null) 
                changeCode(transaction, formAction);
        }
        
        for (PsiElement child : element.getChildren())
            recChangeCode(transaction, child);
    }
    
    static private void changeCode(MetaTransaction transaction, LSFFormActionPropertyDefinitionBody formAction) {
        
//        ASTNode formNode = formAction.getNode();
//        ASTNode firstFormNode = formNode.getFirstChildNode();// FORM
//
////            FORM EXPORT -> EXPORT		EXPORT DOC, DOCX, PDF, XLS, XLSX-> PRINT DOC, DOCX TO formExportFile()
////            FORM PRINT -> PRINT
//        // ensureClass
//
//        // PRINT 
//        List<LSFFormPrintTypeLiteral> formPrintTypeLiteralList = formAction.getFormPrintTypeLiteralList();
//        if(!formPrintTypeLiteralList.isEmpty()) {
//            LSFFormPrintTypeLiteral formPrintType = BaseUtils.single(formPrintTypeLiteralList);
//            ASTNode formPrintTypeNode = formPrintType.getNode();
//
//            // переставляем 
//            ASTNode printNode = formPrintTypeNode.getFirstChildNode();  // PRINT
//            ASTNode printElement = LSFElementGenerator.createFormTypeFromText(formAction.getProject(), printNode.getText());
//            if (transaction != null) {
//                transaction.regChange(Collections.singletonList(printElement), firstFormNode, MetaTransaction.Type.REPLACE);
//            }
//            formNode.replaceChild(firstFormNode, printElement); // FORM -> PRINT
//            if(printNode.getTreeNext() != null && LSFParserDefinition.isWhiteSpaceOrComment((printNode.getTreeNext()).getElementType())) { // пробел 
//                if (transaction != null) {
//                    transaction.regChange(new ArrayList<>(), printNode.getTreeNext(), MetaTransaction.Type.REPLACE);
//                }
//                formPrintTypeNode.removeChild(printNode.getTreeNext());
//            }
//            if (transaction != null) {
//                transaction.regChange(new ArrayList<>(), printNode, MetaTransaction.Type.REPLACE);
//            }
//            formPrintTypeNode.removeChild(printNode);                
//        }
//
//        // EXPORT
//        List<LSFFormExportLiteral> formExportTypeLiteralList = formAction.getFormExportLiteralList();
//        if(!formExportTypeLiteralList.isEmpty()) {
//            LSFFormExportLiteral formExportType = BaseUtils.single(formExportTypeLiteralList);
//            ASTNode formExportTypeNode = formExportType.getNode();
//
//            ASTNode exportNode = formExportTypeNode.getFirstChildNode();  // EXPORT
//
//            ASTNode textNode = exportNode;
//            while( LSFParserDefinition.isWhiteSpaceOrComment((textNode = textNode.getTreeNext()).getElementType()));
//            String format = exportNode.getTreeNext().getTreeNext().getText(); // format
//            boolean changeToPrint = false;
//            if(format.equals("DOC") || format.equals("DOCX") || format.equals("XLS") || format.equals("XLSX") || format.equals("PDF")) {
//                changeToPrint = true;
//            }
//
//            // переставляем 
//            ASTNode replaceElement = LSFElementGenerator.createFormTypeFromText(formAction.getProject(), changeToPrint ? "PRINT" : exportNode.getText());
//            if (transaction != null) {
//                transaction.regChange(Collections.singletonList(replaceElement), firstFormNode, MetaTransaction.Type.REPLACE);
//            }
//            formNode.replaceChild(firstFormNode, replaceElement); // FORM -> PRINT
//            if (transaction != null) {
//                transaction.regChange(new ArrayList<>(), exportNode.getTreeNext(), MetaTransaction.Type.REPLACE);
//            }
//            formExportTypeNode.removeChild(exportNode.getTreeNext());
//            if (transaction != null) {
//                transaction.regChange(new ArrayList<>(), exportNode, MetaTransaction.Type.REPLACE);
//            }
//            formExportTypeNode.removeChild(exportNode);
//            if(changeToPrint) {
//                LeafElement whitespace = ASTFactory.whitespace(" ");
//                if (transaction != null) {
//                    transaction.regChange(Collections.singletonList(whitespace), formNode.getLastChildNode(), MetaTransaction.Type.AFTER);
//                }
//                formNode.addChild(whitespace);
//                ASTNode staticDestination = LSFElementGenerator.createStaticDestination(formAction.getProject()).getNode();
//                if (transaction != null) {
//                    transaction.regChange(Collections.singletonList(staticDestination), whitespace, MetaTransaction.Type.AFTER);
//                }
//                formNode.addChild(staticDestination);
//            }
//        }
//
//        List<LSFModalityTypeLiteral> modalityTypeLiteralList = formAction.getModalityTypeLiteralList();
//        if(!modalityTypeLiteralList.isEmpty()) {
//            LSFModalityTypeLiteral modalityType = BaseUtils.single(modalityTypeLiteralList);
//            String modalityTypeText = modalityType.getText();
//            switch (modalityTypeText) {
//                case "MODAL":
//                    if (transaction != null) {
//                        transaction.regChange(new ArrayList<>(), modalityType.getNode(), MetaTransaction.Type.REPLACE);
//                    }
//                    formNode.removeChild(modalityType.getNode());
//                    break;
//                case "DOCKEDMODAL":
//                    ASTNode node = LSFElementGenerator.createWindowType(formAction.getProject(), "DOCKED").getNode();
//                    if (transaction != null) {
//                        transaction.regChange(Collections.singletonList(node), modalityType.getNode(), MetaTransaction.Type.REPLACE);
//                    }
//                    formNode.replaceChild(modalityType.getNode(), node);
//                    break;
//            }
//        }
    }

//    static private void changeCode(MetaTransaction transaction, LSFDesignStatement statement) {
//        Collection<LSFAddComponentStatement> adds = PsiTreeUtil.findChildrenOfType(statement, LSFAddComponentStatement.class);
//        for (LSFAddComponentStatement add : adds) {
//            changeStatement(transaction, add);
//        }
//    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        changeCode(e.getProject());
    }
}
