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
import com.intellij.openapi.util.Key;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugProcessStarter;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.impl.XDebugSessionImpl;
import com.lsfusion.module.run.LSFusionRunConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LSFDebuggerRunner extends GenericDebuggerRunner {
    public static final String PLUGIN_ENABLED_PROPERTY = "lsfusion.server.plugin.enabled";
    public static final String DEBUG_ACTIONS_PROPERTY = "lsfusion.server.debug.actions";
    public static final String LIGHT_START_PROPERTY = "lsfusion.server.lightstart";
    public static final Key<Integer> DEBUGGER_PROPERTY_KEY = new Key<>("lsfusion.debuggerPort");

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

        debugProcess.putUserData(DEBUGGER_PROPERTY_KEY, ((LSFusionRunConfiguration) env.getRunProfile()).getDebuggerPort(state));

        return startCompatibleSession(env, new XDebugProcessStarter() {
            @Override
            @NotNull
            public XDebugProcess start(final @NotNull XDebugSession session) {
                XDebugSessionImpl sessionImpl = (XDebugSessionImpl) session;
                ExecutionResult executionResult = debugProcess.getExecutionResult();
                sessionImpl.addExtraActions(executionResult.getActions());
                if (executionResult instanceof DefaultExecutionResult) {
                    sessionImpl.addRestartActions(((DefaultExecutionResult) executionResult).getRestartActions());
                }
                return new LSFDebugProcess(session, debuggerSession);
            }
        });
    }

    //backward compatibility with 2025.2 (newSessionBuilder is available since 2026.1)
    //todo: refactor once the since-build is bumped to 261
    private RunContentDescriptor startCompatibleSession(@NotNull ExecutionEnvironment env,
                                                        @NotNull XDebugProcessStarter processStarter) throws ExecutionException {
        XDebuggerManager debuggerManager = XDebuggerManager.getInstance(env.getProject());
        RunContentDescriptor descriptor = startSessionWithBuilder(debuggerManager, env, processStarter);
        if (descriptor != null) {
            return descriptor;
        }
        return debuggerManager.startSession(env, processStarter).getRunContentDescriptor();
    }

    @Nullable
    private RunContentDescriptor startSessionWithBuilder(@NotNull XDebuggerManager debuggerManager,
                                                         @NotNull ExecutionEnvironment env,
                                                         @NotNull XDebugProcessStarter processStarter) throws ExecutionException {
        try {
            Method newSessionBuilder = XDebuggerManager.class.getMethod("newSessionBuilder", XDebugProcessStarter.class);
            Object sessionBuilder = newSessionBuilder.invoke(debuggerManager, processStarter);
            Class<?> sessionBuilderClass = newSessionBuilder.getReturnType();

            Method environmentMethod = sessionBuilderClass.getMethod("environment", ExecutionEnvironment.class);
            Object configuredBuilder = environmentMethod.invoke(sessionBuilder, env);

            Method startSession = sessionBuilderClass.getMethod("startSession");
            Object sessionStartedResult = startSession.invoke(configuredBuilder);
            if (sessionStartedResult == null) {
                return null;
            }

            Method getRunContentDescriptor = startSession.getReturnType().getMethod("getRunContentDescriptor");
            return (RunContentDescriptor) getRunContentDescriptor.invoke(sessionStartedResult);
        } catch (NoSuchMethodException e) {
            return null;
        } catch (IllegalAccessException e) {
            throw new ExecutionException("Failed to start debugger session", e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getTargetException();
            if (cause instanceof ExecutionException) {
                throw (ExecutionException) cause;
            }
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new ExecutionException("Failed to start debugger session", cause);
        }
    }
}
