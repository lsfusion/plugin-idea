package com.lsfusion.util;

import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;

import java.util.*;

public class DesignUtils {

    public static List<PsiElement> sortByModules(LSFFormDeclaration formDecl, Map<PsiElement, LSFModuleDeclaration> elementToModule) {
        List<LSFModuleDeclaration> sortedModules = new ArrayList<>();
        processSorting(formDecl.getLSFFile().getModuleDeclaration(), new HashSet<>(elementToModule.values()), new HashSet<>(), sortedModules);

        List<PsiElement> elementList = new ArrayList<>(elementToModule.keySet());
        elementList.sort((o1, o2) -> {
            assert o1 != null && o2 != null;
            LSFModuleDeclaration module1 = elementToModule.get(o1);
            LSFModuleDeclaration module2 = elementToModule.get(o2);
            if (module1 != module2) {
                return sortedModules.indexOf(module2) - sortedModules.indexOf(module1);
            }
            return o1.getTextOffset() - o2.getTextOffset();
        });
        return elementList;
    }

    private static void processSorting(LSFModuleDeclaration currentModule, Set<LSFModuleDeclaration> all, Set<LSFModuleDeclaration> visited, List<LSFModuleDeclaration> sorted) {
        visited.add(currentModule);
        for (LSFModuleDeclaration moduleDecl : all) {
            if (!visited.contains(moduleDecl)) {
                if (moduleDecl.requires(currentModule)) {
                    processSorting(moduleDecl, all, visited, sorted);
                }
            }
        }
        sorted.add(currentModule);
    }
}
