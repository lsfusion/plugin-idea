package com.lsfusion.debug.property;

import com.intellij.debugger.DebuggerManagerEx;
import com.intellij.debugger.ui.breakpoints.Breakpoint;
import com.intellij.debugger.ui.breakpoints.LineBreakpoint;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.breakpoints.XBreakpoint;

import javax.swing.*;

public class LSFPropertyBreakpoint extends LineBreakpoint {
    protected LSFPropertyBreakpoint(Project project, XBreakpoint breakpoint) {
        super(project, breakpoint);
    }

    public static LSFPropertyBreakpoint create(Project project, XBreakpoint breakpoint) {
        LSFPropertyBreakpoint propertyBreakpoint = new LSFPropertyBreakpoint(project, breakpoint);
        return (LSFPropertyBreakpoint)propertyBreakpoint.init();
    }

    @Override
    protected Icon getDisabledIcon(boolean isMuted) {
        final Breakpoint master = DebuggerManagerEx.getInstanceEx(myProject).getBreakpointManager().findMasterBreakpoint(this);
        if (isMuted) {
            return master == null? AllIcons.Debugger.Db_muted_disabled_field_breakpoint : AllIcons.Debugger.Db_muted_dep_field_breakpoint;
        }
        else {
            return master == null? AllIcons.Debugger.Db_disabled_field_breakpoint : AllIcons.Debugger.Db_dep_field_breakpoint;
        }
    }

    @Override
    protected Icon getSetIcon(boolean isMuted) {
        return isMuted? AllIcons.Debugger.Db_muted_field_breakpoint : AllIcons.Debugger.Db_field_breakpoint;
    }

    @Override
    protected Icon getInvalidIcon(boolean isMuted) {
        return isMuted? AllIcons.Debugger.Db_muted_invalid_field_breakpoint : AllIcons.Debugger.Db_invalid_field_breakpoint;
    }

    @Override
    protected Icon getVerifiedIcon(boolean isMuted) {
        return isMuted? AllIcons.Debugger.Db_muted_verified_field_breakpoint : AllIcons.Debugger.Db_verified_field_breakpoint;
    }

    @Override
    protected Icon getVerifiedWarningsIcon(boolean isMuted) {
        return isMuted? AllIcons.Debugger.Db_muted_field_warning_breakpoint : AllIcons.Debugger.Db_field_warning_breakpoint;
    }
}
