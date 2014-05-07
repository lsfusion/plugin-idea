package com.lsfusion.lang.psi.context;

import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.LSFClassParamDeclare;
import com.lsfusion.lang.psi.LSFClassParamDeclareList;
import com.lsfusion.lang.psi.LSFMappedPropertyClassParamDeclare;
import com.lsfusion.lang.psi.LSFNonEmptyClassParamDeclareList;
import com.lsfusion.lang.psi.declarations.LSFExprParamDeclaration;
import com.lsfusion.lang.psi.declarations.LSFPropDeclaration;
import com.lsfusion.lang.typeinfer.Inferred;
import com.lsfusion.util.BaseUtils;

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