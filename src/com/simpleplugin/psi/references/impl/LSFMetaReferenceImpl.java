package com.simpleplugin.psi.references.impl;

import com.intellij.icons.AllIcons;
import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.util.Condition;
import com.intellij.util.CollectionQuery;
import com.simpleplugin.LSFParserDefinition;
import com.simpleplugin.LSFReferenceAnnotator;
import com.simpleplugin.meta.MetaTransaction;
import com.simpleplugin.psi.*;
import com.simpleplugin.psi.declarations.LSFMetaDeclaration;
import com.simpleplugin.psi.references.LSFMetaReference;
import com.simpleplugin.psi.stubs.types.LSFStubElementTypes;
import com.simpleplugin.psi.stubs.types.MetaStubElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract class LSFMetaReferenceImpl extends LSFFullNameReferenceImpl<LSFMetaDeclaration, LSFMetaDeclaration> implements LSFMetaReference {

    public LSFMetaReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected MetaStubElementType getType() {
        return LSFStubElementTypes.META;
    }

    private long version;
    public long getVersion() {
        return version;
    }
    public void setVersion(long version) {
        this.version = version;
    }
    
    private LSFFile anotherFile;
    public void setAnotherFile(LSFFile anotherFile) {
        this.anotherFile = anotherFile; 
    }

    @Override
    public LSFFile getLSFFile() {
        if(anotherFile!=null)
            return anotherFile;
        return super.getLSFFile();            
    }

    protected abstract LSFMetacodeUsage getMetacodeUsage();

    public abstract LSFMetaCodeBody getMetaCodeBody();

    protected abstract LSFMetaCodeIdList getMetaCodeIdList();

    @Override
    public boolean isCorrect() {
        return super.isCorrect() && getMetacodeUsage()!=null && getMetaCodeIdList()!=null && getNode().findChildByType(LSFTypes.SEMI)!=null;
    }

    @Override
    protected LSFCompoundID getCompoundID() {
        return getMetacodeUsage().getCompoundID();
    }

    @Override
    protected Condition<LSFMetaDeclaration> getCondition() {
        final int paramCount = getMetaCodeIdList().getMetaCodeIdList().size();
        return new Condition<LSFMetaDeclaration>() {
            public boolean value(LSFMetaDeclaration decl) {
                return decl.getParamCount()==paramCount;
            }
        };
    }

    @Override
    public Annotation resolveNotFoundErrorAnnotation(AnnotationHolder holder) {
        CollectionQuery<LSFMetaDeclaration> declarations = new CollectionQuery<LSFMetaDeclaration >(LSFGlobalResolver.findElements(getNameRef(), getFullNameRef(), getLSFFile(), getTypes(), Condition.TRUE, Finalizer.EMPTY));

        if (declarations.findAll().isEmpty()) {
            return super.resolveNotFoundErrorAnnotation(holder);
        }

        String errorText = "Unable to resolve metacode: ";
        for (Iterator<LSFMetaDeclaration> iterator = declarations.iterator(); iterator.hasNext();) {
            errorText += iterator.next().getParamCount();
            if (iterator.hasNext()) {
                errorText += ", ";
            }
        }

        int paramCount = getMetaCodeIdList().getMetaCodeIdList().size();
        Annotation error = holder.createErrorAnnotation(getMetaCodeIdList(), errorText + " parameters expected; " + paramCount + " found");
        error.setEnforcedTextAttributes(LSFReferenceAnnotator.WAVE_UNDERSCORED_ERROR);

        return error;
    }

    @Override
    public List<MetaTransaction.InToken> getUsageParams() {
        List<MetaTransaction.InToken> result = new ArrayList<MetaTransaction.InToken>();
        for(LSFMetaCodeId id : getMetaCodeIdList().getMetaCodeIdList())
            result.add(MetaTransaction.parseToken(id));
        return result;
    }

    @Override
    public boolean isResolveToVirt(LSFMetaDeclaration virtDecl) {
        return LSFGlobalResolver.findElements(getNameRef(), getFullNameRef(), (LSFFile) getContainingFile(), Collections.singleton(getType()), virtDecl, getCondition()).contains(virtDecl);
    }

    @Override
    public String getPreceedingTab() {
        ASTNode treePrev = getNode().getTreePrev();
/*        if(treePrev==null) {
            PsiElement parent = getParent();
            assert parent instanceof LSFStatements;
            treePrev = parent.getNode().getTreePrev(); // предполагается что тут statements будут
        }*/
        if(treePrev!=null && LSFParserDefinition.isWhiteSpace(treePrev.getElementType())) { // сохраним табуляцию
            String whitespace = treePrev.getText();
            return whitespace.substring(whitespace.lastIndexOf('\n') +1);
        }
        return "";
    }

    @Override
    public void setInlinedBody(LSFMetaCodeBody parsed) {
        LSFMetaCodeBody body = getMetaCodeBody();
        if (parsed == null || !isCorrect()) {
            if (body != null && body.isWritable())
                body.delete();
        } else {
            if (body != null) {
                if(!body.getText().equals(parsed.getText()))
                    body.replace(parsed);
            } else {
                getNode().addChild(parsed.getNode(), getNode().findChildByType(LSFTypes.SEMI));
            }
        }
    }

    @Override
    public void dropInlinedBody() {
        LSFMetaCodeBody body = getMetaCodeBody();
        if(body != null)
            body.delete();
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return AllIcons.Nodes.Method;
    }
}
