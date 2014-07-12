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

    public static int hasOneOfSuperClasses(@Nullable PsiClass clazz, @NotNull String... superFQN) {
        while (clazz != null) {
            for(int i=0;i<superFQN.length;i++) {
                if (superFQN[i].equals(clazz.getQualifiedName())) {
                    return i;
                }
            }
    
            clazz = clazz.getSuperClass();
        }
        return -1;
    }

    public static boolean isClass(@Nullable PsiClass clazz, String classFqn) {
        return clazz != null && classFqn.equals(clazz.getQualifiedName());
    }
}
