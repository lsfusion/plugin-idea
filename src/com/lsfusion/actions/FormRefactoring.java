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

import java.util.*;

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
                        Collection<VirtualFile> files = FileTypeIndex.getFiles(LSFFileType.INSTANCE, GlobalSearchScope.projectScope(project));
                        for (VirtualFile lsfFile : files) {
                            PsiFile psiFile = PsiManager.getInstance(project).findFile(lsfFile);
                            if (psiFile instanceof LSFFile) {
                                LSFFile file = (LSFFile) psiFile;
                                Map<PsiElement, Set<String>> clauseAdded = new HashMap<>();
                                for (PsiElement e : file.getStatements()) {
                                    recChangeCode(transaction, e, clauseAdded);
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
    
    static void recChangeCode(MetaTransaction transaction, PsiElement element, Map<PsiElement, Set<String>> clauseAdded) {
        if (element instanceof LSFPredefinedFormPropertyName) {
            LSFPredefinedFormPropertyName statement = (LSFPredefinedFormPropertyName) element;
            changeCode(transaction, statement, clauseAdded);
        }
        if (element instanceof LSFListFormDeclaration) {
            LSFListFormDeclaration statement = (LSFListFormDeclaration) element;
            changeCode(transaction, statement);
        }
        if (element instanceof LSFFormActionPropertyDefinitionBody) {
            LSFFormActionPropertyDefinitionBody statement = (LSFFormActionPropertyDefinitionBody) element;
            changeCode(transaction, statement);
        }

        for (PsiElement child : element.getChildren())
            recChangeCode(transaction, child, clauseAdded);
    }

    static private void changeCode(MetaTransaction transaction, LSFListFormDeclaration listDecl) {
        ASTNode dialogNode = listDecl.getNode().getFirstChildNode();
        if(dialogNode.getText().equals("DIALOG")) {
            ASTNode listToken = LSFElementGenerator.createListToken(listDecl.getProject());
            if (transaction != null) {
                transaction.regChange(BaseUtils.toList(listToken), dialogNode, MetaTransaction.Type.REPLACE);
            }
            listDecl.getNode().replaceChild(dialogNode, listToken);            
        }
    }
    
    static private void changeCode(MetaTransaction transaction, LSFFormActionPropertyDefinitionBody listDecl) {
        ASTNode dialogNode = listDecl.getNode().getFirstChildNode();
        if(dialogNode.getText().equals("FORM")) {
            ASTNode listToken = LSFElementGenerator.createShowToken(listDecl.getProject());
            if (transaction != null) {
                transaction.regChange(BaseUtils.toList(listToken), dialogNode, MetaTransaction.Type.REPLACE);
            }
            listDecl.getNode().replaceChild(dialogNode, listToken);            
        }
    }

    static private void changeCode(MetaTransaction transaction, LSFPredefinedFormPropertyName predefName, Map<PsiElement, Set<String>> clauseAdded) {
        
        boolean newSession = false;
        boolean nestedSession = false;
        String changeName;

        String text = predefName.getText();
        int brac = text.indexOf('[');
        if(brac >= 0)
            text = text.substring(0, brac);
        
        switch (text) {
            case "ADDOBJ":
                changeName = "NEW";
                newSession = false;
                nestedSession = false;
                break;
            case "ADDFORM":
                changeName = "NEW";
                newSession = true;
                nestedSession = false;
                break;
            case "ADDNESTEDFORM":
                changeName = "NEW";
                newSession = true;
                nestedSession = true;
                break;
            case "ADDSESSIONFORM":
                changeName = "NEWEDIT";
                newSession = false;
                nestedSession = false;
                break;
            case "EDITFORM":
                changeName = "EDIT";
                newSession = true;
                nestedSession = false;
                break;
            case "EDITNESTEDFORM":
                changeName = "EDIT";
                newSession = true;
                nestedSession = true;
                break;
            case "EDITSESSIONFORM":
                changeName = "EDIT";
                newSession = false;
                nestedSession = false;
                break;
            case "DELETE":
                changeName = null;
                newSession = true;
                nestedSession = false;
                break;
            case "DELETESESSION":
                changeName = "DELETE";
                newSession = false;
                nestedSession = false;
                break;
            default:
                return;
        }
        
        // изменяем имя
        if(true) { // добавляем префикс
            PsiElement parentElement = null;
            ASTNode placeForOptionList = null;
            LSFFormPropertyOptionsList optionsList = null;
            
            LSFFormMappedNamePropertiesList formNameList = PsiTreeUtil.getParentOfType(predefName, LSFFormMappedNamePropertiesList.class);
            if(formNameList != null) {
                parentElement = formNameList;
                optionsList = formNameList.getFormPropertyOptionsList();
//                if(optionsList == null) {
//                    ASTNode textNode = formNameList.getObjectUsageList().getNode();
//                    while( LSFParserDefinition.isWhiteSpaceOrComment((textNode = textNode.getTreeNext()).getElementType()));
//                    assert textNode.toString().equals(")");
//                    textNode.getTreeNext()
//                }
            } else {
                LSFFormPropertiesList propertiesList = PsiTreeUtil.getParentOfType(predefName, LSFFormPropertiesList.class);
                if(propertiesList != null) {
                    parentElement = propertiesList;
                    optionsList = propertiesList.getFormPropertyOptionsList();
                }
            }
            if(parentElement != null && optionsList != null) {
                List<LSFFormOptionSession> list = optionsList.getFormOptionSessionList();
                
                String clause = newSession ? (nestedSession ? "NESTEDSESSION" : "NEWSESSION") : "OLDSESSION"; 
                
                boolean hasClause = false;
                
                for(LSFFormOptionSession option : list)
                    if(clause.equals(option.getText())) {
                        hasClause = true;
                        break;
                    }
                Set<String> addedClauses = clauseAdded.computeIfAbsent(parentElement, k -> new HashSet<>());
                for(String option : addedClauses)
                    if(clause.equals(option)) {
                        hasClause = true;
                        break;
                    }

                if(!hasClause) {
                    if(!(list.isEmpty() && addedClauses.isEmpty()))
                        System.out.println("NESTED + NEW : " + parentElement.getContainingFile().getName() + " " + parentElement.getTextRange() + " " + parentElement.getText());
                    addedClauses.add(clause);

                    if(newSession) {
                        String optionString = optionsList.getText().trim();
//                    if(optionsList == null) {
//                        LeafElement whitespace = ASTFactory.whitespace(" ");
//                        parentElement.getNode().addChild(placeForOptionList, whitespace);
//                        parentElement.getNode().addChild(placeForOptionList, newOptionList.getNode());
//                    } else

                        if (optionString.isEmpty()) {
                            LSFFormPropertyOptionsList newOptionList = LSFElementGenerator.createFormPropertyOptionsList(predefName.getProject(), clause);
                            LeafElement whitespace = ASTFactory.whitespace(" ");
                            if (transaction != null) {
                                transaction.regChange(BaseUtils.toList(newOptionList.getNode(), whitespace), optionsList.getNode().getTreeNext(), MetaTransaction.Type.BEFORE);
                            }
                            parentElement.getNode().addChild(whitespace, optionsList.getNode().getTreeNext());
                            parentElement.getNode().addChild(newOptionList.getNode(), whitespace);
                        } else {
                            LSFFormOptionSession newOption = LSFElementGenerator.createFormOptionSession(predefName.getProject(), clause);
                            LeafElement whitespace = ASTFactory.whitespace(" ");
                            if (transaction != null) {
                                transaction.regChange(BaseUtils.toList(whitespace, newOption.getNode()), optionsList.getNode().getTreeNext(), MetaTransaction.Type.BEFORE);
                            }
                            optionsList.getNode().addChild(whitespace);
                            optionsList.getNode().addChild(newOption.getNode());
                        }
                    }
                }
            }            
        }

        if(changeName != null) {
            LSFPredefinedAddPropertyName predefAdd = predefName.getPredefinedAddPropertyName();
            if(predefAdd != null) {
                LSFPredefinedAddPropertyName newPredefAdd = LSFElementGenerator.createPredefinedAddPropertyName(predefName.getProject(), changeName);
                if (transaction != null) {
                    transaction.regChange(BaseUtils.toList(newPredefAdd.getNode()), predefAdd.getNode(), MetaTransaction.Type.REPLACE);
                }
                predefName.getNode().replaceChild(predefAdd.getNode(),newPredefAdd.getNode());                
            } else {
                LSFPredefinedFormPropertyName newPredefName = LSFElementGenerator.createPredefinedFormPropertyName(predefName.getProject(), changeName);
                if (transaction != null) {
                    transaction.regChange(BaseUtils.toList(newPredefName.getNode()), predefName.getNode(), MetaTransaction.Type.REPLACE);
                }
                predefName.getNode().getTreeParent().replaceChild(predefName.getNode(), newPredefName.getNode());
            }
        }

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

//    static private void changeCode(MetaTransaction transaction, LSFFormActionPropertyDefinitionBody formAction) {
        
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
//    }

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
