package com.lsfusion.lang.psi.declarations;

import com.lsfusion.documentation.LSFDocumentation;
import com.lsfusion.lang.classes.LSFValueClass;
import com.lsfusion.mcp.LSFMCPDeclaration;
import com.lsfusion.lang.psi.stubs.TableStubElement;
import org.jetbrains.annotations.NotNull;

public interface LSFTableDeclaration extends LSFFullNameDeclaration<LSFTableDeclaration, TableStubElement>, LSFDocumentation, LSFMCPDeclaration {
    @NotNull
    LSFValueClass[] getClasses();

    @NotNull
    String[] getClassNames();
    
    boolean isExplicit();
}
