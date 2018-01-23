package com.lsfusion.lang.psi.declarations;

import com.lsfusion.lang.psi.LSFNavigatorElementDescription;
import com.lsfusion.lang.psi.LSFSimpleName;
import com.lsfusion.lang.psi.stubs.NavigatorElementStubElement;

public interface LSFNavigatorElementDeclaration extends LSFFullNameDeclaration<LSFNavigatorElementDeclaration, NavigatorElementStubElement> {
    LSFSimpleName getSimpleName(); 
    
    LSFNavigatorElementDescription getNavigatorElementDescription();
}
