package com.lsfusion.lang.psi.stubs;

import com.lsfusion.lang.psi.declarations.LSFGlobalDeclaration;

public interface GlobalDeclStubElement<This extends GlobalDeclStubElement<This, Decl>, Decl extends LSFGlobalDeclaration<Decl, This>>
        extends GlobalStubElement<This, Decl> {
}
