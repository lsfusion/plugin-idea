package com.simpleplugin.typeinfer;

import com.intellij.openapi.util.Pair;
import com.simpleplugin.BaseUtils;
import com.simpleplugin.LSFPsiImplUtil;
import com.simpleplugin.classes.LSFClassSet;
import com.simpleplugin.psi.declarations.LSFExprParamDeclaration;

import java.util.*;

public class Inferred {

    public final Map<LSFExprParamDeclaration, LSFClassSet> params;
    public final Set<Compared> compared;

    public final Map<LSFExprParamDeclaration, LSFClassSet> notParams;
    public final Set<Compared> notCompared;

    private static <T> Map<LSFExprParamDeclaration, LSFClassSet> applyCompared(Map<LSFExprParamDeclaration, LSFClassSet> params, Set<Compared> compared) {
        Map<LSFExprParamDeclaration, LSFClassSet> result = new HashMap<LSFExprParamDeclaration, LSFClassSet>(params);
        Set<Compared> rest = new HashSet<Compared>(compared);
        for(Compared<T> compare : compared) {
            rest.remove(compare);
            InferResult recInferred = new Inferred(result, rest).finish();
            // вызываем infer без этого compare чтобы предотвратить рекурсию
            result = opParams(result, compare.inferResolved(compare.second, compare.resolveInferred(compare.first, recInferred)).finish().getMap(), false);
            result = opParams(result, compare.inferResolved(compare.first, compare.resolveInferred(compare.second, recInferred)).finish().getMap(), false);
        }
        return result;
    }
    
    public InferResult finish() {
        final Map<LSFExprParamDeclaration, LSFClassSet> result = BaseUtils.override(applyCompared(notParams, notCompared), applyCompared(params, compared));
        return new InferResult() {
            public LSFClassSet get(LSFExprParamDeclaration decl) {
                return result.get(decl);
            }

            public Map<LSFExprParamDeclaration, LSFClassSet> getMap() {
                return result;
            }
        }; 
    }

    public Inferred(LSFExprParamDeclaration paramDecl, LSFClassSet set) {
        this(Collections.singletonMap(paramDecl, set));
    }

    public Inferred(Map<LSFExprParamDeclaration, LSFClassSet> params) {
        this(params, new HashSet<Compared>());
    }

    public Inferred(Map<LSFExprParamDeclaration, LSFClassSet> params, Set<Compared> compared) {
        this(params, compared, new HashMap<LSFExprParamDeclaration, LSFClassSet>(), new HashSet<Compared>());
    }

    public Inferred(Map<LSFExprParamDeclaration, LSFClassSet> params, Set<Compared> compared, Map<LSFExprParamDeclaration, LSFClassSet> notParams, Set<Compared> notCompared) {
        this.params = params;
        this.compared = compared;
        this.notParams = notParams;
        this.notCompared = notCompared;
    }

    public Inferred(Inferred inferred, boolean not) {
        this(inferred.notParams, inferred.notCompared, inferred.params, inferred.compared);
        assert not;
    }

    private Inferred() {
        this(new HashMap<LSFExprParamDeclaration, LSFClassSet>());
    }

    public Inferred(Compared compared) {
        this(new HashMap<LSFExprParamDeclaration, LSFClassSet>(), Collections.singleton(compared));
    }

    public static final Inferred EMPTY = new Inferred();

    public static Inferred orClasses(Collection<Inferred> ors) {
        if(ors.isEmpty())
            return EMPTY;
        
        Inferred result = null;
        for(Inferred op : ors) {
            if(result == null)
                result = op;
            else {
                result = result.or(op);
            }
        }
        return result;
    }

    public static Inferred andClasses(Collection<Inferred> ands) {
        Inferred result = EMPTY;
        for(Inferred op : ands)
            result = result.and(op);
        return result;
    }

    public Inferred filter(Set<LSFExprParamDeclaration> params) {
        return new Inferred(BaseUtils.filterNullable(this.params, params), compared, BaseUtils.filterNullable(notParams, params), notCompared);
    }
    
    public Inferred override(Inferred inferred) {
        return new Inferred(BaseUtils.override(params, inferred.params), inferred.compared, BaseUtils.override(notParams, inferred.notParams), inferred.notCompared); 
    }

    private static Map<LSFExprParamDeclaration, LSFClassSet> opParams(Map<LSFExprParamDeclaration, LSFClassSet> or1, Map<LSFExprParamDeclaration, LSFClassSet> or2, boolean or) {
        Map<LSFExprParamDeclaration, LSFClassSet> result = new HashMap<LSFExprParamDeclaration, LSFClassSet>(or1);
        for(Map.Entry<LSFExprParamDeclaration, LSFClassSet> decl : or2.entrySet()) {
            LSFClassSet orClass = result.get(decl.getKey());
            if(orClass!=null)
                orClass = LSFPsiImplUtil.op(orClass, decl.getValue(), or);
            else
                orClass = decl.getValue();
            result.put(decl.getKey(), orClass);
        }
        return result;
    }
    
    private static Pair<Map<LSFExprParamDeclaration, LSFClassSet>, Set<Compared>> or(Map<LSFExprParamDeclaration, LSFClassSet> params1, Set<Compared> compared1, Map<LSFExprParamDeclaration, LSFClassSet> params2, Set<Compared> compared2) {
        Set<Compared> rest1 = new HashSet<Compared>(compared1);
        Set<Compared> rest2 = new HashSet<Compared>(compared2);
        Set<Compared> common = BaseUtils.split(rest1, rest2);
        return new Pair<Map<LSFExprParamDeclaration, LSFClassSet>, Set<Compared>>(opParams(applyCompared(params1, rest1), applyCompared(params2, rest2), true), common); 
    }

    public Inferred or(Inferred or2) {
        Pair<Map<LSFExprParamDeclaration, LSFClassSet>, Set<Compared>> or = or(params, compared, or2.params, or2.compared);
        return new Inferred(or.first, or.second, opParams(notParams, or2.notParams, false), BaseUtils.merge(notCompared, or2.notCompared));
    }

    public Inferred and(Inferred or2) {
        Pair<Map<LSFExprParamDeclaration, LSFClassSet>, Set<Compared>> orNot = or(notParams, notCompared, or2.notParams, or2.notCompared);
        return new Inferred(opParams(params, or2.params, false), BaseUtils.merge(compared, or2.compared), orNot.first, orNot.second);
    }
}
