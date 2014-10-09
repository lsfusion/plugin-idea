package com.lsfusion.structure;

import com.intellij.codeInsight.TargetElementUtilBase;
import com.intellij.codeInsight.navigation.ImplementationSearcher;
import com.intellij.ide.impl.StructureViewWrapperImpl;
import com.intellij.ide.structureView.StructureViewFactoryEx;
import com.intellij.ide.structureView.StructureViewWrapper;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.CaretAdapter;
import com.intellij.openapi.editor.event.CaretEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.lang.psi.LSFBuiltInClassName;
import com.lsfusion.lang.psi.LSFClassDecl;
import com.lsfusion.lang.psi.LSFId;

public class LSFStructureFileCaretListener extends CaretAdapter {
    public final static Key<LSFStructureFileCaretListener> PROJECT_COMPONENT_KEY = Key.create("lsfusion.structureview.caret.listener");

    private final Project project;

    private PsiElement currentClassElement;

    public LSFStructureFileCaretListener(Project project) {
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
            PsiElement parent = PsiTreeUtil.getParentOfType(targetElement, LSFClassDecl.class, LSFBuiltInClassName.class);

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
