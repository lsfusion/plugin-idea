package com.lsfusion.debug;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.impl.XDebuggerSupport;
import com.intellij.xdebugger.impl.actions.DebuggerActionHandler;
import com.intellij.xdebugger.impl.actions.XDebuggerSuspendedActionHandler;
import com.intellij.xdebugger.impl.actions.handlers.XDebuggerActionHandler;
import com.intellij.xdebugger.impl.breakpoints.XBreakpointPanelProvider;
import com.intellij.xdebugger.impl.breakpoints.ui.BreakpointItem;
import com.intellij.xdebugger.impl.breakpoints.ui.BreakpointPanelProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

// the problem is that java debugger works even if session is not suspended, which is not good, when we "override" java debugger
public class LSFDumbDebuggerSupport extends XDebuggerSupport {

    private final DebuggerActionHandler myStepOverHandler;
    private final DebuggerActionHandler myStepIntoHandler;
    private final DebuggerActionHandler myStepOutHandler;
    private final DebuggerActionHandler myForceStepOverHandler;
    private final DebuggerActionHandler myForceStepIntoHandler;

    private final DebuggerActionHandler myRunToCursorHandler;
    private final DebuggerActionHandler myForceRunToCursor;

    private final DebuggerActionHandler mySmartStepIntoHandler;

    private final XBreakpointPanelProvider myBreakpointPanelProvider;

    private static class DebuggerActionHandlerFacade extends XDebuggerActionHandler {
        
        private final XDebuggerSuspendedActionHandler handler;

        private DebuggerActionHandlerFacade(DebuggerActionHandler handler) {
            this.handler = (XDebuggerSuspendedActionHandler) handler;
        }

        private boolean checkSuspended(Project project) {
            XDebugSession session = XDebuggerManager.getInstance(project).getCurrentSession();
            return (session != null && session.isSuspended());
        }
        
        // because perform is protected, need to duplicate
        @Override
        public boolean isEnabled(@NotNull Project project, AnActionEvent event) {
            return checkSuspended(project) || handler.isEnabled(project, event);
        }
        @Override
        public void perform(@NotNull Project project, AnActionEvent event) {
            if(!checkSuspended(project) && handler.isEnabled(project, event)) // need to recheck because could be suspended, and thats why return true
                handler.perform(project, event);
        }

        @Override
        protected boolean isEnabled(@NotNull XDebugSession session, DataContext dataContext) {
            throw new UnsupportedOperationException();
        }
        @Override
        protected void perform(@NotNull XDebugSession session, DataContext dataContext) {
            throw new UnsupportedOperationException();
        }
    }
    
    public LSFDumbDebuggerSupport() {
        myStepOverHandler = new DebuggerActionHandlerFacade(super.getStepOverHandler());
        myStepIntoHandler = new DebuggerActionHandlerFacade(super.getStepIntoHandler());
        myStepOutHandler = new DebuggerActionHandlerFacade(super.getStepOutHandler());
        myForceStepOverHandler = new DebuggerActionHandlerFacade(super.getForceStepOverHandler());
        myForceStepIntoHandler = new DebuggerActionHandlerFacade(super.getForceStepIntoHandler());

        myRunToCursorHandler = new DebuggerActionHandlerFacade(super.getRunToCursorHandler());
        myForceRunToCursor = new DebuggerActionHandlerFacade(super.getForceRunToCursorHandler());

        mySmartStepIntoHandler = new DebuggerActionHandlerFacade(super.getStepIntoHandler());
        
        myBreakpointPanelProvider = new XBreakpointPanelProvider() {
            // panelProvider в XDebuggerSupport уже предоставляет breakpoint'ы всех XBreakpoint типов
            @Override
            public void provideBreakpointItems(Project project, Collection<? super BreakpointItem> items) {
            }
        };
    }

    @NotNull
    @Override
    public DebuggerActionHandler getStepOverHandler() {
        return myStepOverHandler;
    }

    @NotNull
    @Override
    public DebuggerActionHandler getStepIntoHandler() {
        return myStepIntoHandler;
    }

    @NotNull
    @Override
    public DebuggerActionHandler getSmartStepIntoHandler() {
        return mySmartStepIntoHandler;
    }

    @NotNull
    @Override
    public DebuggerActionHandler getStepOutHandler() {
        return myStepOutHandler;
    }

    @NotNull
    @Override
    public DebuggerActionHandler getForceStepOverHandler() {
        return myForceStepOverHandler;
    }

    @NotNull
    @Override
    public DebuggerActionHandler getForceStepIntoHandler() {
        return myForceStepIntoHandler;
    }

    @NotNull
    @Override
    public DebuggerActionHandler getRunToCursorHandler() {
        return myRunToCursorHandler;
    }

    @NotNull
    @Override
    public DebuggerActionHandler getForceRunToCursorHandler() {
        return myForceRunToCursor;
    }

    @NotNull
    @Override
    public BreakpointPanelProvider<?> getBreakpointPanelProvider() {
        return myBreakpointPanelProvider;
    }
}
