package com.lsfusion.lang;

import com.intellij.codeInsight.FileModificationService;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.util.IncorrectOperationException;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.lang.psi.LSFGlobalResolver;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import com.lsfusion.lang.psi.references.LSFModuleReference;
import com.lsfusion.lang.psi.references.LSFNamespaceReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class LSFRequireModuleFix implements IntentionAction {
    private final SmartPsiElementPointer<LSFModuleDeclaration> modulePointer;
    private final String requiredModuleName;

    public LSFRequireModuleFix(@NotNull LSFModuleDeclaration moduleDeclaration, @NotNull String requiredModuleName) {
        modulePointer = SmartPointerManager.getInstance(moduleDeclaration.getProject()).createSmartPsiElementPointer(moduleDeclaration);
        this.requiredModuleName = requiredModuleName;
    }

    @Nullable
    public static LSFModuleDeclaration findModuleDeclaration(@NotNull LSFFile file) {
        LSFModuleDeclaration moduleDeclaration = file.getModuleDeclaration();
        if (moduleDeclaration != null) {
            return moduleDeclaration;
        }

        PsiElement context = file.getContext();
        PsiFile containingFile = context == null ? null : context.getContainingFile();
        if (containingFile instanceof LSFFile lsfFile) {
            return lsfFile.getModuleDeclaration();
        }
        return null;
    }

    @Override
    public @IntentionName @NotNull String getText() {
        return "Require module " + requiredModuleName;
    }

    @Override
    public @IntentionFamilyName @NotNull String getFamilyName() {
        return "Require lsFusion module";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        LSFModuleDeclaration moduleDeclaration = modulePointer.getElement();
        return moduleDeclaration != null && shouldAddRequire(moduleDeclaration);
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        LSFModuleDeclaration moduleDeclaration = modulePointer.getElement();
        if (moduleDeclaration != null && FileModificationService.getInstance().prepareFileForWrite(moduleDeclaration.getContainingFile())) {
            WriteCommandAction.runWriteCommandAction(project, () -> applyRequire(moduleDeclaration));
        }
    }

    private void applyRequire(@NotNull LSFModuleDeclaration moduleDeclaration) {
        if (shouldAddRequire(moduleDeclaration)) {
            LSFModuleDeclaration replacement = createUpdatedModuleDeclaration(moduleDeclaration);
            if (replacement != null) {
                moduleDeclaration.replace(replacement);
                LSFGlobalResolver.cached.clear();
            }
        }
    }

    private boolean shouldAddRequire(@NotNull LSFModuleDeclaration moduleDeclaration) {
        String currentModuleName = moduleDeclaration.getGlobalName();
        if (requiredModuleName.equals(currentModuleName)) {
            return false;
        }

        for (LSFModuleReference requireRef : moduleDeclaration.getRequireRefs()) {
            if (requiredModuleName.equals(requireRef.getNameRef())) {
                return false;
            }
        }
        return true;
    }

    @Nullable
    private LSFModuleDeclaration createUpdatedModuleDeclaration(@NotNull LSFModuleDeclaration moduleDeclaration) {
        String moduleName = moduleDeclaration.getGlobalName();
        if (moduleName != null) {
            Set<String> requireNames = new LinkedHashSet<>();
            for (LSFModuleReference requireRef : moduleDeclaration.getRequireRefs()) {
                String requireName = requireRef.getNameRef();
                if (requireName != null) {
                    requireNames.add(requireName);
                }
            }
            requireNames.add(requiredModuleName);

            StringBuilder headerText = new StringBuilder();
            appendHeaderLine(headerText, "MODULE " + moduleName + ";");
            appendHeaderLine(headerText, "REQUIRE " + String.join(", ", requireNames) + ";");

            List<String> priorityNames = new ArrayList<>();
            for (LSFNamespaceReference priorityRef : moduleDeclaration.getPriorityRefs()) {
                String priorityName = priorityRef.getNameRef();
                if (priorityName != null) {
                    priorityNames.add(priorityName);
                }
            }
            if (!priorityNames.isEmpty()) {
                appendHeaderLine(headerText, "PRIORITY " + String.join(", ", priorityNames) + ";");
            }

            LSFNamespaceReference explicitNamespaceRef = moduleDeclaration.getExplicitNamespaceRef();
            if (explicitNamespaceRef != null) {
                appendHeaderLine(headerText, "NAMESPACE " + explicitNamespaceRef.getNameRef() + ";");
            }

            return ((LSFFile) LSFElementGenerator.createDummyFile(moduleDeclaration.getProject(), headerText.toString())).getModuleDeclaration();
        }
        return null;
    }

    private static void appendHeaderLine(@NotNull StringBuilder headerText, @NotNull String line) {
        if (!headerText.isEmpty()) {
            headerText.append('\n');
        }
        headerText.append(line);
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }
}