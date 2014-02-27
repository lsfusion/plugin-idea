package com.lsfusion.psi;

import java.util.Collection;

public interface Finalizer<T> {
    Collection<T> finalize(Collection<T> decls);
    
    
    public final static Finalizer EMPTY = new Finalizer() {
        public Collection finalize(Collection decls) {
            return decls;
        }
    };
}
