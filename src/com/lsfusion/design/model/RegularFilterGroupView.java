package com.lsfusion.design.model;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBCheckBox;
import com.lsfusion.LSFIcons;

import javax.swing.*;
import java.util.Map;

public class RegularFilterGroupView extends ComponentView {
    
    public boolean isSingle = true;
    
    public RegularFilterGroupView() {
        this("");
    }

    public RegularFilterGroupView(String sID) {
        super(sID);
    }

    @Override
    public String getCaption() {
        return "Regular filter";
    }

    @Override
    public Icon getIcon() {
        return LSFIcons.Design.FILTER_GROUP;
    }

    @Override
    protected JComponent createWidgetImpl(Project project, Map<ComponentView, Boolean> selection, Map<ComponentView, JComponent> componentToWidget, JComponent oldWidget) {
        return isSingle ? new JBCheckBox("Filter") : new ComboBox(new Object[]{"<All>", "Filter1", "Filter2", "..."}, -1);
    }
}
