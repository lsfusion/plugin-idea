package com.lsfusion.psi.stubs.extend;

import com.lsfusion.psi.extend.LSFClassExtend;

import java.util.List;

public interface ExtendClassStubElement extends ExtendStubElement<LSFClassExtend, ExtendClassStubElement> {
    
    List<String> getShortExtends();
}
