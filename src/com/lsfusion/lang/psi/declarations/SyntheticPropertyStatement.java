package com.lsfusion.lang.psi.declarations;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.openapi.util.*;
import com.intellij.psi.*;
import com.intellij.psi.impl.light.LightElement;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import com.lsfusion.lang.meta.MetaTransaction;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.stubs.StatementPropStubElement;
import com.lsfusion.lang.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.lang.typeinfer.LSFExClassSet;
import com.lsfusion.refactoring.ElementMigration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class SyntheticPropertyStatement extends LightElement implements LSFGlobalPropDeclaration {

    private final LSFStatementActionDeclaration origin;
    private final List<LSFExClassSet> returnParams;

    public SyntheticPropertyStatement(LSFStatementActionDeclaration origin, List<LSFExClassSet> returnParams) {
        super(origin.getManager(), origin.getLanguage());
        this.origin = origin;
        this.returnParams = returnParams;
    }

    @Override
    public List<LSFExClassSet> resolveExParamClassesNoCache() {
        List<LSFExClassSet> result = origin.resolveExParamClassesNoCache();
        result.addAll(returnParams);
        return result;
    }

    @Override
    public @NotNull PsiElement getNavigationElement() {
        return origin;
    }

    @Override
    public @NotNull PsiElement getContext() {
        return origin;
    }

    @Override
    public boolean isNoParams() {
        return origin.isNoParams();
    }

    @Override
    public String getName() {
        return origin.getName();
    }

    @Override
    public PsiElement setName(@NlsSafe @NotNull String name) throws IncorrectOperationException {
        return null;
    }

    @Override
    public String getValuePresentableText() {
        return origin.getValuePresentableText();
    }

    @Override
    public PsiElement getLookupObject() {
        return origin.getLookupObject();
    }

    @Override
    public PsiElement getParent() {
        return origin;
    }

    @Override
    public PsiFile getContainingFile() {
        return origin.getContainingFile();
    }

    @Override
    public @NotNull TextRange getTextRange() {
        return origin.getTextRange();
    }

    @Override
    public int getTextOffset() {
        return origin.getTextOffset();
    }

    @Override
    public String toString() {
        return origin.toString();
    }

    @Override
    public @Nullable LSFNonEmptyActionOptions getNonEmptyActionOptions() {
        return origin.getNonEmptyActionOptions();
    }

    @Override
    public @Nullable LSFNonEmptyPropertyOptions getNonEmptyPropertyOptions() {
        return origin.getNonEmptyPropertyOptions();
    }

    @Override
    public LSFExplicitClasses getExplicitParams() {
        return origin.getExplicitParams();
    }

    @Override
    public String getCaption() {
        return origin.getCaption();
    }

    @Override
    public Set<LSFActionOrGlobalPropDeclaration<?, ?>> getDependencies() {
        return origin.getDependencies();
    }

    @Override
    public String getPresentableText() {
        return origin.getPresentableText();
    }

    @Override
    public @Nullable Icon getIcon(boolean unused) {
        return origin.getIcon();
    }

    @Override
    public boolean isAction() {
        return false;
    }

    @Override
    public boolean isStoredProperty() {
        return false;
    }

    @Override
    public boolean isMaterializedProperty() {
        return false;
    }

    @Override
    public boolean isDataProperty() {
        return false;
    }

    @Override
    public boolean isDataStoredProperty() {
        return false;
    }

    @Override
    public Collection<FullNameStubElementType> getTypes() {
        return List.of();
    }

    @Override
    public boolean isAbstract() {
        return origin.isAbstract();
    }

    @Override
    public LSFExClassSet resolveExValueClassNoCache(boolean infer) {
        return null;
    }

    @Override
    public @Nullable List<LSFExClassSet> inferParamClasses(LSFExClassSet valueClass) {
        return origin.inferParamClasses(valueClass);
    }

    @Override
    public String getNamespaceName() {
        return origin.getNamespaceName();
    }

    @Override
    public String getCanonicalName() {
        return origin.getCanonicalName();
    }

    @Override
    public int getOffset() {
        return origin.getOffset();
    }

    @Override
    public String getGlobalName() {
        return origin.getGlobalName();
    }

    @Override
    public boolean isInMetaDecl() {
        return origin.isInMetaDecl();
    }

    @Override
    public IStubElementType getElementType() {
        return origin.getElementType();
    }

    @Override
    public StatementPropStubElement getStub() {
        return null;
    }

    @Override
    public String getDeclName() {
        return origin.getDeclName();
    }

    @Override
    public ElementMigration getMigration(String newName) {
        return origin.getMigration(newName);
    }

    @Override
    public @Nullable LSFId getNameIdentifier() {
        return origin.getNameIdentifier();
    }

    @Override
    public boolean resolveDuplicates() {
        return origin.resolveDuplicates();
    }

    @Override
    public PsiElement[] processImplementationsSearch() {
        return new PsiElement[0];
    }

    @Override
    public void setName(@NotNull String name, MetaTransaction transaction) {
    }

    @Override
    public boolean isCorrect() {
        return origin.isCorrect();
    }

    @Override
    public LSFFile getLSFFile() {
        return origin.getLSFFile();
    }

    @Override
    public @NotNull Language getLanguage() {
        return origin.getLanguage();
    }

    @Override
    public PsiManager getManager() {
        return origin.getManager();
    }

    @Override
    public int getStartOffsetInParent() {
        return origin.getStartOffsetInParent();
    }

    @Override
    public char @NotNull [] textToCharArray() {
        return origin.textToCharArray();
    }

    @Override
    public boolean textMatches(@NotNull CharSequence text) {
        return origin.textMatches(text);
    }

    @Override
    public boolean textMatches(@NotNull PsiElement element) {
        return origin.textMatches(element);
    }

    @Override
    public PsiElement findElementAt(int offset) {
        return origin.findElementAt(offset);
    }

    @Override
    public boolean isValid() {
        return origin.isValid();
    }

    @Override
    public boolean isWritable() {
        return origin.isWritable();
    }

    @Override
    public boolean isPhysical() {
        return false;
    }

    @Override
    public ASTNode getNode() {
        return origin.getNode();
    }

    @Override
    public String getText() {
        return origin.getText();
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
        return origin.processDeclarations(processor, state, lastParent, place);
    }

    @Override
    public PsiElement getOriginalElement() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SyntheticPropertyStatement that = (SyntheticPropertyStatement) o;
        return origin.equals(that.origin);
    }

    @Override
    public int hashCode() {
        return origin.hashCode();
    }

    @Override
    public boolean isEquivalentTo(PsiElement another) {
        if (this == another) return true;
        if (another == null) return false;
        if (origin.isEquivalentTo(another)) return true;
        if (getClass() != another.getClass()) return false;
        SyntheticPropertyStatement that = (SyntheticPropertyStatement) another;
        return origin.isEquivalentTo(that.origin);
    }

    @Override
    public @NotNull SearchScope getUseScope() {
        return origin.getUseScope();
    }

    @Override
    public @Nullable Icon getIcon(int flags) {
        return origin.getIcon(flags);
    }
}