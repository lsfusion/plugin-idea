package com.lsfusion.debug;

import com.google.common.base.Throwables;
import com.intellij.debugger.DebuggerBundle;
import com.intellij.debugger.DebuggerManagerEx;
import com.intellij.debugger.engine.*;
import com.intellij.debugger.engine.evaluation.EvaluateException;
import com.intellij.debugger.engine.evaluation.EvaluationContextImpl;
import com.intellij.debugger.engine.evaluation.expression.EvaluatorBuilderImpl;
import com.intellij.debugger.engine.evaluation.expression.ExpressionEvaluator;
import com.intellij.debugger.engine.events.SuspendContextCommandImpl;
import com.intellij.debugger.engine.jdi.ThreadReferenceProxy;
import com.intellij.debugger.engine.requests.RequestManagerImpl;
import com.intellij.debugger.impl.DebuggerContextImpl;
import com.intellij.debugger.impl.DebuggerSession;
import com.intellij.debugger.impl.DebuggerStateManager;
import com.intellij.debugger.jdi.StackFrameProxyImpl;
import com.intellij.debugger.jdi.ThreadReferenceProxyImpl;
import com.intellij.debugger.jdi.VirtualMachineProxyImpl;
import com.intellij.debugger.ui.breakpoints.BreakpointManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.JavaCodeFragment;
import com.intellij.psi.JavaCodeFragmentFactory;
import com.intellij.util.EventDispatcher;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.breakpoints.XBreakpointHandler;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider;
import com.intellij.xdebugger.frame.XSuspendContext;
import com.lsfusion.debug.classchange.LSFClassChangeBreakpointHandler;
import com.lsfusion.debug.property.LSFPropertyBreakpointHandler;
import com.lsfusion.util.ReflectionUtils;
import com.sun.jdi.*;
import com.sun.jdi.event.LocatableEvent;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.StepRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.java.debugger.JavaDebuggerEditorsProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.lsfusion.debug.DebugUtils.*;
import static com.lsfusion.references.LSFToJavaLanguageInjector.INTERNAL_ACTION_FQN;
import static com.lsfusion.util.ReflectionUtils.getPrivateFieldValueByClass;
import static com.sun.jdi.request.StepRequest.STEP_INTO;
import static com.sun.jdi.request.StepRequest.STEP_OUT;


/**
 Stepping algorithm
 
 StepOut:
 1. We're in ScriptingActionProperty+.executeCustom() or LSF
   1.1. stepping out to LSF
   2.1. stepping out with BP in commonExecuteDelegate
     2.1.1. When BP hit - step in to LSF
 2. Else the java stepOut

 StepOver:
 1. We're in LSF or last statement of ScriptingActionProperty+.executeCustom()
   1.1. stepping out 1 time
   1.2. stepping out with BP in commonExecuteDelegate
     1.2.1. When BP hit - step in to LSF
 2. Else the java stepOver

 StepInto:
 1. We're in custom action's delegate
   1.1. Stepping out with BP in commonExecuteCustomDelegate
     1.1.1. When BP hit - single 'step into' in ScriptingActionProperty+.executeCustom()
 2. We're in LSF or last statement of ScriptingActionProperty+.executeCustom()
   2.1. stepping out with BP in commonExecuteDelegate
     2.1.1. When BP hit - step in to LSF or ScriptingActionProperty+.executeCustom()
 2. Else the java stepInto
 */
public class LSFDebugProcess extends JavaDebugProcess {

    // todo: remake into single field after discontinuation of Idea 2023.2 support if method doesn't change again
    private java.lang.reflect.Method doStepMethod;
    private java.lang.reflect.Method doStepMethod_2023_3;

    private final JavaDebuggerEditorsProvider eEditorsProvider;

    private final DebuggerContextImpl SESSION_EMPTY_CONTEXT;
    
    private final List<StepIntoAfterHitBreakpoint> commonDelegateBreakpoints = new ArrayList<>();
    
    private ExpressionEvaluator registerSteppingEvaluator;
    private ExpressionEvaluator unregisterSteppingEvaluator;
    
    private LSFBreakpointHandler breakpointHandler;
    private LSFPropertyBreakpointHandler propertyBreakpointHandler;
    private LSFClassChangeBreakpointHandler classChangeBreakpointHandler;

    private void enableCommonDelegateBreakPoints(SuspendContextImpl suspendContext) {
        invokeRemoteMethod(true, suspendContext);
        for(StepIntoAfterHitBreakpoint commonDelegate : commonDelegateBreakpoints) {
            commonDelegate.enable();
        }
    }

    private void disableCommonDelegateBreakPoints(final SuspendContextImpl suspendContext, boolean scheduleRemoteMethod) {
        for(StepIntoAfterHitBreakpoint commonDelegate : commonDelegateBreakpoints) {
            commonDelegate.disable();
        }
        if (scheduleRemoteMethod) {
            getJavaDebugProcess().getManagerThread().schedule(new SuspendContextCommandImpl(suspendContext) {
                @Override
                public void contextAction(@NotNull SuspendContextImpl suspendContext) {
                    invokeRemoteMethod(false, suspendContext);
                }
            });
        } else {
            invokeRemoteMethod(false, suspendContext);   
        }
    }     

    private void invokeRemoteMethod(boolean register, SuspendContextImpl context, Object... args) {
        try {
            ThreadReferenceProxyImpl thread = context.getThread();
            final StackFrameProxyImpl frameProxy = thread != null ? thread.frame(0) : null;
            if (frameProxy == null) {
                // might be if the thread has been collected
                return;
            }
    
            final EvaluationContextImpl evaluationContext = new EvaluationContextImpl(context, frameProxy, (Value) null);
    
            // "You can invoke methods only inside commands invoked for SuspendContext" fix (2023.3)
            evaluationContext.getManagerThread().invoke(new SuspendContextCommandImpl(context) {
                @Override
                public void contextAction(@NotNull SuspendContextImpl suspendContext) throws Exception {
                    if (register) {
                        registerSteppingEvaluator.evaluate(evaluationContext);
                    } else {
                        unregisterSteppingEvaluator.evaluate(evaluationContext);
                    }
                }
            });
        } catch (Exception e) {
            Throwables.throwIfUnchecked(e);
            throw new RuntimeException(e);
        }
    }

    private boolean disableIfSmallStackCommonDelegateBreakPoints(SuspendContextImpl context) {
        try {
            //при высоте стэка меньше ~7 перестают генериться ивенты для STEP_OUT
            //и JVM просто резумается, поэтому удаляем BP чуть раньше
            if (context.getThread().frameCount() < 10) {
                disableCommonDelegateBreakPoints(context, false);
                return true;
            }
        } catch (EvaluateException ignore) {
        }
        return false;
    }

    public LSFDebugProcess(@NotNull XDebugSession session, DebuggerSession javaSession) {
        super(session, javaSession);

        LSFDebugVMNotifier vmNotifier = new LSFDebugVMNotifier(getJavaDebugProcess());
        DebugUtils.debugProcess = getJavaDebugProcess();
        breakpointHandler = new LSFActionBreakpointHandler(getJavaDebugProcess(), vmNotifier);
        propertyBreakpointHandler = new LSFPropertyBreakpointHandler(getJavaDebugProcess(), vmNotifier);
        classChangeBreakpointHandler = new LSFClassChangeBreakpointHandler(getJavaDebugProcess(), vmNotifier);
        
        commonDelegateBreakpoints.add(new CommonDelegateBreakpoint());
        commonDelegateBreakpoints.add(new CommonCustomDelegateBreakpoint());
        
        eEditorsProvider = new LSFDebuggerEditorsProvider();
        SESSION_EMPTY_CONTEXT = (DebuggerContextImpl) getPrivateFieldValueByClass(DebuggerSession.class, getDebuggerSession(), DebuggerContextImpl.class); // SESSION_EMPTY_CONTEXT field

        try {
            doStepMethod = ReflectionUtils.getPrivateMethodWithException(
                DebugProcessImpl.class,
                "doStep",
                SuspendContextImpl.class, ThreadReferenceProxyImpl.class, Integer.TYPE, Integer.TYPE, RequestHint.class
            );
        } catch (Exception e) {
            doStepMethod_2023_3 = ReflectionUtils.getPrivateMethod(
                    DebugProcessImpl.class,
                    "doStep",
                    SuspendContextImpl.class, ThreadReferenceProxyImpl.class, Integer.TYPE, Integer.TYPE, RequestHint.class, Object.class
            );
        }

        getJavaDebugProcess().addDebugProcessListener(new DebugProcessListener() {
            @Override
            public void paused(SuspendContext suspendContext) {
                ThreadReferenceProxy thread = suspendContext.getThread();
                deleteStepRequests(thread == null ? null : thread.getThreadReference());
                disableCommonDelegateBreakPoints((SuspendContextImpl) suspendContext, true);
            }
        });

        // "Slow operations are prohibited on EDT" fix (2023.3)
        ProgressManager.getInstance().run(new Task.Backgroundable(getProject(), "Stepping evaluators initialization") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                ApplicationManager.getApplication().runReadAction(() -> {
                    try {
                        JavaCodeFragmentFactory codeFragmentFactory = JavaCodeFragmentFactory.getInstance(getProject());

                        String evalString = ACTION_DEBUGGER_FQN + ".getInstance().registerStepping()";
                        final JavaCodeFragment codeFragment = codeFragmentFactory.createCodeBlockCodeFragment(evalString, null, true);
                        registerSteppingEvaluator = EvaluatorBuilderImpl.getInstance().build(codeFragment, null);

                        String unEvalString = ACTION_DEBUGGER_FQN + ".getInstance().unregisterStepping()";
                        final JavaCodeFragment unregisterCodeFragment = codeFragmentFactory.createCodeBlockCodeFragment(unEvalString, null, true);
                        unregisterSteppingEvaluator = EvaluatorBuilderImpl.getInstance().build(unregisterCodeFragment, null);
                    } catch (Exception e) {
                        Throwables.throwIfUnchecked(e);
                        throw new RuntimeException(e);
                    }
                });
            }
        });
        
        javaSession.getProcess().setXDebugProcess(this);
    }

    @NotNull
    @Override
    public XDebuggerEditorsProvider getEditorsProvider() {
        return eEditorsProvider;
    }

    public Project getProject() {
        return getSession().getProject();
    }

    public DebuggerStateManager getContextManager() {
        return getDebuggerSession().getContextManager();
    }

    public DebuggerContextImpl getDebuggerContext() {
        return getContextManager().getContext();
    }

    private SuspendContextImpl getSuspendContext() {
        ApplicationManager.getApplication().assertIsDispatchThread();
        return getDebuggerContext().getSuspendContext();
    }

    public DebugProcessImpl getJavaDebugProcess() {
        return getDebuggerSession().getProcess();
    }

    public SuspendManager getSuspendManager() {
        return getJavaDebugProcess().getSuspendManager();
    }

    public RequestManagerImpl getRequestsManager() {
        return getJavaDebugProcess().getRequestsManager();
    }

    public VirtualMachineProxyImpl getVirtualMachineProxy() {
        return getJavaDebugProcess().getVirtualMachineProxy();
    }

    private BreakpointManager getBreakpointManager() {
        return DebuggerManagerEx.getInstanceEx(getProject()).getBreakpointManager();
    }

    private AtomicReference<ThreadReferenceProxyImpl> getSteppingThroughThread() {
        //приходится делать так жёстко, чтобы встроится во внутреннюю логику java-debuggera
        return (AtomicReference<ThreadReferenceProxyImpl>) getPrivateFieldValueByClass(DebuggerSession.class, getDebuggerSession(), AtomicReference.class); // mySteppingThroughThread field
    }

    private EventDispatcher<DebugProcessListener> getDebugProcessDispatcher() {
        //приходится делать так жёстко, чтобы встроится во внутреннюю логику java-debuggera
        return (EventDispatcher<DebugProcessListener>) ReflectionUtils.getPrivateFieldValue(DebugProcessImpl.class, getJavaDebugProcess(), "myDebugProcessDispatcher");
    }

    private void showStatusText(final String text) {
        getJavaDebugProcess().showStatusText(text);
    }

    // com.intellij.debugger.impl.DebuggerSession.resumeAction()
    private void resumeAction(final StepCommand command, DebuggerSession.Event event) {
        getContextManager().setState(SESSION_EMPTY_CONTEXT, DebuggerSession.State.WAIT_EVALUATION, event, null);
        getJavaDebugProcess().getManagerThread().schedule(command);
    }

    @Override
    public void startForceStepInto(@Nullable XSuspendContext context) {
        startStepInto(context);
    }

    @Override
    public void startStepInto(@Nullable XSuspendContext context) {
        StepCommand stepIntoCommand = null;

        if (isCurrentPositionInLsf() || inExecuteCustom(true)) {
            stepIntoCommand = new StepIntoLsfCommand(getSuspendContext());
        }
        
        if (stepIntoCommand != null) {
            getSteppingThroughThread().set(stepIntoCommand.getContextThread());
            resumeAction(stepIntoCommand, DebuggerSession.Event.STEP);
        } else {
            super.startStepInto(context);
        }
    }

    @Override
    public void startStepOver(@Nullable XSuspendContext context) {
        if (isCurrentPositionInLsf() || inExecuteCustom(true)) {
            StepOverCommand stepOverCommand = new StepOverCommand(getSuspendContext());
            getSteppingThroughThread().set(stepOverCommand.getContextThread());
            resumeAction(stepOverCommand, DebuggerSession.Event.STEP);
        } else {
            super.startStepOver(context);
        }
    }

    @Override
    public void startStepOut(@Nullable XSuspendContext context) {
        if (isCurrentPositionInLsf() || inExecuteCustom()) {
            StepOutCommand stepOutCommand = new StepOutCommand(getSuspendContext());
            getSteppingThroughThread().set(stepOutCommand.getContextThread());
            resumeAction(stepOutCommand, DebuggerSession.Event.STEP);
        } else {
            super.startStepOut(context);
        }
    }

    private boolean inExecuteCustom() {
        return inExecuteCustom(false);
    }

    private boolean inExecuteCustom(final boolean shouldBeLastStatement) {
        ApplicationManager.getApplication().assertIsDispatchThread();

        final Ref<Boolean> inExecute = new Ref<>();
        getJavaDebugProcess().getManagerThread().invokeAndWait(new SuspendContextCommandImpl(getSuspendContext()) {
            @Override
            public void contextAction(@NotNull SuspendContextImpl suspendContext) {
                inExecute.set(inExecuteCustom(getSuspendContext(), shouldBeLastStatement));
            }
        });
        return inExecute.get();
    }
    
    private boolean inExecuteCustom(SuspendContextImpl suspendContext, boolean shouldBeLastStatement) {
        DebuggerManagerThreadImpl.assertIsManagerThread();
        
        if (suspendContext == null) {
            return false;
        }
        
        StackFrameProxyImpl frameProxy = suspendContext.getFrameProxy();
        if (frameProxy == null) {
            return false;
        }

        Location location;
        try {
            location = frameProxy.location();
        } catch (EvaluateException e) {
            return false;
        }
        
        if (location == null) {
            return false;
        }

        Method method = location.method();
        
        String methodName = method.name();
        if (!EXECUTE_CUSTOM_METHOD_NAME.equals(methodName)) {
            return false;
        }
        
        ReferenceType type = location.declaringType();
        if (!(type instanceof ClassType)) {
            return false;
        }
        
        ClassType clazz = (ClassType) type;
        
        boolean isScriptingActionProperty = false;
        while (clazz != null) {
            String name = clazz.name();
            if (INTERNAL_ACTION_FQN.equals(name)) {
                isScriptingActionProperty = true;
                break;
            }
            
            clazz = clazz.superclass();
        }
        
        if (!isScriptingActionProperty) {
            return false;
        }
        
        if (!shouldBeLastStatement) {
            return true;
        }

        List<Location> allLocations;
        try {
            allLocations = method.allLineLocations();
        } catch (AbsentInformationException e) {
            return false;
        }

        return !allLocations.isEmpty() && location.equals(allLocations.get(allLocations.size() - 1));
    }

    private boolean isCurrentPositionInLsf() {
        ApplicationManager.getApplication().assertIsDispatchThread();

        final Ref<Boolean> inLSF = new Ref<>();
        getJavaDebugProcess().getManagerThread().invokeAndWait(new SuspendContextCommandImpl(getSuspendContext()) {
            @Override
            public void contextAction(@NotNull SuspendContextImpl suspendContext) {
                inLSF.set(isTopFrameInLsf(getSuspendContext()));
            }
        });
        return inLSF.get();
    }

    private boolean isTopFrameInLsf(SuspendContextImpl context) {
        ThreadReferenceProxyImpl threadProxy = context.getThread();

        Location location;
        try {
            StackFrameProxyImpl topFrame = threadProxy.frame(0);
            if (topFrame == null) {
                return false;
            }
            location = topFrame.location();
        } catch (EvaluateException ignore) {
            return false;
        }
        if (location == null) {
            return false;
        }
        
        ReferenceType refType = location.declaringType();
        return refType.name().startsWith(DELEGATES_HOLDER_CLASS_FQN_PREFIX) && location.method().name().contains("action");
    }

    private void deleteStepRequests(@Nullable final ThreadReference stepThread) {
        EventRequestManager requestManager = getVirtualMachineProxy().eventRequestManager();
        List<StepRequest> stepRequests = requestManager.stepRequests();
        if (!stepRequests.isEmpty()) {
            final List<StepRequest> toDelete = new ArrayList<>(stepRequests.size());
            for (final StepRequest request : stepRequests) {
                ThreadReference threadReference = request.thread();
                try {
                    if (threadReference.status() != ThreadReference.THREAD_STATUS_UNKNOWN && (stepThread == null || stepThread.equals(threadReference))) {
                        toDelete.add(request);
                    }
                }
                catch (IllegalThreadStateException ignore) {
                }
            }
            requestManager.deleteEventRequests(toDelete);
        }
    }

    private void doStep(final SuspendContextImpl suspendContext, final ThreadReferenceProxyImpl stepThread, int depth, RequestHint hint) {
        if (doStepMethod != null) {
            ReflectionUtils.invokeMethod(doStepMethod, getJavaDebugProcess(), suspendContext, stepThread, -2, depth, hint);
        } else {
            // todo: token parameter is expected to be SteppingStatistic object (2023.3) 
            ReflectionUtils.invokeMethod(doStepMethod_2023_3, getJavaDebugProcess(), suspendContext, stepThread, -2, depth, hint, new Object());
        }
    }
    
    @NotNull
    @Override
    public XBreakpointHandler<?>[] getBreakpointHandlers() {
        XBreakpointHandler<?>[] breakpointHandlers = super.getBreakpointHandlers();
        int handlersCount = breakpointHandlers.length;
        XBreakpointHandler<?>[] result = Arrays.copyOf(breakpointHandlers, handlersCount + 3);
        result[handlersCount] = breakpointHandler;
        result[handlersCount + 1] = propertyBreakpointHandler;
        result[handlersCount + 2] = classChangeBreakpointHandler;
        return result;
    }

    public abstract class StepCommand extends SuspendContextCommandImpl {

        private final ThreadReferenceProxyImpl myContextThread;

        public StepCommand(SuspendContextImpl suspendContext) {
            super(suspendContext);
            final ThreadReferenceProxyImpl contextThread = getDebuggerContext().getThreadProxy();
            myContextThread = contextThread != null ? contextThread : (suspendContext != null ? suspendContext.getThread() : null);
        }

        @Override
        public Priority getPriority() {
            return Priority.HIGH;
        }

        public int getInitialStepping() {
            return STEP_OUT;
        }
        
        @Override
        public void contextAction(@NotNull SuspendContextImpl suspendContext) {
            showStatusText(DebuggerBundle.message("status.process.resumed"));
            
            SuspendContextImpl suspendContextImpl = getSuspendContext();
            ThreadReferenceProxyImpl thread = getContextThread();

            RequestHint hint = createRequestHint(thread, suspendContext);
            hint.setIgnoreFilters(getDebuggerSession().shouldIgnoreSteppingFilters());

            applyThreadFilter(thread);

            doStep(suspendContextImpl, thread, getInitialStepping(), hint);

            getSuspendManager().resume(suspendContextImpl);
            getDebugProcessDispatcher().getMulticaster().resumed(suspendContextImpl);
        }

        public abstract RequestHint createRequestHint(ThreadReferenceProxyImpl thread, SuspendContextImpl suspendContext);

        public ThreadReferenceProxyImpl getContextThread() {
            return myContextThread;
        }

        protected void applyThreadFilter(ThreadReferenceProxy thread) {
            SuspendContextImpl suspendContext = getSuspendContext();
            if (suspendContext != null && suspendContext.getSuspendPolicy() == EventRequest.SUSPEND_ALL) {
                // there could be explicit resume as a result of call to voteSuspend()
                // e.g. when breakpoint was considered invalid, in that case the filter will be applied _after_
                // resuming and all breakpoints in other threads will be ignored.
                // As resume() implicitly cleares the filter, the filter must be always applied _before_ any resume() action happens
                final BreakpointManager breakpointManager = getBreakpointManager();
                breakpointManager.applyThreadFilter(getJavaDebugProcess(), thread.getThreadReference());
            }
        }
    }

    private class StepOutCommand extends StepCommand {
        public StepOutCommand(SuspendContextImpl suspendContext) {
            super(suspendContext);
        }

        @Override
        public RequestHint createRequestHint(ThreadReferenceProxyImpl thread, SuspendContextImpl suspendContext) {
            return new StepOutThanResumeHint(thread, suspendContext, true);
        }
    }

    private class StepOverCommand extends StepCommand {
        public StepOverCommand(SuspendContextImpl suspendContext) {
            super(suspendContext);
        }

        @Override
        public RequestHint createRequestHint(ThreadReferenceProxyImpl thread, SuspendContextImpl suspendContext) {
            return new StepOutThanResumeHint(thread, suspendContext, false);
        }
    }
    
    private class StepIntoLsfCommand extends StepCommand {
        public StepIntoLsfCommand(SuspendContextImpl suspendContext) {
            super(suspendContext);
        }
        
        @Override
        public RequestHint createRequestHint(ThreadReferenceProxyImpl thread, SuspendContextImpl suspendContext) {
            return new AllwaysStepOutHint(thread, suspendContext);
        }

        @Override
        public void contextAction(@NotNull SuspendContextImpl suspendContext) {
            enableCommonDelegateBreakPoints(getSuspendContext());
            super.contextAction(suspendContext);
        }
    }

    private class AllwaysStepOutHint extends RequestHint {
        public AllwaysStepOutHint(ThreadReferenceProxyImpl stepThread, SuspendContextImpl suspendContext) {
            super(stepThread, suspendContext, STEP_OUT);
        }
        @Override
        public int getNextStepDepth(SuspendContextImpl context) {
            disableIfSmallStackCommonDelegateBreakPoints(context);
            return STEP_OUT;
        }
    }

    private class StepOutThanResumeHint extends RequestHint {
        private final boolean stepOutToLsf;

        public StepOutThanResumeHint(ThreadReferenceProxyImpl stepThread, SuspendContextImpl suspendContext, boolean stepOutToLsf) {
            super(stepThread, suspendContext, 0);
            this.stepOutToLsf = stepOutToLsf;
        }

        @Override
        public int getNextStepDepth(SuspendContextImpl context) {
            boolean setupCommonDelegateBP = true;
            if (stepOutToLsf) {
                if (!isTopFrameInLsf(context)) {
                    setupCommonDelegateBP = false;
                }
            }

            boolean disableBreakpoint = disableIfSmallStackCommonDelegateBreakPoints(context);
            if (setupCommonDelegateBP && !disableBreakpoint) {
                enableCommonDelegateBreakPoints(context);
            }

            return STEP_OUT;
        }
    }

    private class StepIntoLsfHint extends RequestHint {
        public StepIntoLsfHint(ThreadReferenceProxyImpl stepThread, SuspendContextImpl suspendContext) {
            super(stepThread, suspendContext, STEP_INTO);
        }

        @Override
        public int getNextStepDepth(SuspendContextImpl context) {
            if (isTopFrameInLsf(context)) {
                return STOP;
            }
            return STEP_INTO;
        }
    }

    private abstract class StepIntoAfterHitBreakpoint extends FilteredRequestorAdapter {
        private boolean enabled = false;

        @Override
        public boolean processLocatableEvent(SuspendContextCommandImpl action, LocatableEvent event) {
            disable();

            SuspendContextImpl suspendContext = action.getSuspendContext();

            ThreadReferenceProxyImpl thread = suspendContext == null ? null : suspendContext.getThread();

            deleteStepRequests(thread == null ? null : thread.getThreadReference());
            
            doStep(suspendContext, thread, STEP_INTO, getRequestHint(suspendContext, thread));

            return false;
        }

        protected abstract RequestHint getRequestHint(SuspendContextImpl suspendContext, ThreadReferenceProxyImpl thread);

        protected abstract Location getLocation();
        
        public void disable() {
            if (enabled) {
                getRequestsManager().deleteRequest(this);
                enabled = false;
            }
        }

        public void enable() {
            if (!enabled) {
                Location location = getLocation();
                if (location == null) {
                    throw new IllegalStateException("Breakpoint delegate location isn't found");
                }

                final BreakpointRequest request = getRequestsManager().createBreakpointRequest(this, location);
                getRequestsManager().enableRequest(request);
                enabled = true;
            }
        }
    }
    
    private class CommonDelegateBreakpoint extends StepIntoAfterHitBreakpoint {

        @Override
        protected RequestHint getRequestHint(SuspendContextImpl suspendContext, ThreadReferenceProxyImpl thread) {
            return new StepIntoLsfHint(thread, suspendContext);
        }

        @Override
        protected Location getLocation() {
            return firstLocationInMethod(getVirtualMachineProxy(), ACTION_DEBUGGER_FQN, COMMON_DELEGATE_METHOD_NAME);
        }
    }
    
    private class CommonCustomDelegateBreakpoint extends StepIntoAfterHitBreakpoint {

        @Override
        protected RequestHint getRequestHint(SuspendContextImpl suspendContext, ThreadReferenceProxyImpl thread) {
            //just single step into
            return null;
        }

        @Override
        protected Location getLocation() {
            return firstLocationInMethod(getVirtualMachineProxy(), INTERNAL_ACTION_FQN, COMMON_CUSTOM_DELEGATE_METHOD_NAME);
        }
    }
}
