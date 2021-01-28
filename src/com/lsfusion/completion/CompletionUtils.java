package com.lsfusion.completion;

import com.intellij.codeInsight.TailType;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Conditions;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.lang.psi.LSFGlobalResolver;
import com.lsfusion.lang.psi.LSFLocalSearchScope;
import com.lsfusion.lang.psi.declarations.LSFDeclaration;
import com.lsfusion.lang.psi.declarations.LSFFullNameDeclaration;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.intellij.codeInsight.completion.PrioritizedLookupElement.withPriority;
import static com.intellij.codeInsight.lookup.LookupElementDecorator.withInsertHandler;
import static com.intellij.codeInsight.lookup.TailTypeDecorator.withTail;

public class CompletionUtils {

    public static final String LSF_COMPLETION_ENABLED = "lsfusion.completion.enabled";
    
    public static boolean isCompletionEnabled(Project project) {
        return project != null && PropertiesComponent.getInstance(project).getBoolean(CompletionUtils.LSF_COMPLETION_ENABLED, true);
    }

    public static void setCompletionEnabled(Project project, boolean enabled) {
        PropertiesComponent.getInstance(project).setValue(CompletionUtils.LSF_COMPLETION_ENABLED, Boolean.toString(enabled));
    }

    public static LookupElement createLookupElement(LSFDeclaration declaration, double priority) {
        String declName = declaration.getDeclName();
        if (declName != null) {
            return createLookupElement(declName, declaration, "", null, "", declaration.getLSFFile().getName(), declaration.getIcon(0), priority);
        }
        return null;
    }

    public static LookupElement createLookupElement(@NotNull String lookupString, PsiElement lookupObject, String additionalIdInfo, String additionalInvisibleIdInfo, String additionalInfo, String typeText, Icon icon, double priority) {
        return createLookupElement(lookupString, lookupObject, additionalIdInfo, additionalInvisibleIdInfo, additionalInfo, typeText, icon, priority, null, false, null);
    }

    public static LookupElement createLookupElement(@NotNull String lookupString, double priority, boolean bold, InsertHandler insertHandler) {
        return createLookupElement(lookupString, null, null, null, priority, null, bold, insertHandler);
    }

    public static LookupElement createLookupElement(@NotNull String lookupString, String additionalInfo, String typeText, Icon icon, double priority, TailType tailType, boolean bold, InsertHandler insertHandler) {
        return createLookupElement(lookupString, null, additionalInfo, null, "", typeText, icon, priority, tailType, bold, insertHandler);
    }

    public static LookupElement createLookupElement(@NotNull String lookupString, PsiElement lookupObject, String additionalIdInfo, String additionalInvisibleIdInfo, String additionalInfo, String typeText, Icon icon, double priority, TailType tailType, boolean bold, InsertHandler insertHandler) {
//        lookupObject = null;
        LookupElementBuilder elementBuilder;
        if (lookupObject != null) {
            String idObject = lookupString + (typeText != null ? "_" + typeText : "") + (additionalIdInfo != null ? "_" + additionalIdInfo : "") + (additionalInvisibleIdInfo != null ? "_" + additionalInvisibleIdInfo : "");
            elementBuilder = LookupElementBuilder.create(idObject, lookupString);
        } else {
            elementBuilder = LookupElementBuilder.create(lookupString);
        }
        if (bold) {
            elementBuilder = elementBuilder.withBoldness(true);
        }
        if (icon != null) {
            elementBuilder = elementBuilder.withIcon(icon);
        }

        if (typeText != null) {
            elementBuilder = elementBuilder.withTypeText(typeText);
        }

//        additionalInfo = "[prio=" + priority + "]" + (additionalInfo == null ? "" : additionalInfo);

        if (additionalIdInfo != null) {
            elementBuilder = elementBuilder.withTailText(additionalIdInfo + (additionalInfo != null ? additionalInfo : ""), true);
        }

        LookupElement element = elementBuilder;
        if (tailType != null) {
            element = withTail(element, TailType.SPACE);
        }

        if (insertHandler != null) {
            element = withInsertHandler(element, insertHandler);
        }

        element = withPriority(element, priority);

        return element;
    }

    public static List<LookupElement> getVariantsFromIndices(String namespace, LSFFile file, Collection<? extends StringStubIndexExtension> indices, double priority, @NotNull GlobalSearchScope scope, LSFLocalSearchScope localScope) {
        Project project = file.getProject();
        List<LookupElement> result = new ArrayList<>();

        try {
            for (StringStubIndexExtension index : indices) {
                for (LSFDeclaration declaration : getDeclarationsFromScope(project, scope, localScope, index, declaration -> namespace == null || !(declaration instanceof LSFFullNameDeclaration) || namespace.equals(((LSFFullNameDeclaration) declaration).getNamespaceName()))) {
                    LookupElement lookupElement = createLookupElement(declaration, priority);
                    if (lookupElement != null)
                        result.add(lookupElement);
                }
            }
        } catch (ProcessCanceledException pce) {
            return result;
        }

        return result;
    }

    public static <G extends LSFFullNameDeclaration> List<G> getDeclarationsFromScope(Project project, GlobalSearchScope scope, LSFLocalSearchScope localScope, StringStubIndexExtension index) {
        return getDeclarationsFromScope(project, scope, localScope, index, Conditions.alwaysTrue());
    }
    public static <G extends LSFDeclaration> List<G> getDeclarationsFromScope(Project project, GlobalSearchScope scope, LSFLocalSearchScope localScope, StringStubIndexExtension index, Condition<G> condition) {
        List<G> result = new ArrayList<>();

        Collection<String> propKeys = index.getAllKeys(project);
//        quickLog("After getAllKeys: " + propKeys.size());
        for (String propKey : propKeys) {
            Collection<G> declarations = LSFGlobalResolver.getItemsFromIndex(index, propKey, project, scope, localScope);
            for (G declaration : declarations) {
                if(condition.value(declaration))
                    result.add(declaration);
            }
        }

        return result;
    }
}
