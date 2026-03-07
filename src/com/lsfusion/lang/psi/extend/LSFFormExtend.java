package com.lsfusion.lang.psi.extend;

import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.*;
import com.lsfusion.lang.psi.stubs.extend.ExtendFormStubElement;
import com.lsfusion.mcp.LSFMCPDeclaration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface LSFFormExtend extends LSFFormContextExtend<LSFFormExtend, ExtendFormStubElement>, LSFMCPDeclaration {

    LSFFormDecl getFormDecl();

    Collection<LSFFormFormsListItem> getFormDecls();

    Collection<LSFObjectDeclaration> getObjectDecls();

    Collection<LSFGroupObjectDeclaration> getGroupObjectDecls();

    @SuppressWarnings("rawtypes")
    default Collection<LSFObjectOrGroupObjectDeclaration> getObjectOrGroupObjectDecls() {
        List<LSFObjectOrGroupObjectDeclaration> result = new ArrayList<>();
        result.addAll(getObjectDecls());
        for (LSFGroupObjectDeclaration go : getGroupObjectDecls())
            if (!go.isSingle())
                result.add(go);
        return result;
    }
    
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
