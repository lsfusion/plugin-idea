package com.lsfusion.debug;

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
import com.intellij.xdebugger.breakpoints.XBreakpointProperties;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import com.intellij.xdebugger.breakpoints.XLineBreakpointTypeBase;
import com.intellij.xdebugger.breakpoints.ui.XBreakpointGroupingRule;
import com.lsfusion.lang.LSFFileType;
import com.lsfusion.lang.psi.LSFActionPropertyDefinitionBody;
import com.lsfusion.lang.psi.LSFListActionPropertyDefinitionBody;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LSFBreakpointType extends XLineBreakpointTypeBase {
    
    private static final String ID = "lsf-line";
    
    private static final String NAME = "lsFusion Line Breakpoint";

    private static Condition<PsiElement> inListAction = new Condition<PsiElement>() {
        @Override
        public boolean value(PsiElement psiElement) {
            return !(psiElement instanceof LSFListActionPropertyDefinitionBody)
                   && psiElement.getParent() instanceof LSFActionPropertyDefinitionBody;
        }
    };


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
                    if (psiElement instanceof PsiWhiteSpace) {
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
}
