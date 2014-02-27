package com.lsfusion.lang.psi.extend;

import com.intellij.psi.StubBasedPsiElement;
import com.lsfusion.lang.psi.LSFGlobalElement;
import com.lsfusion.lang.psi.declarations.LSFFullNameDeclaration;
import com.lsfusion.lang.psi.stubs.extend.ExtendStubElement;

// тут и везде, generic'ом тип declaration тянуть, будет очень громоздко
public interface LSFExtend<This extends LSFExtend<This, Stub>, Stub extends ExtendStubElement<This, Stub>> extends LSFGlobalElement<This, Stub>, StubBasedPsiElement<Stub> {
    
    LSFFullNameDeclaration resolveDecl();
}
