package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTFactory;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.LSFIcons;
import com.lsfusion.lang.LSFParserDefinition;
import com.lsfusion.lang.LSFResolvingError;
import com.lsfusion.lang.meta.MetaTransaction;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFDeclaration;
import com.lsfusion.lang.psi.declarations.LSFMetaDeclaration;
import com.lsfusion.lang.psi.references.LSFMetaReference;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import com.lsfusion.lang.psi.stubs.types.MetaStubElementType;
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

    protected abstract boolean isInline(); 
    public abstract LSFMetaCodeBody getMetaCodeBody();

    protected abstract LSFMetaCodeStatementHeader getMetaCodeStatementHeader();

    protected LSFMetacodeUsage getMetacodeUsage() {
        return getMetaCodeStatementHeader().getMetacodeUsage();
    }

    protected LSFMetaCodeIdList getMetaCodeIdList() {
        return getMetaCodeStatementHeader().getMetaCodeIdList();
    }

    @Override
    public boolean isCorrect() {
        return super.isCorrect() && getMetacodeUsage() != null && getMetaCodeIdList() != null && PsiTreeUtil.getChildOfType(this, LSFMetaCodeStatementSemi.class) != null;
    }

    @Override
    protected LSFCompoundID getCompoundID() {
        return getMetacodeUsage().getCompoundID();
    }

    public int getParamCount() {
        return getMetaCodeIdList().getMetaCodeIdList().size();
    }
    
    @Override
    public Condition<LSFMetaDeclaration> getCondition() {
        final int paramCount = getParamCount();
        return decl -> decl.getParamCount() == paramCount;
    }

    @Override
    protected Collection<? extends LSFMetaDeclaration> resolveNoConditionDeclarations() {
        return LSFFullNameReferenceImpl.findNoConditionElements(this, Finalizer.EMPTY);
    }

    @Override
    public LSFResolvingError resolveAmbiguousErrorAnnotation(Collection<? extends LSFDeclaration> declarations) {
        String ambError = "Ambiguous reference";

        String description = "";
        int i = 1;
        List<LSFMetaCodeDeclarationStatement> decls = new ArrayList<>((Collection<? extends LSFMetaCodeDeclarationStatement>) declarations);
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

        return new LSFResolvingError(getMetaCodeIdList(), ambError, true);
    }

    @Override
    public LSFResolvingError resolveNotFoundErrorAnnotation(Collection<? extends LSFDeclaration> similarDeclarations, boolean canBeDeclaredAfterAndNotChecked) {
        if (similarDeclarations.isEmpty()) {
            return super.resolveNotFoundErrorAnnotation(similarDeclarations, canBeDeclaredAfterAndNotChecked);
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
        return new LSFResolvingError(getMetaCodeIdList(), errorText, true);
    }

    @Override
    public List<MetaTransaction.InToken> getUsageParams() {
        List<MetaTransaction.InToken> result = new ArrayList<>();
        for (LSFMetaCodeId id : getMetaCodeIdList().getMetaCodeIdList())
            result.add(MetaTransaction.parseToken(id));
        return result;
    }

    @Override
    public boolean isResolveToVirt(LSFMetaDeclaration virtDecl) {
        return LSFGlobalResolver.findElements(getNameRef(), getFullNameRef(), (LSFFile) getContainingFile(), getOffsetRef(), LSFLocalSearchScope.createFrom(this), Collections.singleton(getStubElementType()), getCondition(), Collections.singletonList(virtDecl)).contains(virtDecl);
    }

    @Override
    public Integer getOffsetRef() {
        return null; // metas doesn't respect offsets
    }

    @Override
    public void setInlinedBody(LSFMetaCodeBody parsed) {
        if(getProject().isDisposed())
            return;
        if(isInline()) {
            if(parsed != null && isCorrect()) {
                PsiElement parent = getParent();
                if(parent instanceof LSFScriptStatement || parent instanceof LSFLazyMetaDeclStatement) {
                    ASTNode scriptNode = parent.getNode();
                    ASTNode metaNode = getNode();
                    List<ASTNode> inlineNodes = new ArrayList<>();
                    
                    // вырезаем первую { и перевод строки
                    ASTNode from = parsed.getNode().getFirstChildNode();
                    while(from.getElementType() != LSFTypes.META_CODE_BODY_LEFT_BRACE)
                        from = from.getTreeNext();
                    from = from.getTreeNext();
                    while(LSFParserDefinition.isWhiteSpace(from.getElementType())) {
                        String text = from.getText();
                        from = from.getTreeNext();

                        int nextLine = text.indexOf('\n');
                        if(nextLine >= 0) {
                            text = text.substring(nextLine+1, text.length());
                            if(!text.isEmpty())
                                inlineNodes.add(ASTFactory.whitespace(text));
                            break;
                        }                            
                    }

                    // вырезаем последнюю } и перевод строки
                    ASTNode lastNode = null;
                    ASTNode to = parsed.getNode().getLastChildNode();
                    if (from != to) { //empty meta code check
                        while(to.getElementType() != LSFTypes.META_CODE_BODY_RIGHT_BRACE)
                            to = to.getTreePrev();
                        to = to.getTreePrev();
                        while(LSFParserDefinition.isWhiteSpace(to.getElementType())) {
                            String text = to.getText();
                            to = to.getTreePrev();
                            int nextLine = text.indexOf('\n');
                            if(nextLine >= 0) {
                                text = text.substring(0, nextLine);
                                if(!text.isEmpty())
                                    lastNode = ASTFactory.whitespace(text);
                                break;
                            }
                        }

                        ASTNode child = from;
                        while (true) {
                            inlineNodes.add(child);

                            if(child.equals(to))
                                break;
                            child = child.getTreeNext();
                        }
                        if(lastNode != null)
                            inlineNodes.add(lastNode);
                    }

                    ASTNode nextNode = metaNode.getTreeNext();
                    for(ASTNode inlineNode : inlineNodes)
                        scriptNode.addChild(inlineNode, nextNode);
                    scriptNode.removeChild(metaNode);
                }
            }
        } else {
            LSFMetaCodeBody body = getMetaCodeBody();
            if (parsed == null || !isCorrect()) {
                if (body != null && body.isWritable())
                    body.delete();
            } else {
                if (body != null) {
                    if (!body.getText().equals(parsed.getText()))
                        body.replace(parsed);
                } else {
                    LSFMetaCodeStatementSemi semi = PsiTreeUtil.getChildOfType(this, LSFMetaCodeStatementSemi.class);
                    getNode().addChild(parsed.getNode(), semi != null ? semi.getNode() : null);
                }
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
