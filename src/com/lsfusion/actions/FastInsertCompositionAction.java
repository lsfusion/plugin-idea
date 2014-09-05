package com.lsfusion.actions;

import com.intellij.openapi.actionSystem.Presentation;
import com.lsfusion.LSFBundle;
import com.lsfusion.LSFIcons;

public class FastInsertCompositionAction extends InsertCompositionAction {
    protected static final String REFACTORING_NAME = LSFBundle.message("fast.insert.composition.title");
    
    @Override
    protected void initPresentation() {
        Presentation presentation = getTemplatePresentation();
        presentation.setText(REFACTORING_NAME);
        presentation.setDescription("Inserts composition for the lowest expression in the caret position");
        presentation.setIcon(LSFIcons.FILE);
    }

    @Override
    protected boolean isFastAction() {
        return true;
    }
}
