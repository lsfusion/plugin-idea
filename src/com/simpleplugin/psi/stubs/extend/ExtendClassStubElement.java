package com.simpleplugin.psi.stubs.extend;

import com.simpleplugin.psi.extend.LSFClassExtend;

import java.util.List;

public interface ExtendClassStubElement extends ExtendStubElement<LSFClassExtend, ExtendClassStubElement> {
    
    List<String> getShortExtends();
}
