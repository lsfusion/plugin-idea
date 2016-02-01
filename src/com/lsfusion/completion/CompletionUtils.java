package com.lsfusion.completion;

import com.intellij.codeInsight.TailType;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.lsfusion.lang.psi.LSFFile;
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
        return PropertiesComponent.getInstance(project).getBoolean(CompletionUtils.LSF_COMPLETION_ENABLED, true);
    }

    public static void setCompletionEnabled(Project project, boolean enabled) {
        PropertiesComponent.getInstance(project).setValue(CompletionUtils.LSF_COMPLETION_ENABLED, Boolean.toString(enabled));
    }

    public static LookupElement createLookupElement(LSFDeclaration declaration, double priority) {
        return createLookupElement(declaration.getDeclName(), declaration, "", "", declaration.getLSFFile().getName(), declaration.getIcon(0), priority);
    }

    public static LookupElement createLookupElement(String lookupString, PsiElement lookupObject, String additionalIdInfo, String additionalInfo, String typeText, Icon icon, double priority) {
        return createLookupElement(lookupString, lookupObject, additionalIdInfo, additionalInfo, typeText, icon, priority, null, false, null);
    }

    public static LookupElement createLookupElement(String lookupString, double priority, boolean bold, InsertHandler insertHandler) {
        return createLookupElement(lookupString, null, null, null, priority, null, bold, insertHandler);
    }

    public static LookupElement createLookupElement(String lookupString, String additionalInfo, String typeText, Icon icon, double priority, TailType tailType, boolean bold, InsertHandler insertHandler) {
        return createLookupElement(lookupString, null, additionalInfo, "", typeText, icon, priority, tailType, bold, insertHandler);
    }

    public static LookupElement createLookupElement(String lookupString, PsiElement lookupObject, String additionalIdInfo, String additionalInfo, String typeText, Icon icon, double priority, TailType tailType, boolean bold, InsertHandler insertHandler) {
//        lookupObject = null;
        LookupElementBuilder elementBuilder;
        if (lookupObject != null) {
            String idObject = lookupString + (typeText != null ? "_" + typeText : "") + (additionalIdInfo != null ? "_" + additionalIdInfo : "");
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

    public static <G extends LSFDeclaration> List<LookupElement> getVariantsFromIndices(String namespace, LSFFile file, Collection<? extends StringStubIndexExtension> indices, double priority, @NotNull GlobalSearchScope scope) {
        return getVariantsFromIndices(namespace, file.getProject(), indices, priority, scope);
        
    }
    
    public static <G extends LSFDeclaration> List<LookupElement> getVariantsFromIndices(String namespace, Project project, Collection<? extends StringStubIndexExtension> indices, double priority, @NotNull GlobalSearchScope scope) {
        List<LookupElement> result = new ArrayList<LookupElement>();

        try {
            for (StringStubIndexExtension index : indices) {
                Collection<String> allKeys = index.getAllKeys(project);
                for (String variant : allKeys) {
                    Collection<G> declarations = index.get(variant, project, scope);
                    for (G declaration : declarations) {
                        boolean add = true;
                        if (declaration instanceof LSFFullNameDeclaration) {
                            String declNamespace = ((LSFFullNameDeclaration) declaration).getNamespaceName();
                            if (namespace != null && !namespace.equals(declNamespace)) {
                                add = false;
                            }
                        }
                        if (add) {
                            result.add(
                                    createLookupElement(declaration, priority)
                            );
                        }
                    }
                }
            }
        } catch (ProcessCanceledException pce) {
            return result;
        }

        return result;
    }

    public static <G extends LSFFullNameDeclaration> List<G> getDeclarationsFromScope(Project project, GlobalSearchScope scope, StringStubIndexExtension index) {
        List<G> result = new ArrayList<G>();

        Collection<String> propKeys = index.getAllKeys(project);
//        quickLog("After getAllKeys: " + propKeys.size());
        for (String propKey : propKeys) {
            Collection<G> declarations = index.get(propKey, project, scope);
            for (G declaration : declarations) {
                result.add(declaration);
            }
        }

        return result;
    }
}
