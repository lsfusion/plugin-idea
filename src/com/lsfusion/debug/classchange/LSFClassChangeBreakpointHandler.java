package com.lsfusion.debug.classchange;

import com.intellij.debugger.engine.DebugProcessImpl;
import com.lsfusion.debug.LSFBreakpointHandler;
import com.lsfusion.debug.LSFDebugVMNotifier;

public class LSFClassChangeBreakpointHandler extends LSFBreakpointHandler {
    public LSFClassChangeBreakpointHandler(DebugProcessImpl process, LSFDebugVMNotifier vmNotifier) {
        super(LSFClassChangeBreakpointType.class, process, vmNotifier);
    }
}
