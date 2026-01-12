package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.Segment;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.lsfusion.lang.LSFResolvingError;
import com.lsfusion.lang.meta.MetaTransaction;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFActionOrGlobalPropDeclaration;
import com.lsfusion.lang.psi.declarations.SyntheticPropertyStatement;
import com.lsfusion.lang.psi.declarations.LSFDeclaration;
import com.lsfusion.lang.psi.references.LSFReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class LSFReferenceImpl<T extends LSFDeclaration> extends LSFElementImpl implements LSFReference<T>, PsiPolyVariantReference {

    public LSFReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return this;
    }

    @Override
    public PsiElement getElement() {
        return this;
    }

    @Override
    public TextRange getRangeInElement() {
        final TextRange textRange = getTextRange();
        TextRange nameRange = getSimpleName().getTextRange();

        return new TextRange(
                nameRange.getStartOffset() - textRange.getStartOffset(),
                nameRange.getEndOffset() - textRange.getStartOffset()
        );
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        return getText(); // пока так
    }

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        handleElementRename(newElementName, null);
        return this;
    }

    public void handleElementRename(String newElementName, MetaTransaction transaction) {
        getSimpleName().setName(newElementName, transaction);
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        return this;
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        // need to prevent resolving when it's not needed
        boolean result = isDeclarationType(PsiTreeUtil.getParentOfType(element, LSFDeclaration.class)) && getManager().areElementsEquivalent(resolve(), element);
        if(!result) {
            T decl = resolveDecl();
            result = decl instanceof SyntheticPropertyStatement && decl.isEquivalentTo(element.getParent());
        }
        return result;
    }

    // should return instanceof T
    protected abstract boolean isDeclarationType(PsiElement element);

    @Override
    public String getNameRef() {
        return getSimpleName().getText();
    }

    @Override
    public LSFId resolve() {
        T decl = resolveDecl();
        if (decl == null)
            return null;
        return decl.getNameIdentifier();
    }

    public T resolveDecl() {
        LSFResolveResult resolveResult = multiResolveDecl(true);
        if (resolveResult == null) {
            return null;
        }
        return LSFResolveUtil.singleResolve(resolveResult.declarations);
    }

    public LSFResolveResult multiResolveDecl(boolean incompleteCode) {
        return ResolveCache.getInstance(getProject()).resolveWithCaching(this, LSFResolver.INSTANCE, true, incompleteCode);
    }

    public LSFResolveResult.ErrorAnnotator resolveDefaultErrorAnnotator(final Collection<? extends LSFDeclaration> decls, boolean canBeDeclaredAfterAndNotChecked) {
        LSFResolveResult.ErrorAnnotator errorAnnotator = null;
        if (decls.isEmpty()) {
            errorAnnotator = new LSFResolveResult.NotFoundErrorAnnotator(this, decls, canBeDeclaredAfterAndNotChecked);
        } else if (decls.size() > 1) {
            errorAnnotator = new LSFResolveResult.AmbigiousErrorAnnotator(this, decls);
        }
        return errorAnnotator;
    }

    @Override
    public LSFResolvingError resolveErrorAnnotation(final AnnotationHolder holder) {
        LSFResolveResult resolveResult = multiResolveDecl(true);
        LSFResolvingError error = resolveResult.resolveErrorAnnotation(holder);
        if (error == null) {
            for (LSFDeclaration decl : resolveResult.declarations) {
                if (decl instanceof LSFActionOrGlobalPropDeclaration) {
                    LSFNonEmptyPropertyOptions propertyOptions = ((LSFActionOrGlobalPropDeclaration) decl).getNonEmptyPropertyOptions();
                    if (propertyOptions != null)
                        error = checkDeprecated(propertyOptions.getAnnotationSettingList());
                    LSFNonEmptyActionOptions actionOptions = ((LSFActionOrGlobalPropDeclaration) decl).getNonEmptyActionOptions();
                    if (actionOptions != null)
                        error = checkDeprecated(actionOptions.getAnnotationSettingList());
                }
            }
        }
        return error;
    }

    private LSFResolvingError checkDeprecated(List<LSFAnnotationSetting> annotationList) {
        for (LSFAnnotationSetting annotation : annotationList) {
            if (annotation.getSimpleName().getName().equals("deprecated")) {
                List<LSFStringLiteral> options = annotation.getStringLiteralList();
                String since = !options.isEmpty() ? (" since " + options.get(0).getValue()) : "";
                String message = options.size() > 1 ? (", " + options.get(1).getValue()) : "";
                return new LSFResolvingError(this, getTextRange(), "Deprecated" + since + message, false, true);
            }
            return null;
        }
        return null;
    }

    public LSFResolvingError resolveAmbiguousErrorAnnotation(Collection<? extends LSFDeclaration> declarations) {
        return new LSFResolvingError(this, "Ambiguous reference", true);
    }

    public LSFResolvingError resolveNotFoundErrorAnnotation(Collection<? extends LSFDeclaration> similarDeclarations, boolean canBeDeclaredAfterAndNotChecked) {
        return new LSFResolvingError(this, "Cannot resolve symbol '" + getNameRef() + "'" + (canBeDeclaredAfterAndNotChecked ? " or maybe it is declared later" : ""), false);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        // handled by ASTCompletionContributor
        return new Object[0];
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        LSFResolveResult decls = multiResolveDecl(true);
        return LSFResolveUtil.toCandidateInfoArray(new ArrayList<PsiElement>(decls.declarations));
    }

    public static String getPresentableText(LSFReference ref) {
        return ref.getNameRef();
    }

    public static String getLocationString(LSFReference ref) {
        final PsiFile file = ref.getLSFFile();
        final Document document = PsiDocumentManager.getInstance(ref.getProject()).getDocument(file);
        final SmartPsiElementPointer pointer = SmartPointerManager.getInstance(ref.getProject()).createSmartPsiElementPointer(ref);
        final Segment range = pointer.getRange();
        int lineNumber = -1;
        int linePosition = -1;
        if (document != null && range != null) {
            lineNumber = document.getLineNumber(range.getStartOffset()) + 1;
            linePosition = range.getStartOffset() - document.getLineStartOffset(lineNumber - 1) + 1;
        }

        return file.getName() + "(" + lineNumber + ":" + linePosition + ")";
    }

    public static Icon getIcon(LSFReference ref, boolean unused) {
        return null;
    }

    @Nullable
    @Override
    public String getPresentableText() {
        return getPresentableText(this);
    }

    @Nullable
    @Override
    public String getLocationString() {
        return getLocationString(this);
    }

    @Nullable
    @Override
    public Icon getIcon(boolean unused) {
        return getIcon(this, unused);
    }

    @Override
    public ItemPresentation getPresentation() {
        return this;
    }

    @Override
    public String getName() {
        return getNameRef();
    }
}
