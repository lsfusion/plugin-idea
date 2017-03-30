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
import com.lsfusion.lang.psi.declarations.LSFGlobalPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFGroupObjectDeclaration;
import com.lsfusion.lang.psi.declarations.LSFObjectDeclaration;
import com.lsfusion.lang.psi.declarations.LSFPropDeclaration;
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
//        if (element instanceof LSFFormMappedNamePropertiesList) {
//            LSFFormMappedNamePropertiesList statement = (LSFFormMappedNamePropertiesList) element;
//            changeCode(transaction, statement, clauseAdded);
//        }
        if (element instanceof LSFFormMappedPropertiesList) {
            LSFFormMappedPropertiesList statement = (LSFFormMappedPropertiesList) element;
            changeCode(transaction, statement, clauseAdded);
        }

        for (PsiElement child : element.getChildren())
            recChangeCode(transaction, child, clauseAdded);
    }

//    static private void changeCode(MetaTransaction transaction, LSFListFormDeclaration listDecl) {
//        ASTNode dialogNode = listDecl.getNode().getFirstChildNode();
//        if(dialogNode.getText().equals("DIALOG")) {
//            ASTNode listToken = LSFElementGenerator.createListToken(listDecl.getProject());
//            if (transaction != null) {
//                transaction.regChange(BaseUtils.toList(listToken), dialogNode, MetaTransaction.Type.REPLACE);
//            }
//            listDecl.getNode().replaceChild(dialogNode, listToken);            
//        }
//    }
//    
//    static private void changeCode(MetaTransaction transaction, LSFFormActionPropertyDefinitionBody listDecl) {
//        ASTNode dialogNode = listDecl.getNode().getFirstChildNode();
//        if(dialogNode.getText().equals("FORM")) {
//            ASTNode listToken = LSFElementGenerator.createShowToken(listDecl.getProject());
//            if (transaction != null) {
//                transaction.regChange(BaseUtils.toList(listToken), dialogNode, MetaTransaction.Type.REPLACE);
//            }
//            listDecl.getNode().replaceChild(dialogNode, listToken);            
//        }
//    }
    
    static String getViewType(LSFFormPropertyOptionsList optionsList) {
        String result = null;
        for(LSFFormOptionForce option : optionsList.getFormOptionForceList())
            result = option.getText();
        return result;
    }
    
    static void updateViewType(LSFFormPropertyOptionsList optionsList, String resultViewType, MetaTransaction transaction) {
        boolean found = false;
        for(LSFFormOptionForce formOption : optionsList.getFormOptionForceList()) {
            if(resultViewType == null || !formOption.getText().equals(resultViewType)) { // удаляем
                ASTNode optionNode = formOption.getNode();
                if(optionNode.getTreeNext() != null && LSFParserDefinition.isWhiteSpaceOrComment((optionNode.getTreeNext()).getElementType())) { // пробел 
                    if (transaction != null) {
                        transaction.regChange(new ArrayList<>(), optionNode.getTreeNext(), MetaTransaction.Type.REPLACE);
                    }
                    optionsList.getNode().removeChild(optionNode.getTreeNext());
                }
                if (transaction != null) {
                    transaction.regChange(new ArrayList<>(), optionNode, MetaTransaction.Type.REPLACE);
                }
                optionsList.getNode().removeChild(optionNode);
            } else
                found = true;
        }
        
        if(resultViewType != null && !found) {
            if (optionsList.getNode().getText().isEmpty()) {
                LSFFormPropertyOptionsList newOptionList = LSFElementGenerator.createFormPropertyOptionsList(optionsList.getProject(), resultViewType);
                LeafElement whitespace = ASTFactory.whitespace(" ");

                ASTNode treeNext = optionsList.getNode().getTreeNext(); // есть правило после
                
                if(treeNext != null) {
                    if (transaction != null) {
                        transaction.regChange(BaseUtils.toList(newOptionList.getNode(), whitespace), treeNext, MetaTransaction.Type.BEFORE);
                    }
                    optionsList.getParent().getNode().addChild(whitespace, treeNext);
                    optionsList.getParent().getNode().addChild(newOptionList.getNode(), whitespace);
                } else {
                    ASTNode treePrev = optionsList.getNode().getTreePrev(); // есть правило до
                    if( LSFParserDefinition.isWhiteSpaceOrComment(treePrev.getElementType())) {
                        if (transaction != null) {
                            transaction.regChange(BaseUtils.toList(whitespace, newOptionList.getNode()), treePrev, MetaTransaction.Type.BEFORE);
                        }
                        optionsList.getParent().getNode().addChild(newOptionList.getNode(), treePrev);
                        optionsList.getParent().getNode().addChild(whitespace, newOptionList.getNode());
                    } else {
                        if (transaction != null) {
                            transaction.regChange(BaseUtils.toList(whitespace, newOptionList.getNode()), MetaTransaction.findLeafToken(treePrev), MetaTransaction.Type.AFTER);
                        }
                        optionsList.getParent().getNode().addChild(whitespace);
                        optionsList.getParent().getNode().addChild(newOptionList.getNode());
                    }
                }
            } else {
                LSFFormOptionForce newOption = LSFElementGenerator.createFormOptionForce(optionsList.getProject(), resultViewType);
                LeafElement whitespace = ASTFactory.whitespace(" ");
                if (transaction != null) {
                    transaction.regChange(BaseUtils.toList(whitespace, newOption.getNode()), MetaTransaction.findLeafToken(optionsList.getNode()), MetaTransaction.Type.AFTER);
                }
                optionsList.getNode().addChild(whitespace);
                optionsList.getNode().addChild(newOption.getNode());
            }
        }
    }
    
    static private String getOViewType(LSFObjectUsageList usageList, LSFFormPropertyOptionsList declPropertyOptionsList, LSFFormPropertyOptionsList blockPropertyOptionsList) {
        LSFNonEmptyObjectUsageList nonEmptyObjectUsageList = usageList.getNonEmptyObjectUsageList();
        if(nonEmptyObjectUsageList == null)
            return "PANEL";

        LSFGroupObjectDeclaration groupObjectDecl = getExplicitToDraw(declPropertyOptionsList, blockPropertyOptionsList);
        if(groupObjectDecl == null)
            groupObjectDecl = getApplyGroupObject(nonEmptyObjectUsageList);

        LSFFormGroupObjectDeclaration parent = PsiTreeUtil.getParentOfType(groupObjectDecl, LSFFormGroupObjectDeclaration.class);
        if(parent == null) // дерево
            return "GRID";

        List<LSFFormGroupObjectViewType> viewTypeList = parent.getFormGroupObjectOptions().getFormGroupObjectViewTypeList();
        if(viewTypeList.size() > 0)
            return viewTypeList.iterator().next().getText();
        return "GRID";
    }

    private static LSFGroupObjectDeclaration getExplicitToDraw(LSFFormPropertyOptionsList declPropertyOptionsList, LSFFormPropertyOptionsList blockPropertyOptionsList) {
        List<LSFFormOptionToDraw> formOptionToDrawList = declPropertyOptionsList.getFormOptionToDrawList();
        if(formOptionToDrawList.isEmpty())
            formOptionToDrawList = blockPropertyOptionsList.getFormOptionToDrawList();
        if(!formOptionToDrawList.isEmpty())
            return formOptionToDrawList.get(0).getGroupObjectUsage().resolveDecl();
        return null;
    }

    private static LSFGroupObjectDeclaration getApplyGroupObject(LSFNonEmptyObjectUsageList nonEmptyObjectUsageList) {
        LSFFile maxFile = null;
        int maxOffset = -1;
        LSFObjectDeclaration maxDecl = null;
        for(LSFObjectUsage objectUsage : nonEmptyObjectUsageList.getObjectUsageList()) {
            LSFObjectDeclaration lsfObjectDeclaration = objectUsage.resolveDecl();
            if(lsfObjectDeclaration == null)
                continue;
            LSFFile objectFile = lsfObjectDeclaration.getLSFFile();
            int objectOffset = lsfObjectDeclaration.getTextOffset();
            if(maxFile == null) {
                maxFile = objectFile;
                maxOffset = objectOffset;
                maxDecl = lsfObjectDeclaration;
            } else {
                if(maxFile.getName().equals(objectFile.getName())) {
                    if(maxOffset < objectOffset) {
                        maxOffset = objectOffset;
                        maxDecl = lsfObjectDeclaration;
                    }                        
                } else {
                    if(objectFile.getRequireScope().contains(maxFile.getVirtualFile())) {
                        maxFile = objectFile;
                        maxOffset = objectOffset;
                        maxDecl = lsfObjectDeclaration;
                    }
                }                    
            }
        }
        return PsiTreeUtil.getParentOfType(maxDecl, LSFFormCommonGroupObject.class);
    }

    static private void changeCode(MetaTransaction transaction, LSFFormMappedPropertiesList list, Map<PsiElement, Set<String>> clauseAdded) {
        List<LSFFormPropertyDrawMappedDecl> properties = list.getFormPropertyDrawMappedDeclList();

        LSFFormPropertyOptionsList optionList = ((LSFFormPropertiesList) list.getParent()).getFormPropertyOptionsList();
        String blockType = getViewType(optionList);

        String[] options = new String[]{"PANEL", "GRID", "TOOLBAR"};

        Map<LSFFormPropertyDrawMappedDecl, String> toBe = new HashMap<>();
        Map<LSFFormPropertyDrawMappedDecl, String> defViewTypes = new HashMap<>();
        for (LSFFormPropertyDrawMappedDecl decl : properties) {

            String defType = null;
            String prevType = null;

            String oViewType = getOViewType(decl.getObjectUsageList(), decl.getFormPropertyOptionsList(), optionList);
            if (oViewType.contains("PANEL")) {
                defType = "PANEL";
            } else {
                LSFFormPropertyName name = decl.getFormPropertyName();
                LSFPropertyUsage propertyUsage = name.getPropertyUsage();
                if (propertyUsage != null) {
                    LSFPropertyStatement propDecl = (LSFPropertyStatement) propertyUsage.resolveDecl();
                    if (propDecl != null) {
                        LSFPropertyOptions propertyOptions = propDecl.getPropertyOptions();
                        if (propertyOptions != null) {
                            for (LSFViewTypeSetting viewType : propertyOptions.getViewTypeSettingList()) {
                                defType = viewType.getText();
                            }
                        }

                        if (defType == null) {
                            boolean direct = propertyUsage.isDirect();
                            if (propDecl.getActionStatement() != null) {
                                defType = "PANEL";
                                if (direct)
                                    prevType = "GRID";
                            } else {
                                defType = "GRID";
                                if (!direct)
                                    prevType = "PANEL";
                            }
                        }
                    } else
                        defType = "GRID";
                } else {
                    LSFPredefinedFormPropertyName predef = name.getPredefinedFormPropertyName();
                    if (predef.getText().equals("OBJVALUE") || predef.getText().equals("SELECTION"))
                        defType = "GRID";
                    else {
                        defType = "TOOLBAR";
                        if (predef.getText().equals("DELETE"))
                            prevType = "GRID";
                    }
                }
            }
            defViewTypes.put(decl, defType);

            String declToBe = null;
            String pdrawType = getViewType(decl.getFormPropertyOptionsList());
            if (pdrawType != null)
                declToBe = pdrawType;
            else {
                if (blockType != null) {
                    if (blockType.equals("PANEL") && defType.equals("TOOLBAR"))
                        declToBe = defType;
                    else
                        declToBe = blockType;
                } else {
                    if (prevType != null)
                        declToBe = prevType;
                    else
                        declToBe = defType;
                }
            }
            toBe.put(decl, declToBe);
        }

        // два варианта прописываем blockType, и не прописываем blockType
        // собственно переберем варианты
        int minNotNullWords = Integer.MAX_VALUE;
        String minResultBlock = null;
        Map<LSFFormPropertyDrawMappedDecl, String> minResultProps = null;

        // с блоком
        for (String blockOption : options) {
            int notNullWords = 1;
            Map<LSFFormPropertyDrawMappedDecl, String> resultProps = new HashMap<>();
            for (Map.Entry<LSFFormPropertyDrawMappedDecl, String> entry : toBe.entrySet()) {
                resultProps.put(entry.getKey(), null);
                if (!entry.getValue().equals(blockOption)) {
                    notNullWords++;
                    resultProps.put(entry.getKey(), entry.getValue());
                }
            }
            if (minNotNullWords > notNullWords) {
                minNotNullWords = notNullWords;
                minResultBlock = blockOption;
                minResultProps = resultProps;
            }
        }

        // без блока
        int notNullWords = 0;
        Map<LSFFormPropertyDrawMappedDecl, String> resultProps = new HashMap<>();
        for (Map.Entry<LSFFormPropertyDrawMappedDecl, String> entry : toBe.entrySet()) {
            resultProps.put(entry.getKey(), null);
            if (!entry.getValue().equals(defViewTypes.get(entry.getKey()))) {
                resultProps.put(entry.getKey(), entry.getValue());
                notNullWords++;
            }
        }
        if (minNotNullWords > notNullWords || (minNotNullWords == notNullWords && blockType == null)) { // если не было блока то и не добавляем
            minNotNullWords = notNullWords;
            minResultBlock = null;
            minResultProps = resultProps;
        }

        updateViewType(optionList, minResultBlock, transaction);
        for (Map.Entry<LSFFormPropertyDrawMappedDecl, String> minResultProp : minResultProps.entrySet())
            updateViewType(minResultProp.getKey().getFormPropertyOptionsList(), minResultProp.getValue(), transaction);
    }

    static private void changeCode(MetaTransaction transaction, LSFFormMappedNamePropertiesList list, Map<PsiElement, Set<String>> clauseAdded) {
        List<LSFFormPropertyDrawNameDecl> properties = list.getFormPropertiesNamesDeclList().getFormPropertyDrawNameDeclList();
        
        String blockType = getViewType(list.getFormPropertyOptionsList());
        
        String[] options = new String[]{"PANEL", "GRID", "TOOLBAR"};
        
        Map<LSFFormPropertyDrawNameDecl, String> toBe = new HashMap<>();
        Map<LSFFormPropertyDrawNameDecl, String> defViewTypes = new HashMap<>();
        for(LSFFormPropertyDrawNameDecl decl : properties) {

            String defType = null;
            String prevType = null;
            
            String oViewType = getOViewType(decl.getObjectUsageList(), decl.getFormPropertyOptionsList(), list.getFormPropertyOptionsList());
            if(oViewType.contains("PANEL")) {
                defType = "PANEL";
            } else {
                LSFFormPropertyName name = decl.getFormPropertyName();
                LSFPropertyUsage propertyUsage = name.getPropertyUsage();
                if (propertyUsage != null) {
                    LSFPropertyStatement propDecl = (LSFPropertyStatement) propertyUsage.resolveDecl();
                    if (propDecl != null) {
                        LSFPropertyOptions propertyOptions = propDecl.getPropertyOptions();
                        if (propertyOptions != null) {
                            for (LSFViewTypeSetting viewType : propertyOptions.getViewTypeSettingList()) {
                                defType = viewType.getText();
                            }
                        }

                        if (defType == null) {
                            boolean direct = propertyUsage.isDirect();
                            if (propDecl.getActionStatement() != null) {
                                defType = "PANEL";
                                if (direct)
                                    prevType = "GRID";
                            } else {
                                defType = "GRID";
                                if (!direct)
                                    prevType = "PANEL";
                            }
                        }
                    } else
                        defType = "GRID";
                } else {
                    LSFPredefinedFormPropertyName predef = name.getPredefinedFormPropertyName();
                    if (predef.getText().equals("OBJVALUE") || predef.getText().equals("SELECTION"))
                        defType = "GRID";
                    else {
                        defType = "TOOLBAR";
                        if (predef.getText().equals("DELETE"))
                            prevType = "GRID";
                    }
                }
            }
            defViewTypes.put(decl, defType);

            String declToBe = null;
            String pdrawType = getViewType(decl.getFormPropertyOptionsList());
            if(pdrawType != null)
                declToBe = pdrawType;
            else {
                if (blockType != null) {
                    if(blockType.equals("PANEL") && defType.equals("TOOLBAR"))
                        declToBe = defType;
                    else
                        declToBe = blockType;
                } else {
                    if(prevType != null)
                        declToBe = prevType;
                    else
                        declToBe = defType;
                }
            }
            toBe.put(decl, declToBe);
        }

        // два варианта прописываем blockType, и не прописываем blockType
        // собственно переберем варианты
        int minNotNullWords = Integer.MAX_VALUE;
        String minResultBlock = null;
        Map<LSFFormPropertyDrawNameDecl, String> minResultProps = null;

        // с блоком
        for(String blockOption : options) {
            int notNullWords = 1;
            Map<LSFFormPropertyDrawNameDecl, String> resultProps = new HashMap<>();
            for(Map.Entry<LSFFormPropertyDrawNameDecl, String> entry : toBe.entrySet()) {
                resultProps.put(entry.getKey(), null);
                if (!entry.getValue().equals(blockOption)) {
                    notNullWords++;
                    resultProps.put(entry.getKey(), entry.getValue());
                }
            }
            if(minNotNullWords > notNullWords) {
                minNotNullWords = notNullWords;
                minResultBlock = blockOption;
                minResultProps = resultProps;
            }
        }

        // без блока
        int notNullWords = 0;
        Map<LSFFormPropertyDrawNameDecl, String> resultProps = new HashMap<>();
        for(Map.Entry<LSFFormPropertyDrawNameDecl, String> entry : toBe.entrySet()) {
            resultProps.put(entry.getKey(), null);
            if(!entry.getValue().equals(defViewTypes.get(entry.getKey()))) {
                resultProps.put(entry.getKey(), entry.getValue());
                notNullWords++;
            }
        }
        if(minNotNullWords > notNullWords || (minNotNullWords == notNullWords && blockType == null)) { // если не было блока то и не добавляем
            minNotNullWords = notNullWords;
            minResultBlock = null;
            minResultProps = resultProps;
        }
        
        updateViewType(list.getFormPropertyOptionsList(), minResultBlock, transaction);
        for(Map.Entry<LSFFormPropertyDrawNameDecl, String> minResultProp : minResultProps.entrySet())
            updateViewType(minResultProp.getKey().getFormPropertyOptionsList(), minResultProp.getValue(), transaction);

//        boolean newSession = false;
//        boolean nestedSession = false;
//        String changeName;
//
//        String text = predefName.getText();
//        int brac = text.indexOf('[');
//        if(brac >= 0)
//            text = text.substring(0, brac);
//        
//        switch (text) {
//            case "ADDOBJ":
//                changeName = "NEW";
//                newSession = false;
//                nestedSession = false;
//                break;
//            case "ADDFORM":
//                changeName = "NEW";
//                newSession = true;
//                nestedSession = false;
//                break;
//            case "ADDNESTEDFORM":
//                changeName = "NEW";
//                newSession = true;
//                nestedSession = true;
//                break;
//            case "ADDSESSIONFORM":
//                changeName = "NEWEDIT";
//                newSession = false;
//                nestedSession = false;
//                break;
//            case "EDITFORM":
//                changeName = "EDIT";
//                newSession = true;
//                nestedSession = false;
//                break;
//            case "EDITNESTEDFORM":
//                changeName = "EDIT";
//                newSession = true;
//                nestedSession = true;
//                break;
//            case "EDITSESSIONFORM":
//                changeName = "EDIT";
//                newSession = false;
//                nestedSession = false;
//                break;
//            case "DELETE":
//                changeName = null;
//                newSession = true;
//                nestedSession = false;
//                break;
//            case "DELETESESSION":
//                changeName = "DELETE";
//                newSession = false;
//                nestedSession = false;
//                break;
//            default:
//                return;
//        }
//        
//        // изменяем имя
//        if(true) { // добавляем префикс
//            PsiElement parentElement = null;
//            ASTNode placeForOptionList = null;
//            LSFFormPropertyOptionsList optionsList = null;
//            
//            LSFFormMappedNamePropertiesList formNameList = PsiTreeUtil.getParentOfType(predefName, LSFFormMappedNamePropertiesList.class);
//            if(formNameList != null) {
//                parentElement = formNameList;
//                optionsList = formNameList.getFormPropertyOptionsList();
////                if(optionsList == null) {
////                    ASTNode textNode = formNameList.getObjectUsageList().getNode();
////                    while( LSFParserDefinition.isWhiteSpaceOrComment((textNode = textNode.getTreeNext()).getElementType()));
////                    assert textNode.toString().equals(")");
////                    textNode.getTreeNext()
////                }
//            } else {
//                LSFFormPropertiesList propertiesList = PsiTreeUtil.getParentOfType(predefName, LSFFormPropertiesList.class);
//                if(propertiesList != null) {
//                    parentElement = propertiesList;
//                    optionsList = propertiesList.getFormPropertyOptionsList();
//                }
//            }
//            if(parentElement != null && optionsList != null) {
//                List<LSFFormOptionSession> list = optionsList.getFormOptionSessionList();
//                
//                String clause = newSession ? (nestedSession ? "NESTEDSESSION" : "NEWSESSION") : "OLDSESSION"; 
//                
//                boolean hasClause = false;
//                
//                for(LSFFormOptionSession option : list)
//                    if(clause.equals(option.getText())) {
//                        hasClause = true;
//                        break;
//                    }
//                Set<String> addedClauses = clauseAdded.computeIfAbsent(parentElement, k -> new HashSet<>());
//                for(String option : addedClauses)
//                    if(clause.equals(option)) {
//                        hasClause = true;
//                        break;
//                    }
//
//                if(!hasClause) {
//                    if(!(list.isEmpty() && addedClauses.isEmpty()))
//                        System.out.println("NESTED + NEW : " + parentElement.getContainingFile().getName() + " " + parentElement.getTextRange() + " " + parentElement.getText());
//                    addedClauses.add(clause);
//
//                    if(newSession) {
//                        String optionString = optionsList.getText().trim();
////                    if(optionsList == null) {
////                        LeafElement whitespace = ASTFactory.whitespace(" ");
////                        parentElement.getNode().addChild(placeForOptionList, whitespace);
////                        parentElement.getNode().addChild(placeForOptionList, newOptionList.getNode());
////                    } else
//
//                        if (optionString.isEmpty()) {
//                            LSFFormPropertyOptionsList newOptionList = LSFElementGenerator.createFormPropertyOptionsList(predefName.getProject(), clause);
//                            LeafElement whitespace = ASTFactory.whitespace(" ");
//                            if (transaction != null) {
//                                transaction.regChange(BaseUtils.toList(newOptionList.getNode(), whitespace), optionsList.getNode().getTreeNext(), MetaTransaction.Type.BEFORE);
//                            }
//                            parentElement.getNode().addChild(whitespace, optionsList.getNode().getTreeNext());
//                            parentElement.getNode().addChild(newOptionList.getNode(), whitespace);
//                        } else {
//                            LSFFormOptionSession newOption = LSFElementGenerator.createFormOptionSession(predefName.getProject(), clause);
//                            LeafElement whitespace = ASTFactory.whitespace(" ");
//                            if (transaction != null) {
//                                transaction.regChange(BaseUtils.toList(whitespace, newOption.getNode()), optionsList.getNode().getTreeNext(), MetaTransaction.Type.BEFORE);
//                            }
//                            optionsList.getNode().addChild(whitespace);
//                            optionsList.getNode().addChild(newOption.getNode());
//                        }
//                    }
//                }
//            }            
//        }
//
//        if(changeName != null) {
//            LSFPredefinedAddPropertyName predefAdd = predefName.getPredefinedAddPropertyName();
//            if(predefAdd != null) {
//                LSFPredefinedAddPropertyName newPredefAdd = LSFElementGenerator.createPredefinedAddPropertyName(predefName.getProject(), changeName);
//                if (transaction != null) {
//                    transaction.regChange(BaseUtils.toList(newPredefAdd.getNode()), predefAdd.getNode(), MetaTransaction.Type.REPLACE);
//                }
//                predefName.getNode().replaceChild(predefAdd.getNode(),newPredefAdd.getNode());                
//            } else {
//                LSFPredefinedFormPropertyName newPredefName = LSFElementGenerator.createPredefinedFormPropertyName(predefName.getProject(), changeName);
//                if (transaction != null) {
//                    transaction.regChange(BaseUtils.toList(newPredefName.getNode()), predefName.getNode(), MetaTransaction.Type.REPLACE);
//                }
//                predefName.getNode().getTreeParent().replaceChild(predefName.getNode(), newPredefName.getNode());
//            }
//        }

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
