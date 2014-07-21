package com.lsfusion.lang.psi.declarations;

import com.lsfusion.lang.psi.LSFDataPropertyDefinition;
import com.lsfusion.lang.psi.LSFExpressionUnfriendlyPD;
import com.lsfusion.lang.psi.LSFPropertyExpression;
import com.lsfusion.lang.psi.stubs.PropStubElement;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface LSFGlobalPropDeclaration extends LSFFullNameDeclaration<LSFGlobalPropDeclaration, PropStubElement>, LSFPropDeclaration {

    @Nullable
    LSFDataPropertyDefinition getDataPropertyDefinition();

    @Nullable
    LSFExpressionUnfriendlyPD getExpressionUnfriendlyPD();

    @Nullable
    LSFPropertyExpression getPropertyExpression();

    String getCaption();

    boolean isAction();
    
    boolean isStoredProperty();

    boolean isDataStoredProperty();

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
