package com.lsfusion.parameterInfo;

import com.intellij.lang.parameterInfo.CreateParameterInfoContext;
import com.intellij.lang.parameterInfo.ParameterInfoHandler;
import com.intellij.lang.parameterInfo.ParameterInfoUIContext;
import com.intellij.lang.parameterInfo.UpdateParameterInfoContext;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.context.PropertyUsageContext;
import com.lsfusion.lang.psi.Finalizer;
import com.lsfusion.lang.psi.declarations.LSFActionOrPropDeclaration;
import com.lsfusion.lang.psi.declarations.impl.LSFActionOrGlobalPropDeclarationImpl;
import com.lsfusion.lang.psi.references.LSFActionOrPropReference;
import com.lsfusion.lang.psi.references.impl.LSFActionOrPropReferenceImpl;
import com.lsfusion.lang.psi.references.impl.LSFFullNameReferenceImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LSFParameterInfoHandler implements ParameterInfoHandler<PsiElement, LSFParameterInfoHandler.SignatureInfo> {

    @Override
    public @Nullable PsiElement findElementForParameterInfo(@NotNull CreateParameterInfoContext context) {
        InvocationContext invocation = findInvocation(context.getFile(), context.getOffset());
        if (invocation != null) {
            SignatureInfo[] items = collectSignatureInfos(invocation.reference);
            if (items.length > 0) {
                context.setItemsToShow(items);
                return invocation.reference;
            }
        }
        return null;
    }

    @Override
    public void showParameterInfo(@NotNull PsiElement element, @NotNull CreateParameterInfoContext context) {
        InvocationContext invocation = findInvocation(element.getContainingFile(), context.getOffset());
        if (invocation != null) {
            context.showHint(element, invocation.leftParenOffset, this);
        }
    }

    @Override
    public @Nullable PsiElement findElementForUpdatingParameterInfo(@NotNull UpdateParameterInfoContext context) {
        InvocationContext invocation = findInvocation(context.getFile(), context.getOffset());
        return invocation != null ? invocation.reference : null;
    }

    @Override
    public void updateParameterInfo(@NotNull PsiElement element, @NotNull UpdateParameterInfoContext context) {
        InvocationContext invocation = findInvocation(element.getContainingFile(), context.getOffset());
        if (invocation == null) {
            context.removeHint();
            return;
        }
        context.setCurrentParameter(invocation.currentParameterIndex);
    }

    @Override
    public void updateUI(SignatureInfo parameterOwner, @NotNull ParameterInfoUIContext context) {
        int currentParameter = context.getCurrentParameterIndex();
        int highlightStart = -1;
        int highlightEnd = -1;
        boolean disabled = false;
        if (currentParameter >= 0) {
            if (currentParameter < parameterOwner.parameterStartOffsets.length) {
                highlightStart = parameterOwner.parameterStartOffsets[currentParameter];
                highlightEnd = parameterOwner.parameterEndOffsets[currentParameter];
            } else if (parameterOwner.parameterStartOffsets.length > 0) {
                disabled = true;
            }
        }
        context.setupUIComponentPresentation(parameterOwner.presentation, highlightStart, highlightEnd,
                disabled, false, false, context.getDefaultParameterColor());
    }

    private static @Nullable InvocationContext findInvocation(@NotNull PsiFile file, int offset) {
        int leftParenOffset = findInvocationLeftParenOffset(file, offset);
        if (leftParenOffset < 0) {
            return null;
        }
        LSFActionOrPropReference<?, ?> reference = findReferenceBeforeLeftParen(file, leftParenOffset);
        if (reference == null) {
            return null;
        }
        return new InvocationContext(reference, leftParenOffset, getCurrentParameterIndex(file, leftParenOffset, offset));
    }

    private static int findInvocationLeftParenOffset(@NotNull PsiFile file, int offset) {
        CharSequence text = file.getViewProvider().getContents();
        int balance = 0;
        for (int i = Math.min(offset - 1, text.length() - 1); i >= 0; i--) {
            char ch = text.charAt(i);
            if (ch == ')') {
                balance++;
                continue;
            }
            if (ch == '(') {
                if (balance == 0) {
                    return i;
                }
                balance--;
                continue;
            }
            if (balance == 0) {
                if (ch == ';' || ch == '\n' || ch == '\r' || ch == '{' || ch == '}') {
                    break;
                }
            }
        }
        return -1;
    }

    private static @Nullable LSFActionOrPropReference<?, ?> findReferenceBeforeLeftParen(@NotNull PsiFile file, int leftParenOffset) {
        for (PsiElement leaf : findCandidateLeaves(file, leftParenOffset)) {
            LSFActionOrPropReference<?, ?> directReference = PsiTreeUtil.getParentOfType(leaf, LSFActionOrPropReference.class, false);
            if (directReference != null && directReference.getTextRange().getEndOffset() <= leftParenOffset) {
                return directReference;
            }
            for (PsiElement current = leaf; current != null; current = current.getParent()) {
                if (current instanceof PropertyUsageContext usageContext) {
                    LSFActionOrPropReference<?, ?> reference = findReference(usageContext, leftParenOffset);
                    if (reference != null) {
                        return reference;
                    }
                }
            }
        }
        return null;
    }

    private static @Nullable LSFActionOrPropReference<?, ?> findReference(@NotNull PropertyUsageContext owner, int leftParenOffset) {
        LSFActionOrPropReference<?, ?> result = null;
        for (LSFActionOrPropReference<?, ?> reference : PsiTreeUtil.findChildrenOfType(owner, LSFActionOrPropReference.class)) {
            TextRange range = reference.getTextRange();
            if (range.getEndOffset() <= leftParenOffset) {
                if (result == null || range.getEndOffset() > result.getTextRange().getEndOffset()) {
                    result = reference;
                }
            }
        }
        return result;
    }

    private static @NotNull List<PsiElement> findCandidateLeaves(@NotNull PsiFile file, int offset) {
        List<PsiElement> leaves = new ArrayList<>();
        addLeaf(leaves, file.findElementAt(offset));
        if (offset > 0) {
            addLeaf(leaves, file.findElementAt(offset - 1));
        }
        if (offset > 1) {
            addLeaf(leaves, file.findElementAt(offset - 2));
        }
        return leaves;
    }

    private static void addLeaf(@NotNull List<PsiElement> leaves, @Nullable PsiElement leaf) {
        if (leaf != null && !leaves.contains(leaf)) {
            leaves.add(leaf);
        }
    }

    private static int getCurrentParameterIndex(@NotNull PsiFile file, int leftParenOffset, int offset) {
        CharSequence text = file.getViewProvider().getContents();
        int balance = 0;
        int index = 0;

        for (int i = leftParenOffset + 1; i < Math.min(offset, text.length()); i++) {
            char ch = text.charAt(i);
            if (ch == '(') {
                balance++;
            } else if (ch == ')') {
                if (balance == 0) {
                    break;
                }
                balance--;
            } else if (ch == ',' && balance == 0) {
                index++;
            }
        }

        return index;
    }

    private static @NotNull SignatureInfo[] collectSignatureInfos(@NotNull LSFActionOrPropReference<?, ?> reference) {
        LSFActionOrPropReferenceImpl<?, ?> referenceImpl = (LSFActionOrPropReferenceImpl<?, ?>) reference;
        Collection<? extends PsiElement> declarations = LSFFullNameReferenceImpl.findNoConditionElements(referenceImpl, Finalizer.EMPTY);
        if (declarations.isEmpty()) {
            declarations = referenceImpl.multiResolveDecl(true).declarations;
        }
        Map<String, SignatureInfo> infos = new LinkedHashMap<>();
        for (PsiElement declaration : declarations) {
            if (declaration instanceof LSFActionOrPropDeclaration actionOrPropDeclaration) {
                SignatureInfo info = createSignatureInfo(actionOrPropDeclaration);
                infos.putIfAbsent(info.presentation, info);
            }
        }
        return infos.values().toArray(new SignatureInfo[0]);
    }

    private static @NotNull SignatureInfo createSignatureInfo(@NotNull LSFActionOrPropDeclaration declaration) {
        List<String> parameters = buildParameterPresentations(declaration);
        StringBuilder builder = new StringBuilder();
        builder.append(declaration.getDeclName()).append('(');

        int[] starts = new int[parameters.size()];
        int[] ends = new int[parameters.size()];
        for (int i = 0; i < parameters.size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            starts[i] = builder.length();
            builder.append(parameters.get(i));
            ends[i] = builder.length();
        }
        builder.append(')');

        return new SignatureInfo(builder.toString(), starts, ends);
    }

    private static @NotNull List<String> buildParameterPresentations(@NotNull LSFActionOrPropDeclaration declaration) {
        List<String> result = new ArrayList<>();
        List<LSFClassSet> classes = declaration.resolveParamClasses();
        List<String> names = declaration instanceof LSFActionOrGlobalPropDeclarationImpl<?, ?> globalDeclaration
                ? globalDeclaration.resolveParamNames()
                : null;
        int parameterCount = classes != null ? classes.size() : names != null ? names.size() : 0;

        for (int i = 0; i < parameterCount; i++) {
            String parameterName = names != null && i < names.size() ? names.get(i) : null;
            LSFClassSet parameterClass = classes != null && i < classes.size() ? classes.get(i) : null;
            String parameterType = parameterClass != null ? parameterClass.toString() : "?";
            if (parameterName != null && !parameterName.isBlank()) {
                result.add(parameterName + ": " + parameterType);
            } else {
                result.add(parameterType);
            }
        }

        return result;
    }

    private static final class InvocationContext {
        private final LSFActionOrPropReference<?, ?> reference;
        private final int leftParenOffset;
        private final int currentParameterIndex;

        private InvocationContext(LSFActionOrPropReference<?, ?> reference, int leftParenOffset, int currentParameterIndex) {
            this.reference = reference;
            this.leftParenOffset = leftParenOffset;
            this.currentParameterIndex = currentParameterIndex;
        }
    }

    public static class SignatureInfo {
        private final String presentation;
        private final int[] parameterStartOffsets;
        private final int[] parameterEndOffsets;

        private SignatureInfo(String presentation, int[] parameterStartOffsets, int[] parameterEndOffsets) {
            this.presentation = presentation;
            this.parameterStartOffsets = parameterStartOffsets;
            this.parameterEndOffsets = parameterEndOffsets;
        }
    }
}
