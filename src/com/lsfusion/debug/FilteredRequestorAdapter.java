package com.lsfusion.debug;

import com.intellij.debugger.InstanceFilter;
import com.intellij.debugger.engine.events.SuspendContextCommandImpl;
import com.intellij.debugger.ui.breakpoints.FilteredRequestor;
import com.intellij.ui.classFilter.ClassFilter;
import com.sun.jdi.event.LocatableEvent;

public abstract class FilteredRequestorAdapter implements FilteredRequestor {
    @Override
    public abstract boolean processLocatableEvent(SuspendContextCommandImpl action, LocatableEvent event) throws EventProcessingException;

    @Override
    public String getSuspendPolicy() {
        return null;
    }

    @Override
    public boolean isInstanceFiltersEnabled() {
        return false;
    }

    @Override
    public InstanceFilter[] getInstanceFilters() {
        return new InstanceFilter[0];
    }

    @Override
    public boolean isCountFilterEnabled() {
        return false;
    }

    @Override
    public int getCountFilter() {
        return 0;
    }

    @Override
    public boolean isClassFiltersEnabled() {
        return false;
    }

    @Override
    public ClassFilter[] getClassFilters() {
        return new ClassFilter[0];
    }

    @Override
    public ClassFilter[] getClassExclusionFilters() {
        return new ClassFilter[0];
    }
}
