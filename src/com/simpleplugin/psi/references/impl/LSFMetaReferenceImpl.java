package com.simpleplugin.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.FilteredQuery;
import com.intellij.util.Query;
import com.simpleplugin.LSFParserDefinition;
import com.simpleplugin.psi.*;
import com.simpleplugin.psi.declarations.LSFMetaDeclaration;
import com.simpleplugin.psi.references.LSFMetaReference;
import com.simpleplugin.psi.stubs.types.FullNameStubElementType;
import com.simpleplugin.psi.stubs.types.LSFStubElementTypes;
import com.simpleplugin.psi.stubs.types.MetaStubElementType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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
    public List<String> getUsageParams() {
        List<String> result = new ArrayList<String>();
        for(LSFMetaCodeId id : getMetaCodeIdList().getMetaCodeIdList())
            result.add(id.getText());
        return result;
    }

    @Override
    public boolean isResolveToVirt(LSFMetaDeclaration virtDecl) {
        return LSFGlobalResolver.findElements(getNameRef(), getFullNameRef(), (LSFFile) getContainingFile(), getType(), virtDecl, getCondition()).findAll().contains(virtDecl);
    }

    @Override
    public String getPreceedingTab() {
        ASTNode treePrev = getNode().getTreePrev();
/*        if(treePrev==null) {
            PsiElement parent = getParent();
            assert parent instanceof LSFStatements;
            treePrev = parent.getNode().getTreePrev(); // предполагается что тут statements будут
        }*/
        if(treePrev!=null && LSFParserDefinition.isWhiteSpace(treePrev.getElementType())) // сохраним табуляцию
            return treePrev.getText();
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
}
