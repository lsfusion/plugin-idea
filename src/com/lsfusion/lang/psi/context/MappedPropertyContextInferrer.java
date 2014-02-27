package com.lsfusion.psi.context;

import com.lsfusion.util.BaseUtils;
import com.lsfusion.classes.LSFClassSet;
import com.lsfusion.psi.LSFClassParamDeclare;
import com.lsfusion.psi.LSFClassParamDeclareList;
import com.lsfusion.psi.LSFMappedPropertyClassParamDeclare;
import com.lsfusion.psi.LSFNonEmptyClassParamDeclareList;
import com.lsfusion.psi.declarations.LSFExprParamDeclaration;
import com.lsfusion.psi.declarations.LSFPropDeclaration;
import com.lsfusion.typeinfer.Inferred;

import java.util.*;

public class MappedPropertyContextInferrer implements ContextInferrer {
    private final LSFMappedPropertyClassParamDeclare prop;

    public MappedPropertyContextInferrer(LSFMappedPropertyClassParamDeclare prop) {
        this.prop = prop;
    }

    @Override
    public Inferred inferClasses(Set<LSFExprParamDeclaration> params) {
        LSFPropDeclaration decl = prop.getPropertyUsage().resolveDecl();
        if(decl != null) {
            List<LSFClassSet> inferredClasses = decl.inferParamClasses(null);
            if(inferredClasses == null)
                return Inferred.EMPTY;
            
            LSFClassParamDeclareList declList = prop.getClassParamDeclareList();
            if (declList == null) {
                return Inferred.EMPTY;
            }

            List<LSFClassParamDeclare> paramDecls;
            LSFNonEmptyClassParamDeclareList neList = declList.getNonEmptyClassParamDeclareList();
            if(neList == null)
                paramDecls = new ArrayList<LSFClassParamDeclare>();
            else
                paramDecls = neList.getClassParamDeclareList();
            Map<LSFExprParamDeclaration, LSFClassSet> result = new HashMap<LSFExprParamDeclaration, LSFClassSet>();
            for(int i=0;i<BaseUtils.min(paramDecls.size(), inferredClasses.size());i++)
                result.put(paramDecls.get(i).getParamDeclare(), inferredClasses.get(i));
            return new Inferred(result);                            
        }
        return Inferred.EMPTY;
    }
}
