package com.lsfusion.lang.psi;

import java.util.Collection;

public interface Finalizer<T> {
    Collection<T> finalize(Collection<T> decls);
    
    
    Finalizer EMPTY = new Finalizer() {
        public Collection finalize(Collection decls) {
            return decls;
        }
    };
}
