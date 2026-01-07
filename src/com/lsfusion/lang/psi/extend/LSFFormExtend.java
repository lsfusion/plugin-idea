package com.lsfusion.lang.psi.extend;

import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFFilterGroupDeclaration;
import com.lsfusion.lang.psi.declarations.LSFGroupObjectDeclaration;
import com.lsfusion.lang.psi.declarations.LSFObjectDeclaration;
import com.lsfusion.lang.psi.declarations.LSFPropertyDrawDeclaration;
import com.lsfusion.lang.psi.stubs.extend.ExtendFormStubElement;
import com.lsfusion.mcp.LSFMCPDeclaration;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public interface LSFFormExtend extends LSFExtend<LSFFormExtend, ExtendFormStubElement>, LSFMCPDeclaration {

    LSFFormDecl getFormDecl();

    Collection<LSFObjectDeclaration> getObjectDecls();
    
    Collection<LSFGroupObjectDeclaration> getGroupObjectDecls();
    
    Collection<LSFTreeGroupDeclaration> getTreeGroupDecls();
    
    Collection<LSFGroupObjectDeclaration> getGroupObjectNoTreeDecls();

    Collection<LSFFilterGroupDeclaration> getFilterGroupDecls();

    List<LSFFormGroupObjectDeclaration> getFormGroupObjectDeclarations();

    @NotNull
    List<LSFFormTreeGroupObjectList> getFormTreeGroupObjectListList();

    List<LSFFormPropertiesList> getFormPropertiesListList();

    Collection<LSFPropertyDrawDeclaration> getPropertyDrawDecls();

    List<LSFFormFilterGroupDeclaration> getFormFilterGroupDeclarationList();

    List<LSFFormExtendFilterGroupDeclaration> getFormExtendFilterGroupDeclarationList();
    
    List<LSFUserFiltersDeclaration> getUserFiltersDeclarationList();
}
