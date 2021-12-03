package com.lsfusion;

import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.util.Function;
import com.lsfusion.util.ReflectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.function.Supplier;

public class LSFLineMarkerInfo {
    public static LineMarkerInfo create(@NotNull PsiElement element,
                                        @NotNull TextRange range,
                                        Icon icon,
                                        @Nullable Function<PsiElement, String> tooltipProvider,
                                        @Nullable GutterIconNavigationHandler<PsiElement> navHandler,
                                        @NotNull GutterIconRenderer.Alignment alignment) {
        // added reflection access in order not to lose Idea 2020.2- compatibility
        // todo: make direct constructor calls after discontinuation of Idea 2020.2 support 
        return ReflectionUtils.createByPrivateConstructor(LineMarkerInfo.class,
                new Class[]{PsiElement.class,
                        TextRange.class,
                        Icon.class,
                        Supplier.class,
                        Function.class,
                        GutterIconNavigationHandler.class,
                        GutterIconRenderer.Alignment.class},
                element,
                range,
                icon,
                null,
                tooltipProvider,
                navHandler,
                alignment);
    }
}
