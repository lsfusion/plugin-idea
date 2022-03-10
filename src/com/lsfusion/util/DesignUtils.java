package com.lsfusion.util;

import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;

import java.util.*;

import static com.lsfusion.util.BaseUtils.nullEquals;

public class DesignUtils {

    public static List<PsiElement> sortByModules(List<LSFModuleDeclaration> requiredModules, Map<PsiElement, LSFModuleDeclaration> elementToModule) {
        List<PsiElement> elementList = new ArrayList<>(elementToModule.keySet());
        elementList.sort((o1, o2) -> {
            assert o1 != null && o2 != null;
            LSFModuleDeclaration module1 = elementToModule.get(o1);
            LSFModuleDeclaration module2 = elementToModule.get(o2);
            if (module1 != module2) {
                return requiredModules.indexOf(module1) - requiredModules.indexOf(module2);
            }
            return o1.getTextOffset() - o2.getTextOffset();
        });
        return elementList;
    }

    public static List<LSFModuleDeclaration> getRequiredModules(LSFModuleDeclaration currentModule) {
        return getRequiredModules(currentModule, new HashSet<>());
    }

    private static List<LSFModuleDeclaration> getRequiredModules(LSFModuleDeclaration currentModule, Set<LSFModuleDeclaration> usedModules) {
        List<LSFModuleDeclaration> result = new ArrayList<>();
        if (!usedModules.contains(currentModule)) {
            usedModules.add(currentModule);
            if (!nullEquals(currentModule.getName(), "System")) {
                for (LSFModuleDeclaration module : currentModule.getRequireModules()) {
                    for (LSFModuleDeclaration child : getRequiredModules(module, usedModules)) {
                        if (!result.contains(child)) {
                            result.add(child);
                        }
                    }
                }
            }
            result.add(currentModule);
        }
        return result;
    }
}
