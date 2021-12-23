package com.lsfusion.debug;

import com.intellij.debugger.engine.DebugProcessImpl;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceHelper;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.lsfusion.lang.psi.LSFFile;
import lsfusion.server.physics.dev.debug.DebuggerService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LSFDebugVMNotifier {
    private final DebugProcessImpl myProcess;
    private List<Runnable> remoteEvents = new ArrayList<>();
    private DebuggerService debuggerService;
    private Integer debuggerPort;

    public LSFDebugVMNotifier(DebugProcessImpl myProcess) {
        this.myProcess = myProcess;
        debuggerPort = myProcess.getUserData(LSFDebuggerRunner.DEBUGGER_PROPERTY_KEY);
    }

    private void reattachToService() {
        final Timer timerTask = new Timer();
        timerTask.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    Registry registry = LocateRegistry.getRegistry("localhost", debuggerPort);
                    debuggerService = (DebuggerService) registry.lookup("lsfDebuggerService");
                    if (debuggerService != null) {
                        executePendingMethods();
                        timerTask.cancel();
                    }
                } catch (Throwable ignored) {
                }
            }
        }, 0, 1000);
    }

    private void executePendingMethods() {
        for (Runnable remoteEvent : remoteEvents) {
            remoteEvent.run();
        }
    }

    private String getModuleName(XBreakpoint breakpoint) {
        XSourcePosition position = breakpoint.getSourcePosition();
        if (position != null) {
            PsiFileSystemItem systemItem = FileReferenceHelper.getPsiFileSystemItem(PsiManager.getInstance(myProcess.getProject()), position.getFile());
            if (systemItem instanceof LSFFile) {
                return ((LSFFile) systemItem).getModuleDeclaration().getNameIdentifier().getName();
            }
        }
        return null;
    }

    public void executeMethod(boolean register, XBreakpoint breakpoint) {
        XSourcePosition position = breakpoint.getSourcePosition();
        String module = getModuleName(breakpoint);
        if (position == null || module == null) {
            return;
        }
        Integer line = position.getLine();

        if (myProcess.isDetached()) {
            scheduleRemoteInvocation(register, module, line);
        } else if (debuggerService == null) {
            scheduleRemoteInvocation(register, module, line);
            reattachToService();
        } else {
            invokeRemoteMethod(register, module, line);
        }
    }

    private void scheduleRemoteInvocation(final boolean register, final String module, final Integer line) {
        remoteEvents.add(new Runnable() {
            @Override
            public void run() {
                invokeRemoteMethod(register, module, line);
            }
        });
    }

    public void invokeRemoteMethod(final boolean register, final String module, final Integer line) {
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (register) {
                        debuggerService.registerBreakpoint(module, line);
                    } else {
                        debuggerService.unregisterBreakpoint(module, line);
                    }
                } catch (Throwable ignored) {
                }
            }
        });
    }
}
