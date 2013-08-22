package com.simpleplugin.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.CollectionQuery;
import com.intellij.util.EmptyQuery;
import com.intellij.util.Processor;
import com.intellij.util.Query;
import com.simpleplugin.psi.*;
import com.simpleplugin.psi.declarations.LSFExprParamDeclaration;
import com.simpleplugin.psi.declarations.LSFObjectDeclaration;
import com.simpleplugin.psi.extend.LSFFormExtend;
import com.simpleplugin.psi.references.LSFAbstractParamReference;
import com.simpleplugin.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class LSFAbstractParamReferenceImpl<T extends LSFExprParamDeclaration> extends LSFReferenceImpl<T> implements LSFAbstractParamReference<T> {

    public LSFAbstractParamReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean isSoft() {
        return false;
    }

    private static boolean processModifyContextParams(PsiElement modifier, int offset, Set<String> foundParams, Processor<LSFExprParamDeclaration> processor) {

        if(modifier.getTextOffset() > offset) // если идет после искомого элемента, не интересует
            return true;
            
        if (modifier instanceof LSFExprParamDeclaration) {
            LSFExprParamDeclaration param = (LSFExprParamDeclaration)modifier;
            if(foundParams.add(param.getDeclName()) && !processor.process((LSFExprParamDeclaration) modifier))
                return false;            
        }  
        if (!(modifier instanceof LSFPropertyObject)) { // hardcode конечно, но иначе придется все вручную делать
            for (PsiElement child : modifier.getChildren()) {
                if (!processModifyContextParams(child, offset, foundParams, processor)) 
                    return false;
            }
        }
        return true;
    }

    private static boolean processParams(PsiElement current, int offset, Set<String> foundParams, final Processor<LSFExprParamDeclaration> processor) {
        boolean goUp = true;
        if(current instanceof ModifyParamContext) {
            if(current instanceof ExtendParamContext) {
                if(!processParams(current.getParent(), offset, foundParams, processor))
                    return false;
            } else // не extend - останавливаемся
                goUp = false;
            ModifyParamContext extendElement = (ModifyParamContext) current;
            List<PsiElement> modifiers = extendElement.getContextModifier();
            for(PsiElement modifier : modifiers) {
                if(!processModifyContextParams(modifier, offset, foundParams, processor))
                    return false;
            }
        } else {
            Query<LSFFormExtend> extendForms = null;
            if (current instanceof LSFDesignStatement) {
                LSFDesignStatement design = (LSFDesignStatement) current;
                LSFDesignDeclaration designDecl = design.getDesignDeclaration();
                LSFFormUsage formUsage;
                if(designDecl!=null)
                    formUsage = designDecl.getFormUsage();
                else
                    formUsage = design.getExtendDesignDeclaration().getFormUsage();
                extendForms = LSFGlobalResolver.findExtendElements(formUsage.resolveDecl(), LSFStubElementTypes.EXTENDFORM, formUsage.getLSFFile());
            } else if (current instanceof LSFFormStatement) {
                LSFFormExtend currentForm = ((LSFFormStatement)current);
                extendForms = LSFGlobalResolver.findExtendElements(currentForm);
            }
            if(extendForms!=null) {
                if(!extendForms.forEach(new Processor<LSFFormExtend>() {
                    public boolean process(LSFFormExtend formExtend) {
                        for(LSFObjectDeclaration objDecl : formExtend.getObjectDecls())
                            if(!processor.process(objDecl)) //offset надо проверить (если форма текущая)
                                return false;
                        return true;
                    }
                }))
                    return false;
                goUp = false;
            }
        }
            
        if(goUp)
            return processParams(current.getParent(), offset, foundParams, processor);
        
        return true;
    }

    private boolean processParams(Processor<LSFExprParamDeclaration> processor) {        
        return processParams(this, getTextOffset(), new HashSet<String>(), processor);
    }

    @Override
    public Query<T> resolveNoCache() {
        if(getSimpleName()==null)
            return new EmptyQuery<T>(); 
        
        final String nameRef = getNameRef();
        final List<T> objects = new ArrayList<T>();
        processParams(new Processor<LSFExprParamDeclaration>() {
            public boolean process(LSFExprParamDeclaration decl) {
                if(decl.getDeclName().equals(nameRef))
                    objects.add((T) decl);
                return true;
            }
        });
        return new CollectionQuery<T>(objects);
    }

    @Override
    protected void fillListVariants(final Collection<String> variants) {
        processParams(new Processor<LSFExprParamDeclaration>() {
            public boolean process(LSFExprParamDeclaration decl) {
                variants.add(decl.getDeclName());
                return true;
            }
        });
    }
}
