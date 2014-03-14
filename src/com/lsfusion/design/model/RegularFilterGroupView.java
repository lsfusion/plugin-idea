package com.lsfusion.design.model;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBCheckBox;
import com.lsfusion.LSFIcons;

import javax.swing.*;
import java.util.Map;

public class RegularFilterGroupView extends ComponentView {
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
    protected JComponent createWidgetImpl(Project project, Map<ComponentView, Boolean> selection) {
        return new JBCheckBox("RegularFilter");
    }
}
