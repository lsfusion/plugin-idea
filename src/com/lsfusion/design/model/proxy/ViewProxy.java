package com.lsfusion.design.model.proxy;

public abstract class ViewProxy<T> {
    protected final T target;

    public ViewProxy(T target) {
        this.target = target;
    }
}
