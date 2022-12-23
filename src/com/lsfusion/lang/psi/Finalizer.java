package com.lsfusion.lang.psi;

import java.util.Collection;

public interface Finalizer<T> {
    Collection<T> finalize(Collection<T> decls);
    
    
    Finalizer EMPTY = decls -> decls;
}
