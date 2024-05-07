package com.lsfusion.debug;

import com.intellij.debugger.ui.breakpoints.LineBreakpoint;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.breakpoints.XBreakpoint;

public class LSFLineBreakpoint extends LineBreakpoint {
    protected LSFLineBreakpoint(Project project, XBreakpoint xBreakpoint) {
        super(project, xBreakpoint);
    }
}
