package com.lsfusion.debug;

import com.intellij.debugger.engine.DebugProcessImpl;
import com.intellij.debugger.engine.JavaBreakpointHandler;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointType;
import org.jetbrains.annotations.NotNull;

public class LSFBreakpointHandler extends JavaBreakpointHandler {
    private final LSFDebugVMNotifier vmNotifier;

    public LSFBreakpointHandler(@NotNull Class<? extends XBreakpointType<?, ?>> breakpointTypeClass, DebugProcessImpl process, LSFDebugVMNotifier vmNotifier) {
        super(breakpointTypeClass, process);
        this.vmNotifier = vmNotifier;
    }
    
    @Override
    public void registerBreakpoint(@NotNull XBreakpoint breakpoint) {
        if (myProcess.getXdebugProcess() instanceof LSFDebugProcess) {
            vmNotifier.executeMethod(true, breakpoint);

            super.registerBreakpoint(breakpoint);
        }
    }

    @Override
    public void unregisterBreakpoint(@NotNull XBreakpoint breakpoint, boolean temporary) {
        if (myProcess.getXdebugProcess() instanceof LSFDebugProcess) {
            vmNotifier.executeMethod(false, breakpoint);

            super.unregisterBreakpoint(breakpoint, temporary);
        }
    }
}
