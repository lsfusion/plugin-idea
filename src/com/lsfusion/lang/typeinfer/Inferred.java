package com.lsfusion.lang.typeinfer;

import com.intellij.openapi.util.Pair;
import com.lsfusion.lang.classes.DataClass;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.LSFPsiImplUtil;
import com.lsfusion.lang.psi.declarations.LSFExprParamDeclaration;
import com.lsfusion.util.BaseUtils;

import java.util.*;

// есть аналог на сервере, отличия пока:
// поддержка notNull, notNotNull в общем случае + подключение в композицию 
// applyCompared поменять по аналогии с сервером
//   в рекурсивном свойстве  - applyCompared - для рекурсии и с initial по другому сделать ??
// ExClassSet - поддержку конкретных значений (values) подключить
// Inferred поддержка значения FALSE
// в GROUP LAST - само значение в Infer не включать (так как не входит в фильтр)
public class Inferred {

    public final Map<LSFExprParamDeclaration, LSFExClassSet> params;
    public final Set<Compared> compared;

    public final Map<LSFExprParamDeclaration, LSFExClassSet> notParams;
    public final Set<Compared> notCompared;

    private static Map<LSFExprParamDeclaration, LSFExClassSet> orAny(Map<LSFExprParamDeclaration, LSFExClassSet> params) {
        Map<LSFExprParamDeclaration, LSFExClassSet> anyParams = new HashMap<LSFExprParamDeclaration, LSFExClassSet>();
        for(Map.Entry<LSFExprParamDeclaration, LSFExClassSet> param : params.entrySet()) {
            anyParams.put(param.getKey(), LSFExClassSet.orAny(param.getValue()));
        }
        return anyParams;
    } 
    
    private static <T> Map<LSFExprParamDeclaration, LSFExClassSet> applyCompared(Map<LSFExprParamDeclaration, LSFExClassSet> params, Set<Compared> compared) {
        Map<LSFExprParamDeclaration, LSFExClassSet> result = new HashMap<LSFExprParamDeclaration, LSFExClassSet>(params);
        Set<Compared> rest = new HashSet<Compared>(compared);
        for(Compared<T> compare : compared) {
            rest.remove(compare);
            InferExResult recInferred = new Inferred(result, rest).finishEx();
            // вызываем infer без этого compare чтобы предотвратить рекурсию
            LSFExClassSet val1 = compare.resolveInferred(compare.first, recInferred);
            LSFExClassSet val2 = compare.resolveInferred(compare.second, recInferred);
            LSFExClassSet value = LSFPsiImplUtil.op(val1, val2, false);
            LSFClassSet vSet = null;
            if(compare instanceof Equals || ((vSet = LSFExClassSet.fromEx(value)) instanceof DataClass)) {
                Map<LSFExprParamDeclaration, LSFExClassSet> inf1 = compare.inferResolved(compare.second, val1).finishEx().getMap();
                Map<LSFExprParamDeclaration, LSFExClassSet> inf2 = compare.inferResolved(compare.first, val2).finishEx().getMap();
                Map<LSFExprParamDeclaration, LSFExClassSet> inferred = opParams(inf1, inf2, false);
                if (compare instanceof Relationed && !((DataClass) vSet).fixedSize())
                    inferred = orAny(inferred);
                result = opParams(result, inferred, false);
            }
        }
        return result;
    }

    private static Map<LSFExprParamDeclaration, LSFExClassSet> overrideClasses(Map<LSFExprParamDeclaration, LSFExClassSet> oldClasses, Map<LSFExprParamDeclaration, LSFExClassSet> newClasses) {
        Map<LSFExprParamDeclaration, LSFExClassSet> result = new HashMap<LSFExprParamDeclaration, LSFExClassSet>(oldClasses);
        for(Map.Entry<LSFExprParamDeclaration, LSFExClassSet> paramClass : newClasses.entrySet()) {
            LSFExprParamDeclaration param = paramClass.getKey();
            LSFExClassSet newClass = paramClass.getValue();
            if(!(newClass == null && (LSFExClassSet.fromEx(result.get(param)) instanceof DataClass)))
                result.put(param, newClass);
        }
        return result;

    }
    public InferExResult finishEx() {
        final Map<LSFExprParamDeclaration, LSFExClassSet> result = overrideClasses(applyCompared(notParams, notCompared), applyCompared(params, compared));
        return new InferExResult() {
            public LSFExClassSet get(LSFExprParamDeclaration decl) {
                return result.get(decl);
            }

            public Map<LSFExprParamDeclaration, LSFExClassSet> getMap() {
                return result;
            }
        };
        
    }
    public InferResult finish() {
        final Map<LSFExprParamDeclaration, LSFClassSet> result = LSFExClassSet.fromEx(overrideClasses(applyCompared(notParams, notCompared), applyCompared(params, compared)));
        return new InferResult() {
            public LSFClassSet get(LSFExprParamDeclaration decl) {
                return result.get(decl);
            }

            public Map<LSFExprParamDeclaration, LSFClassSet> getMap() {
                return result;
            }
        }; 
    }

    public Inferred(LSFExprParamDeclaration paramDecl, LSFExClassSet set) {
        this(Collections.singletonMap(paramDecl, set));
    }

    public Inferred(Map<LSFExprParamDeclaration, LSFExClassSet> params) {
        this(params, new HashSet<Compared>());
    }

    public Inferred(Map<LSFExprParamDeclaration, LSFExClassSet> params, Set<Compared> compared) {
        this(params, compared, new HashMap<LSFExprParamDeclaration, LSFExClassSet>(), new HashSet<Compared>());
    }

    public Inferred(Map<LSFExprParamDeclaration, LSFExClassSet> params, Set<Compared> compared, Map<LSFExprParamDeclaration, LSFExClassSet> notParams, Set<Compared> notCompared) {
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
        this(new HashMap<LSFExprParamDeclaration, LSFExClassSet>());
    }

    private Inferred(Compared compared) {
        this(new HashMap<LSFExprParamDeclaration, LSFExClassSet>(), Collections.singleton(compared));
    }
    
    public static Inferred create(Compared compare) {
        return new Inferred(compare).and(compare.inferResolved(compare.first, null).and(compare.inferResolved(compare.second, null)));        
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
        return new Inferred(overrideClasses(applyCompared(params, compared), inferred.params), inferred.compared, overrideClasses(applyCompared(notParams, notCompared), inferred.notParams), inferred.notCompared); 
    }

    private static Map<LSFExprParamDeclaration, LSFExClassSet> opParams(Map<LSFExprParamDeclaration, LSFExClassSet> or1, Map<LSFExprParamDeclaration, LSFExClassSet> or2, boolean or) {
        Map<LSFExprParamDeclaration, LSFExClassSet> result = new HashMap<LSFExprParamDeclaration, LSFExClassSet>();
        for(Map.Entry<LSFExprParamDeclaration, LSFExClassSet> decl1 : or1.entrySet()) {
            LSFExprParamDeclaration key1 = decl1.getKey();
            result.put(key1, LSFPsiImplUtil.op(decl1.getValue(), or2.get(key1), or));            
        }
        for (Map.Entry<LSFExprParamDeclaration, LSFExClassSet> decl2 : or2.entrySet()) {
            LSFExprParamDeclaration key2 = decl2.getKey();
            if (!or1.containsKey(key2))
                result.put(key2, LSFPsiImplUtil.op(null, decl2.getValue(), or));
        }
        return result;
    }
    
    private static Pair<Map<LSFExprParamDeclaration, LSFExClassSet>, Set<Compared>> or(Map<LSFExprParamDeclaration, LSFExClassSet> params1, Set<Compared> compared1, Map<LSFExprParamDeclaration, LSFExClassSet> params2, Set<Compared> compared2) {
        Set<Compared> rest1 = new HashSet<Compared>(compared1);
        Set<Compared> rest2 = new HashSet<Compared>(compared2);
        Set<Compared> common = BaseUtils.split(rest1, rest2);
        return new Pair<Map<LSFExprParamDeclaration, LSFExClassSet>, Set<Compared>>(opParams(applyCompared(params1, rest1), applyCompared(params2, rest2), true), common); 
    }

    public Inferred or(Inferred or2) {
        Pair<Map<LSFExprParamDeclaration, LSFExClassSet>, Set<Compared>> or = or(params, compared, or2.params, or2.compared);
        return new Inferred(or.first, or.second, opParams(notParams, or2.notParams, false), BaseUtils.merge(notCompared, or2.notCompared));
    }

    public Inferred and(Inferred or2) {
        Pair<Map<LSFExprParamDeclaration, LSFExClassSet>, Set<Compared>> orNot = or(notParams, notCompared, or2.notParams, or2.notCompared);
        return new Inferred(opParams(params, or2.params, false), BaseUtils.merge(compared, or2.compared), orNot.first, orNot.second);
    }
}
