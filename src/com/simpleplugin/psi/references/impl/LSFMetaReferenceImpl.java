package com.simpleplugin.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.util.Condition;
import com.simpleplugin.LSFDeclarationResolveResult;
import com.simpleplugin.LSFIcons;
import com.simpleplugin.LSFParserDefinition;
import com.simpleplugin.LSFReferenceAnnotator;
import com.simpleplugin.meta.MetaTransaction;
import com.simpleplugin.psi.*;
import com.simpleplugin.psi.declarations.LSFDeclaration;
import com.simpleplugin.psi.declarations.LSFMetaDeclaration;
import com.simpleplugin.psi.references.LSFMetaReference;
import com.simpleplugin.psi.stubs.types.LSFStubElementTypes;
import com.simpleplugin.psi.stubs.types.MetaStubElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;

public abstract class LSFMetaReferenceImpl extends LSFFullNameReferenceImpl<LSFMetaDeclaration, LSFMetaDeclaration> implements LSFMetaReference {

    public LSFMetaReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected MetaStubElementType getStubElementType() {
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
        if (anotherFile != null)
            return anotherFile;
        return super.getLSFFile();
    }

    protected abstract LSFMetacodeUsage getMetacodeUsage();

    public abstract LSFMetaCodeBody getMetaCodeBody();

    protected abstract LSFMetaCodeIdList getMetaCodeIdList();

    @Override
    public boolean isCorrect() {
        return super.isCorrect() && getMetacodeUsage() != null && getMetaCodeIdList() != null && getNode().findChildByType(LSFTypes.SEMI) != null;
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
                return decl.getParamCount() == paramCount;
            }
        };
    }

    @Override
    public LSFDeclarationResolveResult resolveNoCache() {
        Collection<LSFMetaCodeDeclarationStatement> declarations = new ArrayList<LSFMetaCodeDeclarationStatement>((Collection<? extends LSFMetaCodeDeclarationStatement>) super.resolveNoCache().declarations);

        LSFDeclarationResolveResult.ErrorAnnotator errorAnnotator = null;
        if (declarations.size() > 1) {
            final Collection<LSFMetaCodeDeclarationStatement> finalDeclarations = declarations;
            errorAnnotator = new LSFDeclarationResolveResult.ErrorAnnotator() {
                @Override
                public Annotation resolveErrorAnnotation(AnnotationHolder holder) {
                    return resolveAmbiguousErrorAnnotation(holder, finalDeclarations);
                }
            };
        } else if (declarations.isEmpty()) {
            declarations = LSFGlobalResolver.findElements(getNameRef(), getFullNameRef(), getLSFFile(), getStubElementTypes(), Condition.TRUE, Finalizer.EMPTY);

            final Collection<LSFMetaCodeDeclarationStatement> finalDeclarations = declarations;
            errorAnnotator = new LSFDeclarationResolveResult.ErrorAnnotator() {
                @Override
                public Annotation resolveErrorAnnotation(AnnotationHolder holder) {
                    return resolveNotFoundErrorAnnotation(holder, finalDeclarations);
                }
            };
        }

        return new LSFDeclarationResolveResult(declarations, errorAnnotator);
    }

    @Override
    public Annotation resolveAmbiguousErrorAnnotation(AnnotationHolder holder, Collection<? extends LSFDeclaration> declarations) {
        String ambError = "Ambiguous reference";

        String description = "";
        int i = 1;
        List<LSFMetaCodeDeclarationStatement> decls = new ArrayList<LSFMetaCodeDeclarationStatement>((Collection<? extends LSFMetaCodeDeclarationStatement>) declarations);
        for (LSFMetaCodeDeclarationStatement decl : decls) {
            description += decl.getPresentableText();

            if (i < decls.size() - 1) {
                description += ", ";
            } else if (i == decls.size() - 1) {
                description += " and ";
            }

            i++;
        }

        if (!description.isEmpty()) {
            ambError += ": " + description + " match";
        }

        Annotation annotation = holder.createErrorAnnotation(getMetaCodeIdList(), ambError);
        annotation.setEnforcedTextAttributes(LSFReferenceAnnotator.WAVE_UNDERSCORED_ERROR);
        return annotation;
    }

    @Override
    public Annotation resolveNotFoundErrorAnnotation(AnnotationHolder holder, Collection<? extends LSFDeclaration> similarDeclarations) {
        if (similarDeclarations.isEmpty()) {
            return super.resolveNotFoundErrorAnnotation(holder, similarDeclarations);
        }

        String errorText = "Unable to resolve '" + getNameRef() + "' meta: ";
        for (Iterator<? extends LSFDeclaration> iterator = similarDeclarations.iterator(); iterator.hasNext(); ) {
            errorText += ((LSFMetaDeclaration) iterator.next()).getParamCount();
            if (iterator.hasNext()) {
                errorText += ", ";
            }
        }

        int paramCount = getMetaCodeIdList().getMetaCodeIdList().size();
        errorText += " parameter(s) expected; " + paramCount + " found";
        Annotation error = holder.createErrorAnnotation(getMetaCodeIdList(), errorText);
        error.setEnforcedTextAttributes(LSFReferenceAnnotator.WAVE_UNDERSCORED_ERROR);

        return error;
    }

    @Override
    public List<MetaTransaction.InToken> getUsageParams() {
        List<MetaTransaction.InToken> result = new ArrayList<MetaTransaction.InToken>();
        for (LSFMetaCodeId id : getMetaCodeIdList().getMetaCodeIdList())
            result.add(MetaTransaction.parseToken(id));
        return result;
    }

    @Override
    public boolean isResolveToVirt(LSFMetaDeclaration virtDecl) {
        return LSFGlobalResolver.findElements(getNameRef(), getFullNameRef(), (LSFFile) getContainingFile(), Collections.singleton(getStubElementType()), virtDecl, getCondition()).contains(virtDecl);
    }

    @Override
    public String getPreceedingTab() {
        ASTNode treePrev = getNode().getTreePrev();
/*        if(treePrev==null) {
            PsiElement parent = getParent();
            assert parent instanceof LSFStatements;
            treePrev = parent.getNode().getTreePrev(); // предполагается что тут statements будут
        }*/
        if (treePrev != null && LSFParserDefinition.isWhiteSpace(treePrev.getElementType())) { // сохраним табуляцию
            String whitespace = treePrev.getText();
            return whitespace.substring(whitespace.lastIndexOf('\n') + 1);
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
                if (!body.getText().equals(parsed.getText()))
                    body.replace(parsed);
            } else {
                getNode().addChild(parsed.getNode(), getNode().findChildByType(LSFTypes.SEMI));
            }
        }
    }

    @Override
    public void dropInlinedBody() {
        LSFMetaCodeBody body = getMetaCodeBody();
        if (body != null)
            body.delete();
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return LSFIcons.META_REFERENCE;
    }
}
