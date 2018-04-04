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
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiElementProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.lang.LSFElementGenerator;
import com.lsfusion.lang.LSFFileType;
import com.lsfusion.lang.LSFParserDefinition;
import com.lsfusion.lang.meta.MetaChangeDetector;
import com.lsfusion.lang.meta.MetaTransaction;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.*;
import com.lsfusion.lang.psi.references.LSFExprParamReference;
import com.lsfusion.util.BaseUtils;
import com.lsfusion.util.LSFPsiUtils;
import org.jetbrains.annotations.NotNull;

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
                        int i=0;
                        for (VirtualFile lsfFile : files) {
                            if((i++)%10 == 0)
                                System.out.println("Proceeded " + i + " of " + files.size());
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
        if (element instanceof LSFPropertyExprObject) {
            LSFPropertyExprObject statement = (LSFPropertyExprObject) element;
            changeCode(transaction, (LSFPropertyExprObject) statement);
            return; // дальше нет
        }

        for (PsiElement child : element.getChildren())
            recChangeCode(transaction, child, clauseAdded);
    }
    
    private static void addTokens(List<ASTNode> nodes, MetaTransaction transaction, ASTNode anchorNode, boolean before) {
        if(!before) {
            ASTNode nextNode = findNextNode(anchorNode, false);
            if(nextNode == null) { // конец файла
                addEndTokens(nodes, transaction, anchorNode);
                return;
            } else
                anchorNode = nextNode;
        }

        ASTNode parentNode = anchorNode.getTreeParent();
        if (transaction != null) {
            transaction.regChange(nodes, anchorNode, MetaTransaction.Type.BEFORE);
        }
        for(int i=nodes.size()-1;i>=0;i--) {
            ASTNode node = nodes.get(i);
            parentNode.addChild(node, anchorNode);
            anchorNode = node;
        }
        //                    if (transaction != null) {
//                        transaction.regChange(BaseUtils.toList(newOptionList.getNode(), whitespace), treeNext, MetaTransaction.Type.BEFORE);
//                    }
//                    optionsList.getParent().getNode().addChild(whitespace, treeNext);
//                    optionsList.getParent().getNode().addChild(newOptionList.getNode(), whitespace);

    }

    private static void addEndTokens(List<ASTNode> nodes, MetaTransaction transaction, ASTNode anchorNode) {
        if (transaction != null) {
            transaction.regChange(nodes, anchorNode, MetaTransaction.Type.AFTER);
        }
        ASTNode parentNode = anchorNode.getTreeParent();
        for (ASTNode node : nodes) {
            parentNode.addChild(node);
        }
        return;
    }

    // onEdit - отдельно обработать ???
    static private void changeCode(MetaTransaction transaction, LSFPropertyExprObject partDef) {
//        LSFPropertyExpression pe = partDef.getPropertyCalcStatement().getPropertyExpression();
//        if(pe != null) {
//            LSFJoinPropertyDefinition joinDef = PsiTreeUtil.getParentOfType(partDef, LSFJoinPropertyDefinition.class);
//            LSFNonEmptyPropertyExpressionList peList = joinDef.getPropertyExpressionList().getNonEmptyPropertyExpressionList();
//            if (peList == null) { // если нет параметров
//                String newText = pe.getText();
//
//                LSFSimplePE joinPE = PsiTreeUtil.getParentOfType(partDef, LSFSimplePE.class);
//                if(joinPE == null) {
//                    joinPE = joinPE;
//                } else {
//                    LSFBracedPE newExpr = LSFElementGenerator.createBracedPE(joinPE.getProject(), newText);
//                    if (transaction != null) {
//                        transaction.regChange(MetaTransaction.getLeafTokens(newExpr.getNode()), joinPE.getExpressionPrimitive().getNode(), MetaTransaction.Type.REPLACE);
//                    }
//                    joinPE.getNode().replaceChild(joinPE.getExpressionPrimitive().getNode(), newExpr.getNode());
//                }
//            }
//        }
//                LSFExpressionUnfriendlyPD unfriend = statement.getPropertyStatementBody().getPropertyCalcStatement().getExpressionUnfriendlyPD(); 
//        if(unfriend != null) {
//            LSFGroupPropertyDefinition partDef = unfriend.getGroupPropertyDefinition();
//            if(partDef != null) {
//                LSFGroupPropertyBy groupBy = partDef.getGroupPropertyBy();
//                if(groupBy != null) {
//                    LSFNonEmptyPropertyExpressionList neList = groupBy.getNonEmptyPropertyExpressionList();
//                    List<LSFPropertyExpression> propertyExpressionList = neList.getPropertyExpressionList();
//                    boolean hasNotParameter = false;
//                    for (int i = 0; i < propertyExpressionList.size(); i++) {
//                        LSFPropertyExpression propertyExpression = propertyExpressionList.get(i);
//                        LSFExprParamReference paramRef = PsiTreeUtil.findChildOfType(propertyExpression, LSFExprParamReference.class);
//                        if (!(paramRef != null && propertyExpression.getText().trim().equals(paramRef.getText().trim()))) { // если это чисто объявление параметра
//                            hasNotParameter = true;
//                            break;
//                        }
//                    }
//
//                    if (!hasNotParameter) {
//                        String paramsList = neList.getText();
//
//                        // убираем BY
//                        ASTNode node = groupBy.getNode();
//                        ASTNode nnode = node.getTreeNext();
//                        removeElement(transaction, partDef.getNode(), node);
//                        removeWhiteSpaces(transaction, partDef.getNode(), nnode);
//
//                        // добавляем слева
//                        LSFPropertyDeclParams declParams = LSFElementGenerator.createPropertyDeclParams(groupBy.getProject(), "(" + paramsList + ")");
//                        LSFPropertyDeclaration propertyDeclaration = statement.getPropertyDeclaration();
//
//                        boolean addWhitespace = true;
//                        LSFPropertyDeclParams exDeclParams = propertyDeclaration.getPropertyDeclParams();
//                        if (exDeclParams != null) {
//                            addWhitespace = false;
//                            removeElement(transaction, propertyDeclaration.getNode(), exDeclParams.getNode());
//                        }
//
//                        List<ASTNode> leafNodes = MetaTransaction.getLeafTokens(propertyDeclaration.getSimpleNameWithCaption().getNode());
//                        ASTNode addLeafNode = leafNodes.get(leafNodes.size() - 1);
//                        if (addWhitespace) {
//                            // добавляем после слова GROUP новый GROUP BY  
//                            ASTNode wnode = ASTFactory.whitespace(" ");
//                            ;
//                            if (transaction != null) {
//                                transaction.regChange(BaseUtils.<ASTNode>toList(wnode), addLeafNode, MetaTransaction.Type.AFTER);
//                            }
//                            propertyDeclaration.getNode().addChild(wnode);
//
//                            addLeafNode = wnode;
//                        }
//
//                        if (transaction != null) {
//                            transaction.regChange(MetaTransaction.getLeafTokens(declParams.getNode()), addLeafNode, MetaTransaction.Type.AFTER);
//                        }
//                        propertyDeclaration.getNode().addChild(declParams.getNode());
//                    }
//                }
//            }
//        }
    }
//    // onEdit - отдельно обработать ???
//    static private void changeCode(MetaTransaction transaction, LSFPropertyStatement statement) {
//        LSFExpressionUnfriendlyPD unfriend = statement.getPropertyStatementBody().getPropertyCalcStatement().getExpressionUnfriendlyPD(); 
//        if(unfriend != null) {
//            LSFGroupPropertyDefinition partDef = unfriend.getGroupPropertyDefinition();
//            if(partDef != null) {
//                LSFGroupPropertyBy groupBy = partDef.getGroupPropertyBy();
//                if(groupBy != null) {
//                    LSFNonEmptyPropertyExpressionList neList = groupBy.getNonEmptyPropertyExpressionList();
//                    List<LSFPropertyExpression> propertyExpressionList = neList.getPropertyExpressionList();
//                    boolean hasNotParameter = false;
//                    for (int i = 0; i < propertyExpressionList.size(); i++) {
//                        LSFPropertyExpression propertyExpression = propertyExpressionList.get(i);
//                        LSFExprParamReference paramRef = PsiTreeUtil.findChildOfType(propertyExpression, LSFExprParamReference.class);
//                        if (!(paramRef != null && propertyExpression.getText().trim().equals(paramRef.getText().trim()))) { // если это чисто объявление параметра
//                            hasNotParameter = true;
//                            break;
//                        }
//                    }
//
//                    if (!hasNotParameter) {
//                        String paramsList = neList.getText();
//
//                        // убираем BY
//                        ASTNode node = groupBy.getNode();
//                        ASTNode nnode = node.getTreeNext();
//                        removeElement(transaction, partDef.getNode(), node);
//                        removeWhiteSpaces(transaction, partDef.getNode(), nnode);
//
//                        // добавляем слева
//                        LSFPropertyDeclParams declParams = LSFElementGenerator.createPropertyDeclParams(groupBy.getProject(), "(" + paramsList + ")");
//                        LSFPropertyDeclaration propertyDeclaration = statement.getPropertyDeclaration();
//
//                        boolean addWhitespace = true;
//                        LSFPropertyDeclParams exDeclParams = propertyDeclaration.getPropertyDeclParams();
//                        if (exDeclParams != null) {
//                            addWhitespace = false;
//                            removeElement(transaction, propertyDeclaration.getNode(), exDeclParams.getNode());
//                        }
//
//                        List<ASTNode> leafNodes = MetaTransaction.getLeafTokens(propertyDeclaration.getSimpleNameWithCaption().getNode());
//                        ASTNode addLeafNode = leafNodes.get(leafNodes.size() - 1);
//                        if (addWhitespace) {
//                            // добавляем после слова GROUP новый GROUP BY  
//                            ASTNode wnode = ASTFactory.whitespace(" ");
//                            ;
//                            if (transaction != null) {
//                                transaction.regChange(BaseUtils.<ASTNode>toList(wnode), addLeafNode, MetaTransaction.Type.AFTER);
//                            }
//                            propertyDeclaration.getNode().addChild(wnode);
//
//                            addLeafNode = wnode;
//                        }
//
//                        if (transaction != null) {
//                            transaction.regChange(MetaTransaction.getLeafTokens(declParams.getNode()), addLeafNode, MetaTransaction.Type.AFTER);
//                        }
//                        propertyDeclaration.getNode().addChild(declParams.getNode());
//                    }
//                }
//            }
//        }
//
//    }
    
//    static Set<String> existingRenames = new HashSet<>();
//    
//    static private void changeParams(PsiElement element, MetaTransaction transaction, Map<String, String> renameParams, Set<String> removeClasses, LSFPropertyExprObject partDef) {
//        List<LSFExprParamReference> paramRefs = new ArrayList<>();
//        PsiTreeUtil.processElements(element, new PsiElementProcessor() {
//            @Override
//            public boolean execute(@NotNull PsiElement element) {
//                if (element instanceof LSFExprParamReference && (partDef == null || PsiTreeUtil.getParentOfType(element, LSFPropertyExprObject.class).equals(partDef))) {
//                    paramRefs.add((LSFExprParamReference) element);
//                }
//                return true;
//            }
//        });
//        
//        for(LSFExprParamReference paramRef : paramRefs) {
//            String name = paramRef.getName();
//            if(removeClasses != null && removeClasses.contains(name))
//                removeClass(transaction, ((LSFExprParameterNameUsage)paramRef).getClassParamDeclare());
//            String rename = renameParams.get(name);
//            if(rename != null)
//                paramRef.handleElementRename(rename, transaction);
//        }
//    }
//    
//    static void removeAndRename(LSFNonEmptyPropertyExpressionList exprList, MetaTransaction transaction, List<Integer> removeParams, Set<String> removeClasses, Map<String, String> renameParams, LSFPropertyExprObject partDef) {
//        ASTNode exprNode = exprList.getNode();
//        List<LSFPropertyExpression> peList = exprList.getPropertyExpressionList();
//        int maxKeep = -1;
//        for(int i=0;i<peList.size();i++)
//            if(!removeParams.contains(i)) {                
//                maxKeep = i;
//            }
//        for(int i=0;i<peList.size();i++) {
//            LSFPropertyExpression pe = peList.get(i);
//            if(removeParams.contains(i)) {
//                // удаляем сам expr + если не последний следующие whitespace'ы + запятую
//                ASTNode node = pe.getNode();
//
//                ASTNode nnode = node.getTreeNext();
//
//                removeElement(transaction, exprNode, node);
//                if(i<peList.size() - 1) {
//                    removeCommaAndWhitespaces(transaction, exprNode, nnode);
//                }
//            } else {
//                if (renameParams != null)
//                    changeParams(pe, transaction, renameParams, removeClasses, partDef);
//                
//                if(i==maxKeep && i<peList.size()-1) // если последняя keep остальные вырезаем
//                    removeCommaAndWhitespaces(transaction, exprNode, pe.getNode().getTreeNext());
//            }
//        }
//    }
//
//    private static void removeCommaAndWhitespaces(MetaTransaction transaction, ASTNode exprNode, ASTNode nnode) {
//        nnode = removeWhiteSpaces(transaction, exprNode, nnode);
//
//        // assert
//        if(nnode.getElementType() == LSFTypes.COMMA) {
//            ASTNode nnnode = nnode.getTreeNext();
//            
//            removeElement(transaction, exprNode, nnode);
//            
//            removeWhiteSpaces(transaction, exprNode, nnnode);
//        } else {
//            assert false;
//        }
//    }
//
    private static ASTNode removeWhiteSpaces(MetaTransaction transaction, ASTNode exprNode, ASTNode nnode) {
        while (LSFParserDefinition.isWhiteSpaceOrComment(nnode.getElementType())) {
            ASTNode nnnode = nnode.getTreeNext();

            removeElement(transaction, exprNode, nnode);

            nnode = nnnode;
        }
        return nnode;
    }

    private static void removeElement(MetaTransaction transaction, ASTNode exprNode, ASTNode node) {
        if (transaction != null) {
            transaction.regChange(new ArrayList<>(), node, MetaTransaction.Type.REPLACE);
        }
        exprNode.removeChild(node);
    }
//
//    // onEdit - отдельно обработать ???
//    static private void changeCode(MetaTransaction transaction, LSFPropertyExprObject partDef) {
//
//        // в одну строку ACTION f(a); -> REPLACE({) f(a);ADD( }), 
//        //              ACTION f(a) ... pOptions ... ; -> REPLACE({) f(a)ADD(; }) ... ;
//
//        List<Integer> removeParams = new ArrayList<>();
//        Set<String> removeClasses = new HashSet<>();
//        boolean removeGroupBy = false;
//        LSFGroupPropertyBody body = null;
//        LSFGroupPropertyBy groupBy = null;
//
//        Map<String, String> renameParams = new HashMap<>();
//
//        List<LSFPropertyExpression> joinParams = new ArrayList<>();
//        LSFNonEmptyPropertyExpressionList peList = PsiTreeUtil.getParentOfType(partDef, LSFJoinPropertyDefinition.class).getPropertyExpressionList().getNonEmptyPropertyExpressionList();
//        if(peList != null)
//            joinParams = peList.getPropertyExpressionList();
//
//        List<LSFExprParamDeclaration> params = null;
//
//        LSFExpressionUnfriendlyPD unfrien = partDef.getPropertyCalcStatement().getExpressionUnfriendlyPD();
//        LSFGroupPropertyDefinition groupPropertyDefinition = null;
//        if(unfrien != null)  {
//            groupPropertyDefinition = unfrien.getGroupPropertyDefinition();
//            if(groupPropertyDefinition != null) { // если GROUP BY
//                
//                groupBy = groupPropertyDefinition.getGroupPropertyBy();
//                if(groupBy != null) {
//                    body = groupPropertyDefinition.getGroupPropertyBody();
//                    
//                    // assert'им что в BY нет параметров на переименование 
//                    
//                    List<LSFPropertyExpression> propertyExpressionList = groupBy.getNonEmptyPropertyExpressionList().getPropertyExpressionList();
//                    for (int i = 0; i < propertyExpressionList.size(); i++) {
//                        LSFPropertyExpression propertyExpression = propertyExpressionList.get(i);
//                        LSFExprParamReference paramRef = PsiTreeUtil.findChildOfType(propertyExpression, LSFExprParamReference.class);
//                        if (paramRef != null && propertyExpression.getText().trim().equals(paramRef.getText().trim())) { // если это чисто объявление параметра
//                            LSFPropertyExpression joinExpr = joinParams.get(i);
//                            LSFExprParamReference joinParamRef = PsiTreeUtil.findChildOfType(joinExpr, LSFExprParamReference.class);
//                            if (joinParamRef != null && joinExpr.getText().trim().equals(joinParamRef.getText().trim())) { // если это чисто объявление параметра                            
//                                LSFExprParamDeclaration joinDecl = joinParamRef.resolveDecl();
//                                if(joinDecl != null && isOuter(joinDecl, partDef)) { // если объявлено раньше
//                                    removeParams.add(i); // помечаем на remove параметр
//                                    removeClasses.add(paramRef.getName());
//                                    renameParams.put(paramRef.getName(), joinParamRef.getName());
//                                }
//                            }
//                        }
//                    }
//                    if(removeParams.size() == propertyExpressionList.size())
//                        removeGroupBy = true;
//                }
//            }
//        } else {
//            LSFPropertyExpression pe = partDef.getPropertyCalcStatement().getPropertyExpression();
//            if(pe != null) {
//                params = pe.resolveParams();
//                for (int i = 0; i < joinParams.size(); i++) {
//                    LSFPropertyExpression joinExpr = joinParams.get(i);
//                    LSFExprParamReference joinParamRef = PsiTreeUtil.findChildOfType(joinExpr, LSFExprParamReference.class);
//                    if (joinParamRef != null && joinExpr.getText().trim().equals(joinParamRef.getText().trim())) { // если это чисто объявление параметра                            
//                        LSFExprParamDeclaration joinDecl = joinParamRef.resolveDecl();
//                        if (joinDecl != null && isOuter(joinDecl, partDef)) { // если объявлено раньше
//                            removeParams.add(i);
//                            renameParams.put(params.get(i).getDeclName(), joinParamRef.getName());
//                        }
//                    }
//                }
//            }
//        }
//        
//        // removeParams из group by, остальные paramRef'ы переименовываем 
//
//        Set<String> upParams = new HashSet<>();
//        for(LSFExprParamDeclaration param : LSFPsiUtils.getContextParams(partDef, false)) {
//            upParams.add(param.getDeclName());
//        }
//        
//        Set<String> existingRenames = new HashSet<>();
//        List<LSFPropertyExprObject> innerExprs = new ArrayList<LSFPropertyExprObject>();
//        PsiTreeUtil.processElements(partDef.getPropertyCalcStatement(), new PsiElementProcessor() {
//            @Override
//            public boolean execute(@NotNull PsiElement element) {
//                if(element instanceof LSFPropertyExprObject && PsiTreeUtil.getParentOfType(element, LSFPropertyExprObject.class).equals(partDef)) {
//                    innerExprs.add((LSFPropertyExprObject)element);
//                }
//                if(element instanceof LSFExprParamReference && PsiTreeUtil.getParentOfType(element, LSFPropertyExprObject.class).equals(partDef)) {
//                    LSFExprParamReference ref = (LSFExprParamReference) element;
//                    String refName = ref.getName();
//                    if(!renameParams.containsKey(refName)) {
//                        LSFExprParamDeclaration decl = ref.resolveDecl();
//                        if (isOuter(decl, partDef) || renameParams.containsValue(refName)) { // если за пределами inline'а, или уже кто-то переименовывается в это имя
//                            LSFClassName psiClassName = ((LSFExprParameterNameUsage) ref).getClassParamDeclare().getClassName();
//                            String className;
//                            if (psiClassName != null) 
//                                className = psiClassName.getText();
//                            else 
//                                className = "Unknown";
//                            int i = 0;
//                            String newName = null;
//                            while(true) {
//                                newName = className.substring(0, i+1).toLowerCase() + refName;
//                                if(renameParams.containsKey(newName) || renameParams.containsValue(newName) || upParams.contains(newName)) {
//                                    i++;
//                                } else
//                                    break;
//                            }
//                            
//                            String rename = refName + " -> " + newName;
//                            if(existingRenames.add(rename))
//                                System.out.println(rename);
//                            renameParams.put(refName, newName);
//                        }
//                    }
//                }                                    
//                return true;
//            }
//        });
//        
//        // remove'им group params
//        if(groupBy != null) {
//            LSFNonEmptyPropertyExpressionList jExprList = peList;
//            
//            if (removeGroupBy) {
//                ASTNode node = groupBy.getNode();
//                ASTNode nnode = node.getTreeNext();
//                removeElement(transaction, groupPropertyDefinition.getNode(), node);
//                removeWhiteSpaces(transaction, groupPropertyDefinition.getNode(), nnode);
//                removeElement(transaction, peList.getNode(), jExprList.getNode());
//            } else {
//                LSFNonEmptyPropertyExpressionList exprList = groupBy.getNonEmptyPropertyExpressionList();
//                removeAndRename(exprList, transaction, removeParams, removeClasses, renameParams, partDef);
//
//                removeAndRename(jExprList, transaction, removeParams, null, null, null); // будем исходить из того что group by в group by не используется
//            }
//            changeParams(body, transaction, renameParams, null, partDef);
//        } else {
//            if(peList != null) {
//                for (int removeParam : removeParams) {
//                    LSFExprParamDeclaration param = params.get(removeParam);
//
//                    LSFClassParamDeclare classParamDeclare = PsiTreeUtil.getParentOfType(param, LSFClassParamDeclare.class);
//
//                    removeClass(transaction, classParamDeclare);
//                }
//
//                removeAndRename(peList, transaction, removeParams, null, null, null); // будем исходить из того что group by в group by не используется
//            }
//
//            changeParams(partDef.getPropertyCalcStatement(), transaction, renameParams, null, partDef);
//        }
//        
//        for(LSFPropertyExprObject innerExpr : innerExprs)
//            changeCode(transaction, innerExpr);
//    }
//
//    private static void removeClass(MetaTransaction transaction, LSFClassParamDeclare classParamDeclare) {
//        LSFClassName className = classParamDeclare.getClassName();
//        if(className != null) {
//            ASTNode node = className.getNode();
//            ASTNode nnode = node.getTreeNext();
//
//            if (transaction != null) {
//                transaction.regChange(new ArrayList<>(), node, MetaTransaction.Type.REPLACE);
//            }
//            classParamDeclare.getNode().removeChild(node);
//            if (transaction != null) {
//                transaction.regChange(new ArrayList<>(), nnode, MetaTransaction.Type.REPLACE);
//            }
//            classParamDeclare.getNode().removeChild(nnode);
//        }
//    }
//
//    public static boolean isOuter(LSFExprParamDeclaration decl, LSFPropertyExprObject partDef) {
//        if(decl.getTextOffset() < partDef.getTextOffset())
//            return true;
//
//        if(decl instanceof LSFObjectDeclaration) // если объект то может быть и позже
//            return decl.getTextOffset() > partDef.getTextOffset() + partDef.getTextLength();
//
//        return false;
//    }

    //
//    // onEdit - отдельно обработать ???
//    static private void changeCode(MetaTransaction transaction, LSFPartitionPropertyDefinition partDef) {
//
//        // в одну строку ACTION f(a); -> REPLACE({) f(a);ADD( }), 
//        //              ACTION f(a) ... pOptions ... ; -> REPLACE({) f(a)ADD(; }) ... ;
//
//        LSFPartitionPropertyBy partitionPropertyBy = partDef.getPartitionPropertyBy();
//        Map<LSFClassParamDeclare, LSFExprParamReference> proceeded = new HashMap<>();
//        Map<LSFClassParamDeclare, LSFValueClass> classes = new HashMap<>();
//        PsiTreeUtil.processElements(partDef.getPropertyExpression(), new PsiElementProcessor() {
//            @Override
//            public boolean execute(@NotNull PsiElement element) {
//                if (element instanceof LSFPartitionPropertyDefinition) {
//                    changeCode(transaction, (LSFPartitionPropertyDefinition) element);
//                    return false;
//                }
//                return true;
//            }
//        });
//        if (partitionPropertyBy != null) {
//            for (LSFExprParamReference exprParamRef : PsiTreeUtil.findChildrenOfType(partitionPropertyBy, LSFExprParamReference.class)) { // находим param с именем
//                LSFClassParamDeclare classParamDeclare = PsiTreeUtil.getParentOfType(exprParamRef.resolveDecl(), LSFClassParamDeclare.class);
//                if (classParamDeclare.getTextOffset() < partitionPropertyBy.getTextOffset() && classParamDeclare.getTextOffset() > partDef.getTextOffset() && classParamDeclare.getClassName() != null) {
//                    LSFExprParamReference ref = proceeded.get(classParamDeclare);
//                    if (ref == null || exprParamRef.getTextOffset() < ref.getTextOffset())
//                        proceeded.put(classParamDeclare, exprParamRef);
//                    if (ref == null) { // если это не declaration параметра
//                        // нужно в classParamDeclare вырезать classname
//                        LSFValueClass lsfClassSet = classParamDeclare.resolveClass().getCommonClass();
//                        classes.put(classParamDeclare, lsfClassSet);
//
//                        LSFClassName className = classParamDeclare.getClassName();
//                        ASTNode node = className.getNode();
//                        ASTNode nnode = node.getTreeNext();
//
//                        if (transaction != null) {
//                            transaction.regChange(new ArrayList<>(), node, MetaTransaction.Type.REPLACE);
//                        }
//                        classParamDeclare.getNode().removeChild(node);
//                        if (transaction != null) {
//                            transaction.regChange(new ArrayList<>(), nnode, MetaTransaction.Type.REPLACE);
//                        }
//                        classParamDeclare.getNode().removeChild(nnode);
//                    }
//                }
//            }
//
//            // вырезаем BY с пробелом слева
//            ASTNode node = partitionPropertyBy.getNode();
//            ASTNode nnode = node.getTreePrev();
//            if (!LSFParserDefinition.isWhiteSpace(nnode.getElementType()) || LSFParserDefinition.isComment(nnode.getTreePrev().getElementType())) { // comment нельзя перемещать whitespace так как с комментарием сольется
//                nnode = ASTFactory.whitespace(" ");
//            } else {
//                if (transaction != null) {
//                    transaction.regChange(new ArrayList<>(), nnode, MetaTransaction.Type.REPLACE);
//                }
//                partDef.getNode().removeChild(nnode);
//            }
//            if (transaction != null) {
//                transaction.regChange(new ArrayList<>(), node, MetaTransaction.Type.REPLACE);
//            }
//
//            // ensureClass
//            for (Map.Entry<LSFClassParamDeclare, LSFExprParamReference> entry : proceeded.entrySet()) {
//                ((LSFExprParameterNameUsage) entry.getValue()).getClassParamDeclare().ensureClass(classes.get(entry.getKey()), null);
//            }
//
//            partDef.getNode().removeChild(node);
//
//            // добавляем после слова GROUP новый GROUP BY  
//            ASTNode newPartBy = LSFElementGenerator.createNewPartitionBy(partDef.getProject(), node.getText());
//            ASTNode groupWordNode = partDef.getNode().getFirstChildNode();
//            if (transaction != null) {
//                transaction.regChange(BaseUtils.add(BaseUtils.<ASTNode>toList(nnode), MetaTransaction.getLeafTokens(newPartBy)), groupWordNode, MetaTransaction.Type.AFTER);
//            }
//            partDef.getNode().addChild(newPartBy, groupWordNode.getTreeNext());
//            partDef.getNode().addChild(nnode, newPartBy);
//
//        }
//    }
//
    static private void changeCode(MetaTransaction transaction, LSFImportActionPropertyDefinitionBody impDef) {

        // в одну строку ACTION f(a); -> REPLACE({) f(a);ADD( }), 
        //              ACTION f(a) ... pOptions ... ; -> REPLACE({) f(a)ADD(; }) ... ;
        
//        LSFPrevFrom prevFrom = impDef.getPrevFrom();
//
//            // вырезаем BY с пробелом слева
//            ASTNode node = prevFrom.getNode();
//            ASTNode nnode = node.getTreePrev();
//            if(!LSFParserDefinition.isWhiteSpace(nnode.getElementType()) || LSFParserDefinition.isComment(nnode.getTreePrev().getElementType())) { // comment нельзя перемещать whitespace так как с комментарием сольется
//                nnode = ASTFactory.whitespace(" ");
//            } else {
//                if (transaction != null) {
//                    transaction.regChange(new ArrayList<>(), nnode, MetaTransaction.Type.REPLACE);
//                }
//                impDef.getNode().removeChild(nnode);
//            }
//            if (transaction != null) {
//                transaction.regChange(new ArrayList<>(), node, MetaTransaction.Type.REPLACE);
//            }
//            impDef.getNode().removeChild(node);
//
//            // добавляем после слова GROUP новый GROUP BY  
//            ASTNode newFrom = LSFElementGenerator.createNewImportFrom(impDef.getProject(), node.getText());
//            ASTNode toNode = impDef.getToClause().getNode().getTreePrev();
//            if (transaction != null) {
//                transaction.regChange(BaseUtils.add(BaseUtils.<ASTNode>toList(nnode), MetaTransaction.getLeafTokens(newFrom)), toNode, MetaTransaction.Type.BEFORE);
//            }
//            impDef.getNode().addChild(newFrom, toNode);
//            impDef.getNode().addChild(nnode, newFrom);
//
//

//        LSFActionPropertyDefinitionBody action = groupDef.getActionPropertyDefinitionBody();
//        if(action.getListActionPropertyDefinitionBody() != null) { // ACTION list - просто убираем REMOVE(ACTION)
//            ASTNode node = groupDef.getPrevActionClause().getNode();
//            ASTNode nnode = node.getTreeNext();
//            if (transaction != null) {
//                transaction.regChange(new ArrayList<>(), node, MetaTransaction.Type.REPLACE);
//            }
//            groupDef.getNode().removeChild(node);
//
//            if(nnode != null && LSFParserDefinition.isWhiteSpace(nnode.getElementType())) {
//                if (transaction != null) {
//                    transaction.regChange(new ArrayList<>(), nnode, MetaTransaction.Type.REPLACE);
//                }
//                groupDef.getNode().removeChild(nnode);
//            }
//
//        } else {
//            LeafElement lbr = ASTFactory.leaf(LSFTypes.LBRACE, "{");
//            LeafElement rbr = ASTFactory.leaf(LSFTypes.RBRACE, "}");
//            ASTNode node = groupDef.getPrevActionClause().getNode();
//            ASTNode nnode = node.getTreeNext();
//            if (transaction != null) {
//                transaction.regChange(BaseUtils.toList(lbr), node, MetaTransaction.Type.REPLACE);
//            }
//            groupDef.getNode().replaceChild(node, lbr);
//
//            if(!action.getText().contains("{")) { // одна строка 
//                node = action.getNode();
//                // идем вверх и ищем следующий токен
//                ASTNode rleaf = MetaTransaction.findRightLeafToken(node);
//                if(rleaf.getElementType().equals(LSFTypes.SEMI))
//                    node = rleaf; 
//                else
//                    node = findNextNode(node, true);
//
//                if(node.getElementType().equals(LSFTypes.SEMI)) { // в одну строку ACTION f(a); -> REPLACE({) f(a);ADD( }),
//                    addTokens(BaseUtils.toList(ASTFactory.whitespace(" "), rbr), transaction, node, false);
//                } else { // ACTION f(a) ... pOptions ... ; -> REPLACE({) f(a)ADD(; }) ... ;
//                    addTokens(BaseUtils.toList(ASTFactory.leaf(LSFTypes.SEMI, ";"), ASTFactory.whitespace(" "), rbr), transaction, action.getNode(), false);
//                }
//            } else { // в несколько строк и не list ACTION NEWSESSION { добавляем {\n берем в фигурные скобки, табулируем все строки и }\n - в конце (ASSERT ЧТО В ТАКОМ СЛУЧАЕ ЗАКАНЧИВАЕТСЯ НА }
//                if(nnode != null && LSFParserDefinition.isWhiteSpace(nnode.getElementType())) {
//                    if (transaction != null) {
//                        transaction.regChange(new ArrayList<>(), nnode, MetaTransaction.Type.REPLACE);
//                    }
//                    groupDef.getNode().removeChild(nnode);
//                }
//                
//                // добавляем \n\t перед
//                addTokens(BaseUtils.toList(ASTFactory.whitespace("\n\t")), transaction, action.getNode(), true);
//
//                ASTNode nextNode = findNextNode(action.getNode(), false);
//                ASTNode rleaf = MetaTransaction.findRightLeafToken(action.getNode());
//                assert rleaf.getElementType().equals(LSFTypes.RBRACE);
//                
//                // бежим
//                for(ASTNode leafToken : MetaTransaction.getLeafTokens(action.getNode())) {
//                    if(LSFParserDefinition.isWhiteSpaceOrComment(leafToken.getElementType()) && leafToken.getText().contains("\n")) {
//                        LeafElement newWhitespace = ASTFactory.whitespace(leafToken.getText().replace("\n", "\n\t"));
//                        if(transaction != null)
//                            transaction.regChange(BaseUtils.toList(newWhitespace), leafToken, MetaTransaction.Type.REPLACE);
//                        leafToken.getTreeParent().replaceChild(leafToken, newWhitespace);
//                    }   
//                }
//
//                List<ASTNode> add = BaseUtils.toList(ASTFactory.whitespace("\n"), rbr);
//                if(nextNode == null)
//                    addEndTokens(add, transaction, action.getNode());
//                else
//                    addTokens(add, transaction, nextNode, true);
//            }
//        }
    }

    private static ASTNode findNextNode(ASTNode node, boolean skipWhitespaces) {
        while(true) {
            if(skipWhitespaces)
                while(node.getTreeNext() != null && LSFParserDefinition.isWhiteSpaceOrComment(node.getTreeNext().getElementType()))
                     node = node.getTreeNext();                    

            ASTNode siblingNode = node;
            while((siblingNode = siblingNode.getTreeNext())!=null) {
                ASTNode leafToken = MetaTransaction.findLeftLeafToken(siblingNode);
                if (leafToken != null) {
                    if (skipWhitespaces)
                        while (leafToken != null && LSFParserDefinition.isWhiteSpaceOrComment(leafToken.getElementType()))
                            leafToken = leafToken.getTreeNext();
                    if (leafToken != null)
                        return leafToken;
                }
            }

            node = node.getTreeParent();
            if(node == null)
                return null;
        }
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
    
//    static String getViewType(LSFFormPropertyOptionsList optionsList) {
//        String result = null;
//        for(LSFFormOptionForce option : optionsList.getFormOptionForceList())
//            result = option.getText();
//        return result;
//    }
//    
//    static void updateViewType(LSFFormPropertyOptionsList optionsList, String resultViewType, MetaTransaction transaction) {
//        boolean found = false;
//        for(LSFFormOptionForce formOption : optionsList.getFormOptionForceList()) {
//            if(resultViewType == null || !formOption.getText().equals(resultViewType)) { // удаляем
//                ASTNode optionNode = formOption.getNode();
//                if(optionNode.getTreeNext() != null && LSFParserDefinition.isWhiteSpaceOrComment((optionNode.getTreeNext()).getElementType())) { // пробел 
//                    if (transaction != null) {
//                        transaction.regChange(new ArrayList<>(), optionNode.getTreeNext(), MetaTransaction.Type.REPLACE);
//                    }
//                    optionsList.getNode().removeChild(optionNode.getTreeNext());
//                }
//                if (transaction != null) {
//                    transaction.regChange(new ArrayList<>(), optionNode, MetaTransaction.Type.REPLACE);
//                }
//                optionsList.getNode().removeChild(optionNode);
//            } else
//                found = true;
//        }
//        
//        if(resultViewType != null && !found) {
//            if (optionsList.getNode().getText().isEmpty()) {
//                LSFFormPropertyOptionsList newOptionList = LSFElementGenerator.createFormPropertyOptionsList(optionsList.getProject(), resultViewType);
//                LeafElement whitespace = ASTFactory.whitespace(" ");
//
//                ASTNode treeNext = optionsList.getNode().getTreeNext(); // есть правило после
//                
//                if(treeNext != null) {
//                    if (transaction != null) {
//                        transaction.regChange(BaseUtils.toList(newOptionList.getNode(), whitespace), treeNext, MetaTransaction.Type.BEFORE);
//                    }
//                    optionsList.getParent().getNode().addChild(whitespace, treeNext);
//                    optionsList.getParent().getNode().addChild(newOptionList.getNode(), whitespace);
//                } else {
//                    ASTNode treePrev = optionsList.getNode().getTreePrev(); // есть правило до
//                    if( LSFParserDefinition.isWhiteSpaceOrComment(treePrev.getElementType())) {
//                        if (transaction != null) {
//                            transaction.regChange(BaseUtils.toList(whitespace, newOptionList.getNode()), treePrev, MetaTransaction.Type.BEFORE);
//                        }
//                        optionsList.getParent().getNode().addChild(newOptionList.getNode(), treePrev);
//                        optionsList.getParent().getNode().addChild(whitespace, newOptionList.getNode());
//                    } else {
//                        if (transaction != null) {
//                            transaction.regChange(BaseUtils.toList(whitespace, newOptionList.getNode()), MetaTransaction.findLeafToken(treePrev), MetaTransaction.Type.AFTER);
//                        }
//                        optionsList.getParent().getNode().addChild(whitespace);
//                        optionsList.getParent().getNode().addChild(newOptionList.getNode());
//                    }
//                }
//            } else {
//                LSFFormOptionForce newOption = LSFElementGenerator.createFormOptionForce(optionsList.getProject(), resultViewType);
//                LeafElement whitespace = ASTFactory.whitespace(" ");
//                if (transaction != null) {
//                    transaction.regChange(BaseUtils.toList(whitespace, newOption.getNode()), MetaTransaction.findLeafToken(optionsList.getNode()), MetaTransaction.Type.AFTER);
//                }
//                optionsList.getNode().addChild(whitespace);
//                optionsList.getNode().addChild(newOption.getNode());
//            }
//        }
//    }
//    
//    static private String getOViewType(LSFObjectUsageList usageList, LSFFormPropertyOptionsList declPropertyOptionsList, LSFFormPropertyOptionsList blockPropertyOptionsList) {
//        LSFNonEmptyObjectUsageList nonEmptyObjectUsageList = usageList.getNonEmptyObjectUsageList();
//        if(nonEmptyObjectUsageList == null)
//            return "PANEL";
//
//        LSFGroupObjectDeclaration groupObjectDecl = getExplicitToDraw(declPropertyOptionsList, blockPropertyOptionsList);
//        if(groupObjectDecl == null)
//            groupObjectDecl = getApplyGroupObject(nonEmptyObjectUsageList);
//
//        LSFFormGroupObjectDeclaration parent = PsiTreeUtil.getParentOfType(groupObjectDecl, LSFFormGroupObjectDeclaration.class);
//        if(parent == null) // дерево
//            return "GRID";
//
//        List<LSFFormGroupObjectViewType> viewTypeList = parent.getFormGroupObjectOptions().getFormGroupObjectViewTypeList();
//        if(viewTypeList.size() > 0)
//            return viewTypeList.iterator().next().getText();
//        return "GRID";
//    }
//
//    private static LSFGroupObjectDeclaration getExplicitToDraw(LSFFormPropertyOptionsList declPropertyOptionsList, LSFFormPropertyOptionsList blockPropertyOptionsList) {
//        List<LSFFormOptionToDraw> formOptionToDrawList = declPropertyOptionsList.getFormOptionToDrawList();
//        if(formOptionToDrawList.isEmpty())
//            formOptionToDrawList = blockPropertyOptionsList.getFormOptionToDrawList();
//        if(!formOptionToDrawList.isEmpty())
//            return formOptionToDrawList.get(0).getGroupObjectUsage().resolveDecl();
//        return null;
//    }
//
//    private static LSFGroupObjectDeclaration getApplyGroupObject(LSFNonEmptyObjectUsageList nonEmptyObjectUsageList) {
//        LSFFile maxFile = null;
//        int maxOffset = -1;
//        LSFObjectDeclaration maxDecl = null;
//        for(LSFObjectUsage objectUsage : nonEmptyObjectUsageList.getObjectUsageList()) {
//            LSFObjectDeclaration lsfObjectDeclaration = objectUsage.resolveDecl();
//            if(lsfObjectDeclaration == null)
//                continue;
//            LSFFile objectFile = lsfObjectDeclaration.getLSFFile();
//            int objectOffset = lsfObjectDeclaration.getTextOffset();
//            if(maxFile == null) {
//                maxFile = objectFile;
//                maxOffset = objectOffset;
//                maxDecl = lsfObjectDeclaration;
//            } else {
//                if(maxFile.getName().equals(objectFile.getName())) {
//                    if(maxOffset < objectOffset) {
//                        maxOffset = objectOffset;
//                        maxDecl = lsfObjectDeclaration;
//                    }                        
//                } else {
//                    if(objectFile.getRequireScope().contains(maxFile.getVirtualFile())) {
//                        maxFile = objectFile;
//                        maxOffset = objectOffset;
//                        maxDecl = lsfObjectDeclaration;
//                    }
//                }                    
//            }
//        }
//        return PsiTreeUtil.getParentOfType(maxDecl, LSFFormCommonGroupObject.class);
//    }
//
//    static private void changeCode(MetaTransaction transaction, LSFFormMappedPropertiesList list, Map<PsiElement, Set<String>> clauseAdded) {
//        List<LSFFormPropertyDrawMappedDecl> properties = list.getFormPropertyDrawMappedDeclList();
//
//        LSFFormPropertyOptionsList optionList = ((LSFFormPropertiesList) list.getParent()).getFormPropertyOptionsList();
//        String blockType = getViewType(optionList);
//
//        String[] options = new String[]{"PANEL", "GRID", "TOOLBAR"};
//
//        Map<LSFFormPropertyDrawMappedDecl, String> toBe = new HashMap<>();
//        Map<LSFFormPropertyDrawMappedDecl, String> defViewTypes = new HashMap<>();
//        for (LSFFormPropertyDrawMappedDecl decl : properties) {
//
//            String defType = null;
//            String prevType = null;
//
//            String oViewType = getOViewType(decl.getObjectUsageList(), decl.getFormPropertyOptionsList(), optionList);
//            if (oViewType.contains("PANEL")) {
//                defType = "PANEL";
//            } else {
//                LSFFormPropertyName name = decl.getFormPropertyName();
//                LSFPropertyUsage propertyUsage = name.getPropertyUsage();
//                if (propertyUsage != null) {
//                    LSFPropertyStatement propDecl = (LSFPropertyStatement) propertyUsage.resolveDecl();
//                    if (propDecl != null) {
//                        LSFPropertyOptions propertyOptions = propDecl.getPropertyOptions();
//                        if (propertyOptions != null) {
//                            for (LSFViewTypeSetting viewType : propertyOptions.getViewTypeSettingList()) {
//                                defType = viewType.getText();
//                            }
//                        }
//
//                        if (defType == null) {
//                            boolean direct = propertyUsage.isDirect();
//                            if (propDecl.getActionStatement() != null) {
//                                defType = "PANEL";
//                                if (direct)
//                                    prevType = "GRID";
//                            } else {
//                                defType = "GRID";
//                                if (!direct)
//                                    prevType = "PANEL";
//                            }
//                        }
//                    } else
//                        defType = "GRID";
//                } else {
//                    LSFPredefinedFormPropertyName predef = name.getPredefinedFormPropertyName();
//                    if (predef.getText().equals("OBJVALUE") || predef.getText().equals("SELECTION"))
//                        defType = "GRID";
//                    else {
//                        defType = "TOOLBAR";
//                        if (predef.getText().equals("DELETE"))
//                            prevType = "GRID";
//                    }
//                }
//            }
//            defViewTypes.put(decl, defType);
//
//            String declToBe = null;
//            String pdrawType = getViewType(decl.getFormPropertyOptionsList());
//            if (pdrawType != null)
//                declToBe = pdrawType;
//            else {
//                if (blockType != null) {
//                    if (blockType.equals("PANEL") && defType.equals("TOOLBAR"))
//                        declToBe = defType;
//                    else
//                        declToBe = blockType;
//                } else {
//                    if (prevType != null)
//                        declToBe = prevType;
//                    else
//                        declToBe = defType;
//                }
//            }
//            toBe.put(decl, declToBe);
//        }
//
//        // два варианта прописываем blockType, и не прописываем blockType
//        // собственно переберем варианты
//        int minNotNullWords = Integer.MAX_VALUE;
//        String minResultBlock = null;
//        Map<LSFFormPropertyDrawMappedDecl, String> minResultProps = null;
//
//        // с блоком
//        for (String blockOption : options) {
//            int notNullWords = 1;
//            Map<LSFFormPropertyDrawMappedDecl, String> resultProps = new HashMap<>();
//            for (Map.Entry<LSFFormPropertyDrawMappedDecl, String> entry : toBe.entrySet()) {
//                resultProps.put(entry.getKey(), null);
//                if (!entry.getValue().equals(blockOption)) {
//                    notNullWords++;
//                    resultProps.put(entry.getKey(), entry.getValue());
//                }
//            }
//            if (minNotNullWords > notNullWords) {
//                minNotNullWords = notNullWords;
//                minResultBlock = blockOption;
//                minResultProps = resultProps;
//            }
//        }
//
//        // без блока
//        int notNullWords = 0;
//        Map<LSFFormPropertyDrawMappedDecl, String> resultProps = new HashMap<>();
//        for (Map.Entry<LSFFormPropertyDrawMappedDecl, String> entry : toBe.entrySet()) {
//            resultProps.put(entry.getKey(), null);
//            if (!entry.getValue().equals(defViewTypes.get(entry.getKey()))) {
//                resultProps.put(entry.getKey(), entry.getValue());
//                notNullWords++;
//            }
//        }
//        if (minNotNullWords > notNullWords || (minNotNullWords == notNullWords && blockType == null)) { // если не было блока то и не добавляем
//            minNotNullWords = notNullWords;
//            minResultBlock = null;
//            minResultProps = resultProps;
//        }
//
//        updateViewType(optionList, minResultBlock, transaction);
//        for (Map.Entry<LSFFormPropertyDrawMappedDecl, String> minResultProp : minResultProps.entrySet())
//            updateViewType(minResultProp.getKey().getFormPropertyOptionsList(), minResultProp.getValue(), transaction);
//    }
//
//    static private void changeCode(MetaTransaction transaction, LSFFormMappedNamePropertiesList list, Map<PsiElement, Set<String>> clauseAdded) {
//        List<LSFFormPropertyDrawNameDecl> properties = list.getFormPropertiesNamesDeclList().getFormPropertyDrawNameDeclList();
//        
//        String blockType = getViewType(list.getFormPropertyOptionsList());
//        
//        String[] options = new String[]{"PANEL", "GRID", "TOOLBAR"};
//        
//        Map<LSFFormPropertyDrawNameDecl, String> toBe = new HashMap<>();
//        Map<LSFFormPropertyDrawNameDecl, String> defViewTypes = new HashMap<>();
//        for(LSFFormPropertyDrawNameDecl decl : properties) {
//
//            String defType = null;
//            String prevType = null;
//            
//            String oViewType = getOViewType(decl.getObjectUsageList(), decl.getFormPropertyOptionsList(), list.getFormPropertyOptionsList());
//            if(oViewType.contains("PANEL")) {
//                defType = "PANEL";
//            } else {
//                LSFFormPropertyName name = decl.getFormPropertyName();
//                LSFPropertyUsage propertyUsage = name.getPropertyUsage();
//                if (propertyUsage != null) {
//                    LSFPropertyStatement propDecl = (LSFPropertyStatement) propertyUsage.resolveDecl();
//                    if (propDecl != null) {
//                        LSFPropertyOptions propertyOptions = propDecl.getPropertyOptions();
//                        if (propertyOptions != null) {
//                            for (LSFViewTypeSetting viewType : propertyOptions.getViewTypeSettingList()) {
//                                defType = viewType.getText();
//                            }
//                        }
//
//                        if (defType == null) {
//                            boolean direct = propertyUsage.isDirect();
//                            if (propDecl.getActionStatement() != null) {
//                                defType = "PANEL";
//                                if (direct)
//                                    prevType = "GRID";
//                            } else {
//                                defType = "GRID";
//                                if (!direct)
//                                    prevType = "PANEL";
//                            }
//                        }
//                    } else
//                        defType = "GRID";
//                } else {
//                    LSFPredefinedFormPropertyName predef = name.getPredefinedFormPropertyName();
//                    if (predef.getText().equals("OBJVALUE") || predef.getText().equals("SELECTION"))
//                        defType = "GRID";
//                    else {
//                        defType = "TOOLBAR";
//                        if (predef.getText().equals("DELETE"))
//                            prevType = "GRID";
//                    }
//                }
//            }
//            defViewTypes.put(decl, defType);
//
//            String declToBe = null;
//            String pdrawType = getViewType(decl.getFormPropertyOptionsList());
//            if(pdrawType != null)
//                declToBe = pdrawType;
//            else {
//                if (blockType != null) {
//                    if(blockType.equals("PANEL") && defType.equals("TOOLBAR"))
//                        declToBe = defType;
//                    else
//                        declToBe = blockType;
//                } else {
//                    if(prevType != null)
//                        declToBe = prevType;
//                    else
//                        declToBe = defType;
//                }
//            }
//            toBe.put(decl, declToBe);
//        }
//
//        // два варианта прописываем blockType, и не прописываем blockType
//        // собственно переберем варианты
//        int minNotNullWords = Integer.MAX_VALUE;
//        String minResultBlock = null;
//        Map<LSFFormPropertyDrawNameDecl, String> minResultProps = null;
//
//        // с блоком
//        for(String blockOption : options) {
//            int notNullWords = 1;
//            Map<LSFFormPropertyDrawNameDecl, String> resultProps = new HashMap<>();
//            for(Map.Entry<LSFFormPropertyDrawNameDecl, String> entry : toBe.entrySet()) {
//                resultProps.put(entry.getKey(), null);
//                if (!entry.getValue().equals(blockOption)) {
//                    notNullWords++;
//                    resultProps.put(entry.getKey(), entry.getValue());
//                }
//            }
//            if(minNotNullWords > notNullWords) {
//                minNotNullWords = notNullWords;
//                minResultBlock = blockOption;
//                minResultProps = resultProps;
//            }
//        }
//
//        // без блока
//        int notNullWords = 0;
//        Map<LSFFormPropertyDrawNameDecl, String> resultProps = new HashMap<>();
//        for(Map.Entry<LSFFormPropertyDrawNameDecl, String> entry : toBe.entrySet()) {
//            resultProps.put(entry.getKey(), null);
//            if(!entry.getValue().equals(defViewTypes.get(entry.getKey()))) {
//                resultProps.put(entry.getKey(), entry.getValue());
//                notNullWords++;
//            }
//        }
//        if(minNotNullWords > notNullWords || (minNotNullWords == notNullWords && blockType == null)) { // если не было блока то и не добавляем
//            minNotNullWords = notNullWords;
//            minResultBlock = null;
//            minResultProps = resultProps;
//        }
//        
//        updateViewType(list.getFormPropertyOptionsList(), minResultBlock, transaction);
//        for(Map.Entry<LSFFormPropertyDrawNameDecl, String> minResultProp : minResultProps.entrySet())
//            updateViewType(minResultProp.getKey().getFormPropertyOptionsList(), minResultProp.getValue(), transaction);
//
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
//    }

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
