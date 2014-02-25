package com.simpleplugin.completion;

import com.intellij.codeInsight.TailType;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.simpleplugin.psi.LSFFile;
import com.simpleplugin.psi.declarations.LSFDeclaration;
import com.simpleplugin.psi.declarations.LSFFullNameDeclaration;

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

    public static LookupElement createLookupElement(LSFDeclaration declaration) {
        return createLookupElement(declaration, 5);
    }

    public static LookupElement createLookupElement(LSFDeclaration declaration, double priority) {
        return createLookupElement(declaration.getName(), declaration.getLSFFile().getName(), declaration.getIcon(0), priority);
    }

    public static LookupElement createLookupElement(String lookupString, String typeText, Icon icon) {
        return createLookupElement(lookupString, typeText, icon, 5);
    }

    public static LookupElement createLookupElement(String lookupString, String typeText, Icon icon, double priority) {
        return createLookupElement(lookupString, "", typeText, icon, priority);
    }

    public static LookupElement createLookupElement(String lookupString, String additionalInfo, String typeText, Icon icon, double priority) {
        return createLookupElement(lookupString, additionalInfo, typeText, icon, priority, null, false, null);
    }

    public static LookupElement createLookupElement(String lookupString, double priority, boolean bold, InsertHandler insertHandler) {
        return createLookupElement(lookupString, null, null, null, priority, null, bold, insertHandler);
    }

    public static LookupElement createLookupElement(String lookupString, String additionalInfo, String typeText, Icon icon, double priority, TailType tailType, boolean bold, InsertHandler insertHandler) {
        LookupElementBuilder elementBuilder = LookupElementBuilder.create(lookupString);
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

        if (additionalInfo != null) {
            elementBuilder = elementBuilder.withTailText(additionalInfo, true);
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

    public static <G extends LSFDeclaration> List<LookupElement> getVariantsFromIndices(String namespace, LSFFile file, Collection<? extends StringStubIndexExtension> indices, double priority, GlobalSearchScope scope) {
        List<LookupElement> result = new ArrayList<LookupElement>();

        try {
            Project project = file.getProject();

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
