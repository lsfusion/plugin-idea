package com.lsfusion.design.ui;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBTabbedPane;
import com.lsfusion.design.model.ComponentView;
import com.lsfusion.design.model.ContainerView;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

import static java.lang.Math.max;

public class TabbedContainerView extends JComponentPanel {
    public TabbedContainerView(TabbedPane tabbedPane) {
        super(tabbedPane);
    }
    
    public static TabbedContainerView create(Project project, ContainerView container, Map<ComponentView, Boolean> selection, Map<ComponentView, JComponentPanel> componentToWidget, JComponentPanel oldWidget) {
        ComponentView selectedChild = null;
        if (oldWidget != null) {
            TabbedPane oldTabbedPane = (TabbedPane) oldWidget.getComponent(0);
            selectedChild = (ComponentView) ((JComponent) oldTabbedPane.getSelectedComponent()).getClientProperty("componentView");
        }

        JComponent selectedWidget = null;
        TabbedPane tabbedPane = new TabbedPane();
        boolean hasChildren = false;
        for (ComponentView child : container.getChildren()) {
            JComponentPanel childWidget = child.createWidget(project, selection, componentToWidget);
            if (childWidget != null) {
                hasChildren = true;
                String caption = child.getCaption();
                if (caption == null) {
                    caption = "";
                }
                childWidget.putClientProperty("componentView", child);
                tabbedPane.addTab(caption, childWidget);
                if (child == selectedChild) {
                    selectedWidget = childWidget;
                }
            }
        }

        if (hasChildren) {
            if (selectedWidget != null) {
                tabbedPane.setSelectedComponent(selectedWidget);
            }
            return new TabbedContainerView(tabbedPane);
        }
        return null;    
    }
    
    static class TabbedPane extends JBTabbedPane {
        public TabbedPane() {
            super(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        }

        @Override
        public Dimension getMinimumSize() {
            return layoutSize(CachableLayout.minSizeGetter);
        }

        @Override
        public Dimension getPreferredSize() {
            return layoutSize(CachableLayout.prefSizeGetter);
        }

        private Dimension layoutSize(CachableLayout.ComponentSizeGetter sizeGetter) {
            Dimension pref = super.getPreferredSize();

            //заново считаем максимальный размер и вычитаем его, т. к. размеры таб-панели зависят от LAF
            int maxWidth = 0;
            int maxHeight = 0;
            for (int i = 0; i < getTabCount(); i++) {
                Component child = getComponentAt(i);
                if (child != null) {
                    Dimension size = sizeGetter.get(child);
                    if (size != null) {
                        maxWidth = max(maxWidth, size.width);
                        maxHeight = max(maxHeight, size.height);
                    }
                }
            }
            pref.width -= maxWidth;
            pref.height -= maxHeight;

            Component selected = getSelectedComponent();
            if (selected != null) {
                Dimension d = sizeGetter.get(selected);
                pref.width += d.width;
                pref.height += d.height;
            }

            return pref;
        }
    }
}
