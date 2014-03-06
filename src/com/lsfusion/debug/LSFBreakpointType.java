package com.lsfusion.debug;

import com.intellij.xdebugger.breakpoints.XLineBreakpointTypeBase;

public class LSFBreakpointType extends XLineBreakpointTypeBase {
    public static final String ID = "lsf-line";
    private static final String NAME = "lsFusion Line Breakpoint";

    public LSFBreakpointType() {
        super(ID, NAME, new LSFDebuggerEditorsProvider());
    }
    
    
    
}
