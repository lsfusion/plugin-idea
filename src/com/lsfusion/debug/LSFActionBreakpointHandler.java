package com.lsfusion.debug;

import com.intellij.debugger.engine.DebugProcessImpl;

public class LSFActionBreakpointHandler extends LSFBreakpointHandler {
    public LSFActionBreakpointHandler(DebugProcessImpl process, LSFDebugVMNotifier vmNotifier) {
        super(LSFBreakpointType.class, process, vmNotifier);
    }
}
