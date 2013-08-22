package com.simpleplugin.psi.extend;

import com.intellij.psi.StubBasedPsiElement;
import com.simpleplugin.psi.LSFElement;
import com.simpleplugin.psi.LSFGlobalElement;
import com.simpleplugin.psi.declarations.LSFFullNameDeclaration;
import com.simpleplugin.psi.stubs.extend.ExtendStubElement;

// тут и везде, generic'ом тип declaration тянуть, будет очень громоздко
public interface LSFExtend<This extends LSFExtend<This, Stub>, Stub extends ExtendStubElement<This, Stub>> extends LSFGlobalElement<This, Stub>, StubBasedPsiElement<Stub> {
    
    LSFFullNameDeclaration resolveDecl();
}
