package com.lsfusion.lang.psi.stubs.extend;

import com.lsfusion.lang.psi.extend.LSFClassExtend;

import java.util.List;

public interface ExtendClassStubElement extends ExtendStubElement<LSFClassExtend, ExtendClassStubElement> {
    
    List<String> getShortExtends();
}
