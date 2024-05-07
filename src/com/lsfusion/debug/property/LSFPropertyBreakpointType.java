package com.lsfusion.debug.property;

import com.intellij.debugger.ui.breakpoints.Breakpoint;
import com.intellij.debugger.ui.breakpoints.JavaBreakpointType;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointProperties;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import com.intellij.xdebugger.breakpoints.XLineBreakpointTypeBase;
import com.intellij.xdebugger.breakpoints.ui.XBreakpointGroupingRule;
import com.lsfusion.lang.LSFFileType;
import com.lsfusion.lang.psi.LSFDataPropertyDefinition;
import com.lsfusion.lang.psi.LSFLocalDataPropertyDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.java.debugger.breakpoints.properties.JavaLineBreakpointProperties;

import javax.swing.*;
import java.util.List;

public class LSFPropertyBreakpointType  extends XLineBreakpointTypeBase implements JavaBreakpointType {
    private static final String ID = "lsf-property";

    private static final String NAME = "lsFusion Property Breakpoint";

    private static Condition<PsiElement> isInPropDeclaration = psiElement -> psiElement instanceof LSFLocalDataPropertyDefinition || psiElement instanceof LSFDataPropertyDefinition;
    
    protected LSFPropertyBreakpointType() {
        super(ID, NAME, null);
    }

    @Override
    public Icon getTemporaryIcon() {
        return super.getTemporaryIcon();
    }

    @NotNull
    @Override
    public Icon getInactiveDependentIcon() {
        return super.getInactiveDependentIcon();
    }

    @NotNull
    @Override
    public Icon getEnabledIcon() {
        return AllIcons.Debugger.Db_field_breakpoint;
    }

    @NotNull
    @Override
    public Icon getDisabledIcon() {
        return AllIcons.Debugger.Db_disabled_field_breakpoint;
    }

    @NotNull
    @Override
    public Icon getMutedEnabledIcon() {
        return AllIcons.Debugger.Db_muted_field_breakpoint;
    }

    @NotNull
    @Override
    public Icon getMutedDisabledIcon() {
        return AllIcons.Debugger.Db_muted_disabled_field_breakpoint;
    }

    @NotNull
    @Override
    public Breakpoint createJavaBreakpoint(Project project, XBreakpoint breakpoint) {
        return new LSFPropertyBreakpoint(project, breakpoint);
    }

    @Override
    public boolean canPutAt(@NotNull VirtualFile file, int line, @NotNull Project project) {
        if (file.getFileType() != LSFFileType.INSTANCE) {
            return false;
        }

        final Document document = FileDocumentManager.getInstance().getDocument(file);
        if (document != null) {
            final Ref<Boolean> stoppable = Ref.create(false);
            XDebuggerUtil.getInstance().iterateLine(project, document, line, psiElement -> {
                if (psiElement instanceof PsiWhiteSpace) {
                    return true;
                }

                if (PsiTreeUtil.findFirstParent(psiElement, true, isInPropDeclaration) != null) {
                    stoppable.set(true);
                    return false;
                }
                return true;
            });

            return stoppable.get();
        }

        return false;
    }

    @Override
    public int getPriority() {
        return 200;
    }

    @Override
    public List<XBreakpointGroupingRule<XLineBreakpoint<XBreakpointProperties>, ?>> getGroupingRules() {
        return XDebuggerUtil.getInstance().getGroupingByFileRuleAsList();
    }
    
    @Nullable
    @Override
    public XBreakpointProperties<?> createProperties() {
        return new JavaLineBreakpointProperties();
    }

    @Nullable
    @Override
    public XBreakpointProperties<?> createBreakpointProperties(@NotNull VirtualFile file, int line) {
        return new JavaLineBreakpointProperties();
    }
}
