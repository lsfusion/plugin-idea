package com.lsfusion.lang.psi.declarations;

import com.lsfusion.lang.psi.LSFStubElement;
import com.lsfusion.lang.psi.LSFStubbedElement;

public interface LSFExplicitValueProp<This extends LSFExplicitValueProp<This, Stub>, Stub extends LSFStubElement<Stub, This>> extends LSFStubbedElement<This, Stub> {

    LSFGlobalPropDeclaration getDeclaration();

}
