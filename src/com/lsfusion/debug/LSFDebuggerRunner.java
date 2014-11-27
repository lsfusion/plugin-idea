package com.lsfusion.debug;

import com.intellij.debugger.DebugEnvironment;
import com.intellij.debugger.DebuggerManagerEx;
import com.intellij.debugger.DefaultDebugUIEnvironment;
import com.intellij.debugger.engine.DebugProcessImpl;
import com.intellij.debugger.impl.DebuggerSession;
import com.intellij.debugger.impl.GenericDebuggerRunner;
import com.intellij.debugger.ui.tree.render.BatchEvaluator;
import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.configurations.*;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugProcessStarter;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.impl.XDebugSessionImpl;
import com.lsfusion.module.run.LSFusionRunConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LSFDebuggerRunner extends GenericDebuggerRunner {
    public static final String DEBUG_ACTIONS_PROPERTY = "lsfusion.server.debug.actions";
    
    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return executorId.equals(DefaultDebugExecutor.EXECUTOR_ID) && profile instanceof LSFusionRunConfiguration;
    }

    @NotNull
    @Override
    public String getRunnerId() {
        return "LSFDebuggerRunner";
    }

    @Nullable
    @Override
    protected RunContentDescriptor createContentDescriptor(@NotNull RunProfileState state, @NotNull ExecutionEnvironment environment) throws ExecutionException {
        if (state instanceof JavaCommandLine) {
            final JavaParameters params = ((JavaCommandLine) state).getJavaParameters();
            ParametersList vmParametersList = params.getVMParametersList();
            if (!vmParametersList.hasProperty(DEBUG_ACTIONS_PROPERTY)) {
                vmParametersList.addProperty(DEBUG_ACTIONS_PROPERTY, "true");
            }
        }
        return super.createContentDescriptor(state, environment);
    }

    @Nullable
    @Override
    protected RunContentDescriptor attachVirtualMachine(RunProfileState state, @NotNull ExecutionEnvironment env, RemoteConnection connection, boolean pollConnection) throws ExecutionException {
        DefaultDebugUIEnvironment debugEnvironment = new DefaultDebugUIEnvironment(env, state, connection, pollConnection);
        DebugEnvironment environment = debugEnvironment.getEnvironment();
        final DebuggerSession debuggerSession = DebuggerManagerEx.getInstanceEx(env.getProject()).attachVirtualMachine(environment);
        if (debuggerSession == null) {
            return null;
        }

        final DebugProcessImpl debugProcess = debuggerSession.getProcess();
        if (debugProcess.isDetached() || debugProcess.isDetaching()) {
            debuggerSession.dispose();
            return null;
        }
        if (environment.isRemote()) {
            // optimization: that way BatchEvaluator will not try to lookup the class file in remote VM
            // which is an expensive operation when executed first time
            debugProcess.putUserData(BatchEvaluator.REMOTE_SESSION_KEY, Boolean.TRUE);
        }

        XDebugSession debugSession =
            XDebuggerManager.getInstance(env.getProject()).startSession(this, env, env.getContentToReuse(), new XDebugProcessStarter() {
                @Override
                @NotNull
                public XDebugProcess start(final @NotNull XDebugSession session) {
                    XDebugSessionImpl sessionImpl = (XDebugSessionImpl) session;
                    ExecutionResult executionResult = debugProcess.getExecutionResult();
                    sessionImpl.addExtraActions(executionResult.getActions());
                    if (executionResult instanceof DefaultExecutionResult) {
                        sessionImpl.addRestartActions(((DefaultExecutionResult) executionResult).getRestartActions());
                        sessionImpl.addExtraStopActions(((DefaultExecutionResult) executionResult).getAdditionalStopActions());
                    }
                    return new LSFDebugProcess(session, debuggerSession);
                }
            });
        return debugSession.getRunContentDescriptor();
    }
}
