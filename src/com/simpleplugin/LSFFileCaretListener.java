package com.simpleplugin;

import com.intellij.codeInsight.TargetElementUtilBase;
import com.intellij.codeInsight.navigation.ImplementationSearcher;
import com.intellij.ide.impl.StructureViewWrapperImpl;
import com.intellij.ide.structureView.StructureViewFactoryEx;
import com.intellij.openapi.editor.event.CaretEvent;
import com.intellij.openapi.editor.event.CaretListener;
import com.intellij.psi.PsiElement;
import com.simpleplugin.psi.LSFClassDecl;
import com.simpleplugin.psi.LSFFile;
import com.simpleplugin.psi.LSFId;

public class LSFFileCaretListener implements CaretListener {
    private LSFFile file;

    private PsiElement latestClassElement;

    public LSFFileCaretListener() {
    }

    public void setFile(LSFFile file) {
        this.file = file;
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

            if (parent != null && parent != latestClassElement) {
                latestClassElement = parent;
                ((StructureViewWrapperImpl) StructureViewFactoryEx.getInstanceEx(file.getProject()).getStructureViewWrapper()).rebuild();
            }
        }
    }
}
