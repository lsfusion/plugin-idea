package com.lsfusion.debug.classchange;

import com.intellij.debugger.ui.breakpoints.LineBreakpoint;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.breakpoints.XBreakpoint;

public class LSFClassChangeBreakpoint extends LineBreakpoint {
    protected LSFClassChangeBreakpoint(Project project, XBreakpoint xBreakpoint) {
        super(project, xBreakpoint);
    }
}
