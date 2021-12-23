package com.lsfusion.debug.classchange;

import com.intellij.debugger.ui.breakpoints.Breakpoint;
import com.intellij.debugger.ui.breakpoints.JavaBreakpointType;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Processor;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointProperties;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import com.intellij.xdebugger.breakpoints.XLineBreakpointTypeBase;
import com.intellij.xdebugger.breakpoints.ui.XBreakpointGroupingRule;
import com.lsfusion.lang.LSFFileType;
import com.lsfusion.lang.psi.declarations.LSFClassDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.java.debugger.breakpoints.properties.JavaLineBreakpointProperties;

import java.util.List;

public class LSFClassChangeBreakpointType extends XLineBreakpointTypeBase implements JavaBreakpointType {
    private static final String ID = "lsf-class-change";

    private static final String NAME = "lsFusion Class Change Breakpoint";

    private static Condition<PsiElement> isInClassDeclaration = new Condition<>() {
        @Override
        public boolean value(PsiElement psiElement) {
            return psiElement instanceof LSFClassDeclaration;
        }
    };
    
    protected LSFClassChangeBreakpointType() {
        super(ID, NAME, null);
    }

    @NotNull
    @Override
    public Breakpoint createJavaBreakpoint(Project project, XBreakpoint breakpoint) {
        return LSFClassChangeBreakpoint.create(project, breakpoint);
    }

    @Override
    public boolean canPutAt(@NotNull VirtualFile file, int line, @NotNull Project project) {
        if (file.getFileType() == LSFFileType.INSTANCE) {
            final Document document = FileDocumentManager.getInstance().getDocument(file);
            if (document != null) {
                final Ref<Boolean> stoppable = Ref.create(false);
                XDebuggerUtil.getInstance().iterateLine(project, document, line, new Processor<>() {
                    @Override
                    public boolean process(PsiElement psiElement) {
                        if (psiElement instanceof PsiWhiteSpace) {
                            return true;
                        }

                        if (PsiTreeUtil.findFirstParent(psiElement, true, isInClassDeclaration) != null) {
                            stoppable.set(true);
                            return false;
                        }
                        return true;
                    }
                });

                return stoppable.get();
            }
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
    public XBreakpointProperties createProperties() {
        return new JavaLineBreakpointProperties();
    }

    @Nullable
    @Override
    public XBreakpointProperties createBreakpointProperties(@NotNull VirtualFile file, int line) {
        return new JavaLineBreakpointProperties();
    }
}
