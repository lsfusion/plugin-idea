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

public class FormRefactoring2 extends com.intellij.openapi.actionSystem.AnAction {
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
        if (element instanceof LSFActionPropertyDefinitionBody) {
            LSFActionPropertyDefinitionBody statement = (LSFActionPropertyDefinitionBody) element;
            changeCode(transaction, statement);
        }

        for (PsiElement child : element.getChildren())
            recChangeCode(transaction, child, clauseAdded);
    }

    private static void addTokens(List<ASTNode> nodes, MetaTransaction transaction, ASTNode anchorNode, boolean before) {
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

    // onEdit - отдельно обработать ???
    static private void changeCode(MetaTransaction transaction, LSFActionPropertyDefinitionBody action) {
        
        // в одну строку ACTION f(a); -> REPLACE({) f(a);ADD( }), 
        //              ACTION f(a) ... pOptions ... ; -> REPLACE({) f(a)ADD(; }) ... ;

//        List<ASTNode> leafTokens = MetaTransaction.getLeafTokens(action.getNode());
//        int i = leafTokens.size() - 1;
//        while(LSFParserDefinition.isWhiteSpaceOrComment(leafTokens.get(i).getElementType())) i--;
//        ASTNode rightToken = leafTokens.get(i);

        ASTNode node = action.getNode();
        ASTNode rightToken = MetaTransaction.findRightLeafToken(node);
        if(rightToken.getElementType().equals(LSFTypes.SEMI) || rightToken.getElementType().equals(LSFTypes.RBRACE))
            return;
        else {
            node = findNextNode(node, true);
            if(node != null && node.getElementType().equals(LSFTypes.SEMI))
                return;            
        }

        ASTNode leaf = ASTFactory.leaf(LSFTypes.SEMI, ";");
        if(transaction != null)
            transaction.regChange(BaseUtils.toList(leaf), rightToken, MetaTransaction.Type.AFTER);
        rightToken.getTreeParent().addChild(leaf);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        changeCode(e.getProject());
    }
}
