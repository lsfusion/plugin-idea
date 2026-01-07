package com.lsfusion.lang.psi.stubs;

import com.lsfusion.lang.psi.declarations.LSFFullNameDeclaration;

public interface FullNameStubElement<This extends FullNameStubElement<This, Decl>, Decl extends LSFFullNameDeclaration<Decl, This>> extends GlobalDeclStubElement<This, Decl> {
    
    int getOffset();
}
