package com.lsfusion.debug.property;

import com.intellij.debugger.engine.DebugProcessImpl;
import com.lsfusion.debug.LSFBreakpointHandler;
import com.lsfusion.debug.LSFDebugVMNotifier;

public class LSFPropertyBreakpointHandler extends LSFBreakpointHandler {
    public LSFPropertyBreakpointHandler(DebugProcessImpl process, LSFDebugVMNotifier vmNotifier) {
        super(LSFPropertyBreakpointType.class, process, vmNotifier);
    }
}
