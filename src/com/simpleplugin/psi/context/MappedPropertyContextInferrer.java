package com.simpleplugin.psi.context;

import com.simpleplugin.BaseUtils;
import com.simpleplugin.classes.LSFClassSet;
import com.simpleplugin.psi.LSFClassParamDeclare;
import com.simpleplugin.psi.LSFClassParamDeclareList;
import com.simpleplugin.psi.LSFMappedPropertyClassParamDeclare;
import com.simpleplugin.psi.LSFNonEmptyClassParamDeclareList;
import com.simpleplugin.psi.declarations.LSFExprParamDeclaration;
import com.simpleplugin.psi.declarations.LSFPropDeclaration;
import com.simpleplugin.typeinfer.Inferred;

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
