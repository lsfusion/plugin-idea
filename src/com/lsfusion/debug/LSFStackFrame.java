package com.lsfusion.debug;

import com.intellij.debugger.SourcePosition;
import com.intellij.debugger.engine.DebugProcessImpl;
import com.intellij.debugger.engine.JavaDebuggerEvaluator;
import com.intellij.debugger.engine.JavaStackFrame;
import com.intellij.debugger.engine.SuspendContextImpl;
import com.intellij.debugger.engine.events.DebuggerContextCommandImpl;
import com.intellij.debugger.jdi.StackFrameProxyImpl;
import com.intellij.debugger.ui.impl.watch.MethodsTracker;
import com.intellij.debugger.ui.impl.watch.StackFrameDescriptorImpl;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.ColoredTextContainer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import com.intellij.xdebugger.frame.XStackFrame;
import com.lsfusion.LSFIcons;
import com.lsfusion.lang.psi.LSFPropertyDeclaration;
import com.lsfusion.lang.psi.LSFPropertyStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LSFStackFrame extends XStackFrame {
    private final Project project;
    @NotNull
    private final StackFrameProxyImpl frame;
    @NotNull
    private final DebugProcessImpl debugProcess;
    @NotNull
    private final XSourcePosition position;
    private JavaDebuggerEvaluator evaluator;

    public LSFStackFrame(Project project, @NotNull final StackFrameProxyImpl frame, @NotNull final DebugProcessImpl debugProcess, @NotNull XSourcePosition position) {
        this.project = project;
        this.frame = frame;
        this.debugProcess = debugProcess;
        this.position = position;

        debugProcess.getManagerThread().invoke(new DebuggerContextCommandImpl(debugProcess.getDebuggerContext()) {
            @Override
            public void threadAction(@NotNull SuspendContextImpl suspendContext) {
                StackFrameDescriptorImpl stackFrameDescriptor = new StackFrameDescriptorImpl(frame, new MethodsTracker());
                evaluator = new JavaDebuggerEvaluator(debugProcess, new JavaStackFrame(stackFrameDescriptor, false));
            }
        });
    }

    @Nullable
    @Override
    public XDebuggerEvaluator getEvaluator() {
        return evaluator;
    }

    @Nullable
    @Override
    public Object getEqualityObject() {
        return frame.hashCode();
    }

    @Nullable
    @Override
    public XSourcePosition getSourcePosition() {
        return position;
    }

    @Override
    public void customizePresentation(@NotNull ColoredTextContainer component) {
        String currentActionName = ApplicationManager.getApplication().runReadAction(new Computable<String>() {
            @Override
            public String compute() {
                return getCurrentActionName();
            }
        });
        component.append("LSF: " + currentActionName + "()", SimpleTextAttributes.REGULAR_ATTRIBUTES);
        component.append(":" + (position.getLine() + 1), SimpleTextAttributes.REGULAR_ATTRIBUTES);
        component.append(", " + position.getFile().getName(), SimpleTextAttributes.REGULAR_ATTRIBUTES);
        component.setIcon(LSFIcons.FILE);
    }

    private String getCurrentActionName() {
        PsiFile file = PsiManager.getInstance(project).findFile(position.getFile());
        if (file != null) {
            SourcePosition pos = SourcePosition.createFromLine(file, position.getLine());
            PsiElement currentElement = pos.getElementAt();
            if (currentElement != null) {
                LSFPropertyStatement statement = PsiTreeUtil.getParentOfType(currentElement, LSFPropertyStatement.class);
                if (statement != null) {
                    LSFPropertyDeclaration decl = statement.getPropertyDeclaration();
                    return decl.getSimpleNameWithCaption().getSimpleName().getName();
                }
            }
        }
        return "";
    }
}
