package com.lsfusion.mcp;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.lang.psi.LSFId;
import com.lsfusion.lang.psi.LSFMetaCodeStatement;
import com.lsfusion.lang.psi.declarations.LSFGlobalDeclaration;
import com.lsfusion.lang.psi.references.LSFGlobalReference;
import com.lsfusion.lang.psi.references.LSFNamespaceReference;
import com.lsfusion.util.Pair;
import org.json.JSONArray;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Collections;

public interface LSFMCPStatement extends PsiElement {
    // next uses
    static @NonNull Collection<LSFGlobalReference> getNameReferences(LSFMCPStatement el) {
        return PsiTreeUtil.findChildrenOfAnyType(el, false, LSFGlobalReference.class).stream()
                .filter(ref -> !(ref instanceof LSFNamespaceReference))
                .toList();
    }

    static @NonNull Collection<PsiReference> getNameReferenced(LSFGlobalDeclaration<?, ?> cur, GlobalSearchScope scope) {
        LSFId nameIdentifier = cur.getNameIdentifier();
        if (nameIdentifier != null)
            return ReferencesSearch.search(nameIdentifier, scope).findAll().stream()
                    .filter(ref -> !(ref instanceof LSFNamespaceReference))
                    .toList();

        return Collections.emptyList();
    }

    static String getCode(LSFMCPStatement stmt) {
        return stmt.getText();
    }

    static Pair<LSFFile, TextRange> getLocation(LSFMCPStatement stmt) {
        return new Pair<>((LSFFile)stmt.getContainingFile(), stmt.getTextRange());
    }

    static JSONArray getMetacodeStack(LSFMCPStatement stmt) {
        LSFMetaCodeStatement cur = null;
        JSONArray out = new JSONArray();
        while (true) {
            cur = PsiTreeUtil.getParentOfType(cur != null ? cur : stmt, LSFMetaCodeStatement.class, true);
            if(cur == null) break;
            out.put(cur.getMetaCodeStatementHeader().getText());
        }

        if (out.isEmpty()) return null;
        return out;
    }
}
