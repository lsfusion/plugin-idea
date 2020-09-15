package com.lsfusion.dependencies.property;

import com.intellij.codeHighlighting.Pass;
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.JBColor;
import com.intellij.util.Function;
import com.lsfusion.actions.ToggleShowTableAction;
import com.lsfusion.lang.psi.LSFId;
import com.lsfusion.lang.psi.LSFSimpleName;
import com.lsfusion.lang.psi.declarations.LSFAggrParamGlobalPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFGlobalPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFTableDeclaration;
import com.lsfusion.lang.psi.declarations.impl.LSFTableDeclarationImpl;
import com.lsfusion.lang.psi.indexes.TableIndex;
import com.lsfusion.util.LSFFileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PropertyTableLineMarkerProvider implements LineMarkerProvider {
    @Nullable
    @Override
    public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {
        return null;
    }

    @Override
    public void collectSlowLineMarkers(@NotNull List<? extends PsiElement> elements, @NotNull Collection<? super LineMarkerInfo<?>> result) {
        if (!elements.isEmpty() && !ToggleShowTableAction.isShowTableEnabled(elements.iterator().next().getProject())) {
            return;
        }
        
        Document document = null;
        Set<Integer> usedLines = new HashSet<>();
        for (PsiElement element : elements) {
            if (document == null) {
                document = PsiDocumentManager.getInstance(element.getProject()).getDocument(element.getContainingFile());
            }
            int lineNumber = document.getLineNumber(element.getTextOffset());
            if (!usedLines.contains(lineNumber)) {
                LSFGlobalPropDeclaration propDeclaration = getPropertyDeclaration(element);
                if (propDeclaration != null) {
                    LSFId nameIdentifier = propDeclaration.getNameIdentifier();
                    if (nameIdentifier != null && nameIdentifier.getFirstChild() == element) {
                        if(!(propDeclaration instanceof LSFAggrParamGlobalPropDeclaration && ((LSFAggrParamGlobalPropDeclaration) propDeclaration).getAggrPropertyDefinition() == null)) {
                            if (propDeclaration.isCorrect() && (propDeclaration.isDataStoredProperty() || propDeclaration.isPersistentProperty())) {
                                result.add(createLineMarker(element));
                                usedLines.add(lineNumber);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private static LSFGlobalPropDeclaration getPropertyDeclaration(PsiElement element) {
        if (element instanceof LeafPsiElement && element.getParent() instanceof LSFSimpleName) {
            return PsiTreeUtil.getParentOfType(element, LSFGlobalPropDeclaration.class);
        }
        return null;
    }

    private LineMarkerInfo<?> createLineMarker(PsiElement psi) {
        return new LineMarkerInfo(
                psi,
                psi.getTextRange(),
                createIcon(),
                Pass.LINE_MARKERS,
                PropertyShowTableTooltipProvider.INSTANCE,
                ShowTableNavigationProvider.INSTANCE,
                GutterIconRenderer.Alignment.RIGHT
        );
    }

    private static Icon createIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                String text = "T";
                g.setColor(new JBColor(new Color(255, 153, 0), new Color(255, 153, 0)));

                g.drawRoundRect(x, y, 12, 12, 2, 2);
                Font textFont = new Font("Dialog", Font.BOLD, 11);
                g.setFont(textFont);

                int textWidth = g.getFontMetrics(textFont).stringWidth(text);
                g.drawString(text, x + (14 - textWidth) / 2, y + 11);
            }

            @Override
            public int getIconWidth() {
                return 12;
            }

            @Override
            public int getIconHeight() {
                return 12;
            }
        };
    }

    private static class PropertyShowTableTooltipProvider implements Function<PsiElement, String> {
        static PropertyShowTableTooltipProvider INSTANCE = new PropertyShowTableTooltipProvider();

        @Override
        public String fun(PsiElement psi) {
            LSFGlobalPropDeclaration propertyDecl = getPropertyDeclaration(psi); 
            String name = propertyDecl != null ? propertyDecl.getTableName() : null;
            return name == null ? "Unknown table" : ("Table: " + name);
        }
    }
    
    private static class ShowTableNavigationProvider implements GutterIconNavigationHandler {
        static ShowTableNavigationProvider INSTANCE = new ShowTableNavigationProvider();

        @Override
        public void navigate(MouseEvent e, PsiElement psi) {
            if (e.getID() == MouseEvent.MOUSE_RELEASED) {
                LSFGlobalPropDeclaration propertyDecl = getPropertyDeclaration(psi);
                String name = propertyDecl != null ? propertyDecl.getTableName() : null;
                if (name != null) {
                    int underscoreIndex = name.indexOf("_");
                    if (underscoreIndex != -1) {
                        String namespace = name.substring(0, underscoreIndex);
                        String tableName = name.substring(underscoreIndex + 1);
                        Collection<LSFTableDeclaration> tableDeclarations = TableIndex.getInstance().get(tableName, psi.getProject(), LSFFileUtils.getModuleWithDependenciesScope(psi));
                        for (LSFTableDeclaration declaration : tableDeclarations) {
                            if (declaration.getNamespaceName().equals(namespace)) {
                                ((LSFTableDeclarationImpl) declaration).navigate(true);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }
}
