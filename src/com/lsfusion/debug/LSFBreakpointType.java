package com.lsfusion.debug;

import com.intellij.debugger.ui.breakpoints.Breakpoint;
import com.intellij.debugger.ui.breakpoints.JavaBreakpointType;
import com.intellij.debugger.ui.breakpoints.LineBreakpoint;
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
import com.lsfusion.lang.psi.LSFClassStatement;
import com.lsfusion.lang.psi.LSFPropertyStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.java.debugger.breakpoints.properties.JavaBreakpointProperties;
import org.jetbrains.java.debugger.breakpoints.properties.JavaLineBreakpointProperties;

import java.util.List;

public class LSFBreakpointType extends XLineBreakpointTypeBase implements JavaBreakpointType {
    
    private static final String ID = "lsf-line";
    
    private static final String NAME = "lsFusion Line Breakpoint";

    private static Condition<PsiElement> inListAction = new Condition<PsiElement>() {
        @Override
        public boolean value(PsiElement psiElement) {
            return true;
            // in theory we can pre-check rules that are debuggable, but there are a lot of them, and in that case we need to keep it consistent with DebugInfo, and for example java does not do that, so i don't see any reason why we should 
//            return !(psiElement instanceof LSFListActionPropertyDefinitionBody)
//                   && psiElement.getParent() instanceof LSFActionPropertyDefinitionBody;
        }
    };

    @Override
    public int getPriority() {
        return 200;
    }

    public LSFBreakpointType() {
//        super(ID, NAME, new LSFDebuggerEditorsProvider());
        super(ID, NAME, null);
    }

    @Override
    public List<XBreakpointGroupingRule<XLineBreakpoint<XBreakpointProperties>, ?>> getGroupingRules() {
        return XDebuggerUtil.getInstance().getGroupingByFileRuleAsList();
    }

    @Override
    public boolean canPutAt(@NotNull VirtualFile file, int line, @NotNull Project project) {
        if (file.getFileType() != LSFFileType.INSTANCE) {
            return false;
        }
        
        final Document document = FileDocumentManager.getInstance().getDocument(file);
        if (document != null) {
            final Ref<Boolean> stoppable = Ref.create(false);
            XDebuggerUtil.getInstance().iterateLine(project, document, line, new Processor<PsiElement>() {
                @Override
                public boolean process(PsiElement psiElement) {
                    if (psiElement instanceof PsiWhiteSpace || hasPropertyBreakpoint(psiElement) || hasClassBreakpoint(psiElement)) {
                        return true;
                    }
                    
                    if (PsiTreeUtil.findFirstParent(psiElement, true, inListAction) != null) {
                        stoppable.set(true);
                        return false;
                    }
                    return true;
                }
            });
            
            return stoppable.get();
        }

        return false;
    }

    private boolean hasPropertyBreakpoint(PsiElement psiElement) {
        return PsiTreeUtil.getParentOfType(psiElement, LSFPropertyStatement.class) != null;
    }

    private boolean hasClassBreakpoint(PsiElement psiElement) {
        return PsiTreeUtil.getParentOfType(psiElement, LSFClassStatement.class) != null;
    }

    //createJavaBreakpoint in interface is @NotNull, but this implementation can return null, and it's ok and nothing fails.
    @SuppressWarnings("NullableProblems")
    @Override
    public Breakpoint createJavaBreakpoint(Project project, XBreakpoint breakpoint) {
        return LineBreakpoint.create(project, breakpoint);
    }

    @Nullable
    @Override
    public JavaBreakpointProperties createProperties() {
        return new JavaLineBreakpointProperties();
    }

    @Nullable
    @Override
    public JavaBreakpointProperties createBreakpointProperties(@NotNull VirtualFile file, int line) {
        return new JavaLineBreakpointProperties();
    }
}
