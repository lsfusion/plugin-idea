package com.lsfusion.debug;

import com.intellij.debugger.jdi.VirtualMachineProxyImpl;
import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.ReferenceType;

import java.util.List;

public class DebugUtils {
    public static final String DELEGATES_HOLDER_CLASS_PACKAGE = "lsfusion.server.physics.dev.debug";
    public static final String DELEGATES_HOLDER_CLASS_NAME_PREFIX = "DebugDelegatesHolder_";
    public static final String DELEGATES_HOLDER_CLASS_FQN_PREFIX = DELEGATES_HOLDER_CLASS_PACKAGE + "." + DELEGATES_HOLDER_CLASS_NAME_PREFIX;
    
    public static final String ACTION_DEBUGGER_FQN = "lsfusion.server.physics.dev.debug.ActionDebugger";
    public static final String COMMON_DELEGATE_METHOD_NAME = "commonExecuteDelegate";
    
    public static final String COMMON_CUSTOM_DELEGATE_METHOD_NAME = "commonExecuteInternalDelegate";
    
    public static final String EXECUTE_CUSTOM_METHOD_NAME = "executeInternal";

    public static Location firstLocationInMethod(VirtualMachineProxyImpl virtualMachineProxy, String classFQN, String methodName) {
        List<ReferenceType> cls = virtualMachineProxy.classesByName(classFQN);
        if (cls.isEmpty()) {
            // в этом месте нужен prepareClassRequest, но будем считать, что в нашем контексте этот класс уже точно загружен
            return null;
        }

        ReferenceType debuggerType = cls.iterator().next();
        Method method = debuggerType.methodsByName(methodName).get(0);

        try {
            return method.allLineLocations().iterator().next();
        } catch (Exception e) {
            return null;
        }
    }
}
