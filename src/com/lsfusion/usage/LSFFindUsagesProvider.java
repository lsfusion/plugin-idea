package com.lsfusion.usage;

import com.intellij.lang.HelpID;
import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.lang.LSFLexerAdapter;
import com.lsfusion.lang.psi.LSFId;
import com.lsfusion.lang.psi.LSFSimpleName;
import com.lsfusion.lang.psi.LSFTypes;
import com.lsfusion.lang.psi.declarations.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LSFFindUsagesProvider implements FindUsagesProvider {
    private static final DefaultWordsScanner WORDS_SCANNER =
            new DefaultWordsScanner(
                    new LSFLexerAdapter(), 
                    TokenSet.create(LSFTypes.ID), TokenSet.create(LSFTypes.COMMENTS),
                    TokenSet.EMPTY
            );

    @Nullable
    @Override
    public WordsScanner getWordsScanner() {
        return null;
    }

    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
        return psiElement instanceof LSFId;
    }

    @Nullable
    @Override
    public String getHelpId(@NotNull PsiElement psiElement) {
        return HelpID.FIND_OTHER_USAGES;
    }

    @NotNull
    @Override
    public String getType(@NotNull PsiElement element) {
        LSFDeclaration parent = PsiTreeUtil.getParentOfType(element, LSFDeclaration.class);
        if (parent instanceof LSFClassDeclaration) {
            return "class";
        } else if (parent instanceof LSFFormDeclaration) {
            return "form";
        } else if (parent instanceof LSFNavigatorElementDeclaration) {
            return "navigator element";
        } else if (parent instanceof LSFWindowDeclaration) {
            return "window";
        } else if (parent instanceof LSFActionOrGlobalPropDeclaration) {
            return ((LSFActionOrGlobalPropDeclaration) parent).isAction() ? "action" : "property";
        } else if (parent instanceof LSFTableDeclaration) {
            return "table";
        } else if (parent instanceof LSFGroupDeclaration) {
            return "group";
        } else if (parent instanceof LSFMetaDeclaration) {
            return "metacode";
        } else if (parent instanceof LSFModuleDeclaration) {
            return "module";
        } else if (parent instanceof LSFExplicitNamespaceDeclaration) {
            return "namespace";
        } else if (parent instanceof LSFLocalPropDeclaration) {
            return "local property";
        } else if (parent instanceof LSFObjectDeclaration) {
            return "object";
        } else if (parent instanceof LSFParamDeclaration) {
            return "parameter";
        } else if (parent instanceof LSFStaticObjectDeclaration) {
            return "static object";
        } else if (parent instanceof LSFTreeGroupDecl) {
            return "tree";
        } else if (parent instanceof LSFGroupObjectDeclaration) {
            return "group object";
        } else if (parent instanceof LSFFilterGroupDeclaration) {
            return "filter group";
        } else if (parent instanceof LSFPropertyDrawDeclaration) {
            return "property on form";
        } else if (element instanceof LSFSimpleName) {
            return "lsFusion element";
        } else {
            return "";
        }
    }

    @NotNull
    @Override
    public String getDescriptiveName(@NotNull PsiElement element) {
        if (element instanceof LSFSimpleName) {
            return element.getText();
        } else {
            return "";
        }
    }

    @NotNull
    @Override
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        if (element instanceof LSFSimpleName) {
            return element.getText();
        } else {
            return "";
        }
    }
}