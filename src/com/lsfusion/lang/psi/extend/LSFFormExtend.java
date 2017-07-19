package com.lsfusion.lang.psi.extend;

import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.*;
import com.lsfusion.lang.psi.stubs.extend.ExtendFormStubElement;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface LSFFormExtend extends LSFExtend<LSFFormExtend, ExtendFormStubElement> {

    LSFFormDecl getFormDecl();

    Collection<LSFObjectDeclaration> getObjectDecls();
    
    Collection<LSFGroupObjectDeclaration> getGroupObjectDecls();
    
    Collection<LSFTreeGroupDeclaration> getTreeGroupDecls();
    
    Collection<LSFGroupObjectDeclaration> getGroupObjectNoTreeDecls();

    Collection<LSFFilterGroupDeclaration> getFilterGroupDecls();

    Collection<LSFFormGroupObjectDeclaration> getFormGroupObjectDeclarations();

    @NotNull
    List<LSFFormTreeGroupObjectList> getFormTreeGroupObjectListList();

    List<LSFFormPropertiesList> getFormPropertiesListList();

    Collection<LSFPropertyDrawDeclaration> getPropertyDrawDecls();

    List<LSFFormFilterGroupDeclaration> getFormFilterGroupDeclarationList();

    List<LSFFormExtendFilterGroupDeclaration> getFormExtendFilterGroupDeclarationList();

    Set<LSFDeclaration> resolveDuplicates();
}
