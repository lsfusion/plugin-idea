package com.simpleplugin;

import com.intellij.codeInsight.TargetElementUtilBase;
import com.intellij.codeInsight.navigation.ImplementationSearcher;
import com.intellij.ide.impl.StructureViewWrapperImpl;
import com.intellij.ide.structureView.StructureViewFactoryEx;
import com.intellij.ide.structureView.StructureViewWrapper;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.CaretEvent;
import com.intellij.openapi.editor.event.CaretListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.simpleplugin.psi.LSFClassDecl;
import com.simpleplugin.psi.LSFId;

public class LSFFileCaretListener implements CaretListener {
    public final static Key<LSFFileCaretListener> PROJECT_COMPONENT_KEY = Key.create("lsfusion.structureview.caret.listener");

    private final Project project;

    private PsiElement currentClassElement;

    public LSFFileCaretListener(Project project) {
        this.project = project;
    }

    public void install(Editor editor) {
        if (editor != null) {
            editor.getCaretModel().removeCaretListener(this);
            editor.getCaretModel().addCaretListener(this);
        }
    }

    @Override
    public void caretPositionChanged(CaretEvent e) {
        PsiElement targetElement = TargetElementUtilBase.findTargetElement(e.getEditor(), ImplementationSearcher.getFlags());

        if (targetElement instanceof LSFId) {
            PsiElement parent = targetElement;
            while (parent != null) {
                if (parent instanceof LSFClassDecl) {
                    break;
                }
                parent = parent.getParent();
            }

            if (parent != null && parent != currentClassElement) {
                currentClassElement = parent;
                StructureViewWrapper structureViewWrapper = StructureViewFactoryEx.getInstanceEx(project).getStructureViewWrapper();
                if (structureViewWrapper instanceof StructureViewWrapperImpl) {
                    ((StructureViewWrapperImpl) structureViewWrapper).rebuild();
                }
            }
        }
    }
}
