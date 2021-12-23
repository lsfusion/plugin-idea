package com.lsfusion.lang.typeinfer;

public abstract class Compared<T> {
    
    public final T first;
    public final T second;

    protected Compared(T first, T second) {
        this.first = first;
        this.second = second;
    }

    public abstract LSFExClassSet resolveInferred(T operand, InferExResult inferred);
    public abstract Inferred inferResolved(T operand, LSFExClassSet classSet);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Compared compared = (Compared) o;

        return (first.equals(compared.first) && second.equals(compared.second)) || (first.equals(compared.second) && second.equals(compared.first));
    }

    @Override
    public int hashCode() {
        return first.hashCode() + second.hashCode();
    }
}
