package com.lsfusion.lang.psi.declarations;

import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.stubs.PropStubElement;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface LSFGlobalPropDeclaration extends LSFFullNameDeclaration<LSFGlobalPropDeclaration, PropStubElement>, LSFPropDeclaration {

    @Nullable
    LSFDataPropertyDefinition getDataPropertyDefinition();

    @Nullable
    public abstract LSFActionStatement getActionStatement();

    @Nullable
    public abstract LSFPropertyCalcStatement getPropertyCalcStatement();

    String getCaption();

    boolean isAction();
    
    boolean isStoredProperty();

    boolean isPersistentProperty();

    boolean isDataProperty();

    boolean isDataStoredProperty();

    boolean isUnfriendly();

    public List<String> resolveParamNames();

    boolean resolveDuplicateColumns();

    @Nullable
    String getTableName();

    @Nullable
    String getTableNameNoCache();

    @Nullable
    String getColumnName();

    @Nullable
    String getColumnNameNoCache();
}
