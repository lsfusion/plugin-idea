package com.lsfusion.util;

import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

//see com.siyeh.ig.psiutils.MethodUtils and com.siyeh.ig.psiutils.TypeUtils for some usefull examples
public class JavaPsiUtils {
    public static boolean hasSuperClass(@Nullable PsiClass clazz, @NotNull String superFQN) {
        while (clazz != null) {
            if (superFQN.equals(clazz.getQualifiedName())) {
                return true;
            }

            clazz = clazz.getSuperClass();
        }
        return false;
    }

    public static boolean isClass(@Nullable PsiClass clazz, String classFqn) {
        return clazz != null && classFqn.equals(clazz.getQualifiedName());
    }
}
