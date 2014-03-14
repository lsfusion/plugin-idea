package com.lsfusion.design.model;

import com.intellij.openapi.project.Project;
import com.lsfusion.LSFIcons;
import com.lsfusion.design.ui.FlexAlignment;

import javax.swing.*;
import java.util.Map;

public class TreeGroupView extends ComponentView {
    public TreeGroupView() {
        this("");
    }

    public TreeGroupView(String sID) {
        super(sID);
        flex = 1;
        alignment = FlexAlignment.STRETCH;
    }

    @Override
    public String getCaption() {
        return "Tree";
    }

    @Override
    public Icon getIcon() {
        return LSFIcons.Design.TREE_GROUP;
    }

    @Override
    protected JComponent createWidgetImpl(Project project, Map<ComponentView, Boolean> selection) {
        return super.createWidgetImpl(project, selection);
        //todo: 
    }
}
