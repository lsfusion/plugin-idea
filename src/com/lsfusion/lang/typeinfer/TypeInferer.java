package com.lsfusion.lang.typeinfer;

import com.intellij.extapi.psi.ASTDelegatePsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.source.codeStyle.CodeEditUtil;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.tree.IElementType;
import com.lsfusion.lang.LSFElementGenerator;
import com.lsfusion.lang.LSFFileType;
import com.lsfusion.lang.LSFParserDefinition;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.meta.MetaTransaction;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.context.ContextModifier;
import com.lsfusion.lang.psi.context.ExtendParamContext;
import com.lsfusion.lang.psi.context.ModifyParamContext;
import com.lsfusion.lang.psi.declarations.LSFExprParamDeclaration;
import com.lsfusion.lang.psi.declarations.LSFMetaDeclaration;
import com.lsfusion.lang.psi.declarations.LSFParamDeclaration;
import com.lsfusion.lang.psi.impl.LSFActionStatementImpl;
import com.lsfusion.util.BaseUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TypeInferer {

    private static void recTypeInfer(PsiElement element, Set<LSFExprParamDeclaration> currentParams, MetaTransaction transaction, boolean first) {
        if (!first && element instanceof LSFMetaCodeStatement)
            return;

        if (element instanceof ModifyParamContext) {
            if (!(element instanceof ExtendParamContext))
                currentParams = new HashSet<LSFExprParamDeclaration>();

            ModifyParamContext modifyContext = (ModifyParamContext) element;
            ContextModifier contextModifier = modifyContext.getContextModifier();
            Set<LSFExprParamDeclaration> params = new HashSet<LSFExprParamDeclaration>(contextModifier.resolveParams(Integer.MAX_VALUE, currentParams));
            InferResult inferred = modifyContext.getContextInferrer().inferClasses(params).finish();
            for (LSFExprParamDeclaration param : params) {
                LSFClassSet inferredClass = inferred.get(param);
                if (inferredClass != null && param instanceof LSFParamDeclaration) {
                    ((LSFParamDeclaration) param).ensureClass(inferredClass.getCommonClass(), transaction);
                }
            }
            currentParams = BaseUtils.merge(currentParams, params);
        }

        for (PsiElement child : element.getChildren())
            recTypeInfer(child, currentParams, transaction, false);
    }

    public static void typeInfer(PsiElement modifyContext, MetaTransaction metaTrans) {
        assert !(modifyContext instanceof ExtendParamContext);
        recTypeInfer(modifyContext, new HashSet<LSFExprParamDeclaration>(), metaTrans, true);
    }

    public static void metaTypeInfer(LSFMetaDeclaration metaDecl, MetaTransaction transaction) {
        List<LSFMetaCodeStatement> usages = LSFResolver.findMetaUsages(metaDecl);
        for (LSFMetaCodeStatement usage : usages)
            typeInfer(usage, transaction);
    }

    public static void typeInfer(LSFFile file, MetaTransaction transaction) {
        for(PsiElement child : file.getStatements())
            if(child instanceof LSFMetaDeclaration)
                metaTypeInfer((LSFMetaDeclaration)child, transaction);
            else
                typeInfer(child, transaction);
    }

    private static void recRemove(PsiElement element, MetaTransaction transaction) {
        if (element instanceof LSFActionListParams) {
            ASTDelegatePsiElement parentStatement = (ASTDelegatePsiElement) element.getParent();

            // remove'м старый
            LSFActionListParams actionListParams = (LSFActionListParams) element;

            ASTNode node = actionListParams.getNode();
            String textDeclare = node.getText();

            // если спереди и сзади пробел вырезаем см regchange
            if(LSFParserDefinition.isWhiteSpaceOrComment(node.getTreePrev().getElementType()) && LSFParserDefinition.isWhiteSpaceOrComment(node.getTreeNext().getElementType())) {
                ASTNode prevNode = node.getTreePrev();

                if (transaction != null)
                    transaction.regChange(new ArrayList<ASTNode>(), prevNode, MetaTransaction.Type.REPLACE);

                CodeEditUtil.removeChild(prevNode.getTreeParent(), prevNode);
            }

            if (transaction != null)
                transaction.regChange(new ArrayList<ASTNode>(), node, MetaTransaction.Type.REPLACE);

            parentStatement.deleteChildInternal(node);

            if(parentStatement instanceof LSFActionStatement) {
                LSFPropertyDeclaration pdl = ((LSFPropertyStatement) parentStatement.getParent()).getPropertyDeclaration();
                if (pdl.getPropertyDeclParams() == null) {
                    LSFPropertyDeclParams newPDecl = LSFElementGenerator.createPropertyDeclParams(element.getProject(), textDeclare);

                    LSFSimpleNameWithCaption snameWithCaption = pdl.getSimpleNameWithCaption();
                    ASTNode lastNode;
                    if (snameWithCaption.getStringLiteral() != null)
                        lastNode = snameWithCaption.getStringLiteral().getNode();
                    else
                        lastNode = snameWithCaption.getSimpleName().getNode();

                    if (transaction != null) // ? разрезать или нет на leafToken
                        transaction.regChange(MetaTransaction.getLeafTokens(newPDecl.getNode()), lastNode, MetaTransaction.Type.AFTER);

                    pdl.addAfter(newPDecl, snameWithCaption);
                }
            }
        }

        for (PsiElement child : element.getChildren())
            recRemove(child, transaction);
    }

    public static void remove(Project project) {

        MetaTransaction transaction = new MetaTransaction();

        GlobalSearchScope scope = ProjectScope.getProjectScope(project);

        final List<LSFFile> files = new ArrayList<LSFFile>();

        for (VirtualFile lsfFile : FileTypeIndex.getFiles(LSFFileType.INSTANCE, scope)) {
            PsiFile psiFile = PsiManager.getInstance(project).findFile(lsfFile);
            if (psiFile instanceof LSFFile) {
                recRemove(psiFile, transaction);
            }
        }

        transaction.apply();
    }

    private static void recFindNotInferred(PsiElement element, Set<LSFExprParamDeclaration> currentParams, LSFMetaCodeStatement statement) {
        if (element instanceof ModifyParamContext) {
            if (!(element instanceof ExtendParamContext))
                currentParams = new HashSet<LSFExprParamDeclaration>();

            boolean unfr = false;
            ModifyParamContext modifyContext = (ModifyParamContext) element;
            if (element instanceof LSFPropertyStatement) {
                LSFPropertyStatement ps = (LSFPropertyStatement) element;
                if (ps.isUnfriendly())
                    unfr = true;
            }

            ContextModifier contextModifier = modifyContext.getContextModifier();
            Set<LSFExprParamDeclaration> params = new HashSet<LSFExprParamDeclaration>(contextModifier.resolveParams(Integer.MAX_VALUE, currentParams));
            if (!unfr) {
                for (LSFExprParamDeclaration param : params) {
                    LSFClassSet resClass = param.resolveClass();
                    if (resClass == null) {
                        //                    Notifications.Bus.notify(new Notificati(on("unResolved", "Unresolved", element.getContainingFile().getName() + " " + element.getTextRange(), NotificationType.INFORMATION));

                        System.out.println(element.getContainingFile().getName() + " " + (statement == null ? "NO" : statement.getMetaCodeStatementHeader().getMetacodeUsage().getText()) + " " + element.getText());
                        break;
                    }
                }
            }
            currentParams = BaseUtils.merge(currentParams, params);
        }

        for (PsiElement child : element.getChildren())
            recFindNotInferred(child, currentParams, element instanceof LSFMetaCodeStatement ? (LSFMetaCodeStatement) element : statement);
    }

    public static void findNotInferred(LSFFile file) {
        recFindNotInferred(file, new HashSet<LSFExprParamDeclaration>(), null);
    }

}
