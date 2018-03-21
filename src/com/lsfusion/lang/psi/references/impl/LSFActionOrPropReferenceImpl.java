package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Conditions;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.lang.LSFElementGenerator;
import com.lsfusion.lang.LSFReferenceAnnotator;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.meta.MetaTransaction;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.context.PropertyUsageContext;
import com.lsfusion.lang.psi.declarations.*;
import com.lsfusion.lang.psi.references.LSFActionOrPropReference;
import com.lsfusion.util.BaseUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.lsfusion.lang.psi.references.impl.LSFPropReferenceImpl.enableAbstractImpl;

public abstract class LSFActionOrPropReferenceImpl<T extends LSFActionOrPropDeclaration, G extends LSFActionOrGlobalPropDeclaration> extends LSFFullNameReferenceImpl<T, G> implements LSFActionOrPropReference<T, G> {

    public LSFActionOrPropReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    protected Finalizer<T> getNoConditionFinalizer(List<LSFClassSet> usageClasses) {
        return new Finalizer<T>() {
            public Collection<T> finalize(Collection<T> decls) {
                Map<T, Integer> declMap = new HashMap<>();

                GlobalSearchScope scope = GlobalSearchScope.allScope(getProject());

                for (T decl : decls) {
                    List<LSFClassSet> declClasses = decl.resolveParamClasses();
                    if (declClasses == null) {
                        declMap.put(decl, 0);
                        continue;
                    }

                    declMap.put(decl, LSFPsiImplUtil.getCommonChildrenCount(declClasses, usageClasses, scope));
                }

                int commonClasses = 0;
                List<T> result = new ArrayList<>();
                for (Map.Entry<T, Integer> entry : declMap.entrySet()) {
                    if (entry.getValue() > commonClasses) {
                        commonClasses = entry.getValue();
                        result = new ArrayList<>();
                        result.add(entry.getKey());
                    } else if (entry.getValue() == commonClasses) {
                        result.add(entry.getKey());
                    }
                }
                return result;
            }
        };
    }

    @Nullable
    public List<LSFClassSet> getExplicitClasses() {
        LSFExplicitPropClassUsage explicitUsage = getExplicitPropClassUsage();
        if(explicitUsage == null)
            return null;

        LSFEmptyExplicitPropClassList eList = explicitUsage.getEmptyExplicitPropClassList();
        LSFNonEmptyExplicitPropClassList neList = eList.getNonEmptyExplicitPropClassList();
        List<LSFClassSet> result = new ArrayList<>();
        if(neList!=null) {
            for (LSFExplicitPropClass explPropClass : neList.getExplicitPropClassList()) {
                LSFClassName className = explPropClass.getClassName();
                if (className != null)
                    result.add(LSFPsiImplUtil.resolveClass(className));
                else
                    result.add(null);
            }
        }
        return result;
    }

    @Nullable
    protected PropertyUsageContext getPropertyUsageContext() {
        return PsiTreeUtil.getParentOfType(this, PropertyUsageContext.class);
    }
    
    @Nullable
    protected List<LSFClassSet> getUsageContext() {
        PropertyUsageContext propertyUsageContext = getPropertyUsageContext();
        return propertyUsageContext == null ? null : propertyUsageContext.resolveParamClasses();
    }

    public boolean onlyNotEquals() {
        return false;
    }

    protected boolean canBeUsedInDirect() {
        return getExplicitClasses() == null && getUsageContext() != null && !isImplement();
    }

    protected Condition<T> getDirectCondition() {
        List<LSFClassSet> directClasses = getExplicitClasses();
        if(directClasses == null) {
            directClasses = getUsageContext();
            if(directClasses==null) // невозможно определить классы, подходят все
                return Condition.TRUE;
        }

        final List<LSFClassSet> fDirectClasses = directClasses;
        final boolean isImplement = enableAbstractImpl && isImplement();
        return new Condition<T>() {
            public boolean value(T decl) {
                if(isImplement && !decl.isAbstract())
                    return false;

                List<LSFClassSet> declClasses = decl.resolveParamClasses();
                return declClasses == null || (declClasses.size() == fDirectClasses.size() && LSFPsiImplUtil.containsAll(declClasses, fDirectClasses, false));
            }
        };
    }

    protected Condition<T> getInDirectCondition() {
        final List<LSFClassSet> usageClasses = getUsageContext();
        assert canBeUsedInDirect(); // потому как иначе direct бы подошел  
        final boolean isImplement = enableAbstractImpl && isImplement();

        return new Condition<T>() {
            public boolean value(T decl) {
                if(isImplement && !decl.isAbstract())
                    return false;

                List<LSFClassSet> declClasses = decl.resolveParamClasses();
                assert declClasses != null; // потому как иначе direct бы подошел 
                return declClasses.size() == usageClasses.size() && LSFPsiImplUtil.haveCommonChilds(declClasses, usageClasses, GlobalSearchScope.allScope(getProject()));
            }
        };
    }

    protected Finalizer<T> getDirectFinalizer() {
        List<LSFClassSet> explicitClasses = getExplicitClasses();
        List<LSFClassSet> directClasses = explicitClasses;
        if(directClasses == null) {
            directClasses = getUsageContext();
            if(directClasses==null) // невозможно определить прямое или обратное использование, соответственно непонятно как "экранировать"
                return Finalizer.EMPTY;
        }
        final boolean onlyNotEquals = onlyNotEquals();
        final boolean isNotEquals = onlyNotEquals || (enableAbstractImpl && isImplement() && explicitClasses == null);

        final List<LSFClassSet> fDirectClasses = directClasses;
        return new Finalizer<T>() {
            public Collection<T> finalize(Collection<T> decls) {

                Map<T, List<LSFClassSet>> mapClasses = new HashMap<>();
                Set<T> equals = isNotEquals ? new HashSet<>() : null;
                for (T decl : decls) {
                    List<LSFClassSet> declClasses = decl.resolveParamClasses();
                    if(declClasses != null && declClasses.size() == fDirectClasses.size()) { // double check, так как из-за recursion guard'а decl.resolvePC может внутри проверки condition возвращать null и соотвественно подходить, а в finalizer'е классы resolve'ся и уже не подходит (можно было бы и containsAll проверять но это серьезный overhead будет)
                        assert declClasses.size() == fDirectClasses.size();
                        if(isNotEquals && declClasses.equals(fDirectClasses))
                            equals.add(decl);
                        mapClasses.put(decl, declClasses);
                    }
                }

                if (!mapClasses.isEmpty()) { // есть прямые наследования
                    Collection<T> result = new ArrayList<>();

                    if((isNotEquals && equals.size() < mapClasses.size()) || onlyNotEquals) // оставим только не равные 
                        mapClasses = BaseUtils.filterNotKeys(mapClasses, equals);

                    List<T> listMapClasses = new ArrayList<>(mapClasses.keySet());
                    for (int i = 0, size = listMapClasses.size(); i < size; i++) {
                        T decl = listMapClasses.get(i);
                        List<LSFClassSet> classesI = mapClasses.get(decl);
                        boolean found = false;
                        for (int j = 0; j < size; j++)
                            if (i != j) {
                                List<LSFClassSet> classesJ = mapClasses.get(listMapClasses.get(j));
                                if (LSFPsiImplUtil.containsAll(classesI, classesJ, true) && !LSFPsiImplUtil.containsAll(classesJ, classesI, true)) {
                                    found = true;
                                    break;
                                }
                            }
                        if (!found)
                            result.add(decl);
                    }

                    return result;
                }

                return decls;
            }
        };
    }

    @Override
    public Condition<G> getCondition() {
        return BaseUtils.immutableCast(getDirectCondition());
    }

    @Override
    public Condition<G> getFullCondition() {
        Condition<G> result = super.getFullCondition();
        if(canBeUsedInDirect())
            result = Conditions.or(result, BaseUtils.<Condition<G>>immutableCast(getInDirectCondition()));
        return result;
    }

    @Override
    protected Finalizer<G> getFinalizer() {
        return BaseUtils.immutableCast(getDirectFinalizer());
    }

    @Override
    public Annotation resolveAmbiguousErrorAnnotation(AnnotationHolder holder, Collection<? extends LSFDeclaration> declarations) {
        String ambError = "Ambiguous reference";

        String description = "";
        int i = 1;
        List<T> decls = new ArrayList<>((Collection<? extends T>) declarations);
        for (T decl : decls) {
            description += decl.getPresentableText();

            if (i < decls.size() - 1) {
                description += ", ";
            } else if (i == decls.size() - 1) {
                description += " and ";
            }

            i++;
        }

        if (!description.isEmpty()) {
            ambError += ": " + description + " match";
        }

        return resolveErrorTarget(holder, ambError, false);
    }
    
    protected abstract String getErrorName();

    @Override
    public Annotation resolveNotFoundErrorAnnotation(AnnotationHolder holder, Collection<? extends LSFDeclaration> similarDeclarations) {
        String errorText;
        boolean noSuchProperty = similarDeclarations.size() == 0;
        if (similarDeclarations.size() != 1) {
            if (noSuchProperty) {
                errorText = BaseUtils.capitalize(getErrorName()) + " '" + getNameRef() + "' not found";
            } else {
                errorText = "Cannot resolve " + getErrorName() + " " + getNameRef() + listClassesToString(getUsageContext());
            }
        } else {
            errorText = similarDeclarations.iterator().next().getPresentableText() + " cannot be applied to " +
                    getNameRef() + listClassesToString(getUsageContext());
        }

        return resolveErrorTarget(holder, errorText, noSuchProperty);
    }

    private String listClassesToString(List<LSFClassSet> classes) {
        String result = "(";
        if (classes != null) {
            for (Iterator<LSFClassSet> iterator = classes.iterator(); iterator.hasNext(); ) {
                LSFClassSet set = iterator.next();
                result += set == null ? "?" : set;
                result += iterator.hasNext() ? ", " : "";
            }
        }
        return result += ")";
    }

    public Annotation resolveErrorTarget(AnnotationHolder holder, String errorText, boolean noSuchProperty) {
        Annotation annotation;
        PropertyUsageContext propertyUsageContext = getPropertyUsageContext();
        PsiElement paramList = propertyUsageContext == null ? null : propertyUsageContext.getParamList();
        if (noSuchProperty || paramList == null) {
            annotation = holder.createErrorAnnotation(getTextRange(), errorText);
        } else {
            annotation = holder.createErrorAnnotation(paramList, errorText);
        }
        if (!noSuchProperty) {
            annotation.setEnforcedTextAttributes(LSFReferenceAnnotator.WAVE_UNDERSCORED_ERROR);
        }
        return annotation;
    }

    public static final boolean enableAbstractImpl = true;

    public boolean isDirect() {
        List<LSFClassSet> usageContext = getExplicitClasses();
        if(usageContext == null) {
            usageContext = getUsageContext();
            if(usageContext==null) // невозможно определить классы, подходят все
                return true;
        }

        T decl = resolveDecl();
        assert decl != null; // предполагается что ошибка resolve'а уже отработана

        List<LSFClassSet> declClasses = decl.resolveParamClasses();
        if (declClasses == null)
            return true;

        if(declClasses.size() != usageContext.size())
            return false;

        if (LSFPsiImplUtil.containsAll(declClasses, usageContext, false)) // подходят по классам
            return true;

        return false;
    }

    public boolean isNoContext() {
        return isNoContext(getPropertyUsageContext());
    }
    public abstract boolean isNoContext(PropertyUsageContext usageContext);

    protected abstract LSFExplicitPropClassUsage getExplicitPropClassUsage();

    @Override
    public boolean hasExplicitClasses() {
        return getExplicitPropClassUsage() != null;
    }

    public void setExplicitClasses(List<LSFClassSet> classes, MetaTransaction transaction) {
        String explicitClasses = "";
        for(LSFClassSet declClass : classes)
            explicitClasses = (explicitClasses.isEmpty() ? "" : explicitClasses + ",") + (declClass == null ? "?" : declClass.getCommonClass().getQName(this));
        LSFExplicitPropClassUsage explicitNode = LSFElementGenerator.createExplicitClassUsageFromText(getProject(), explicitClasses);

        if(transaction != null) // ? разрезать или нет на leafToken
            transaction.regChange(MetaTransaction.getLeafTokens(explicitNode.getNode()), getSimpleName().getNode(), MetaTransaction.Type.AFTER);

        addAfter(explicitNode, getCompoundID());
    }

    public void dropExplicitClasses(MetaTransaction transaction) {
        ASTNode node = getExplicitPropClassUsage().getNode();

        if(transaction != null)
            transaction.regChange(new ArrayList<ASTNode>(), node, MetaTransaction.Type.REPLACE);

        deleteChildInternal(node);
    }

    @Override
    public void dropFullNameRef(MetaTransaction transaction) {
        if(transaction!=null) {
            assert getFullNameRef() != null;
            ASTNode namespaceNode = getCompoundID().getNamespaceUsage().getNode();
            transaction.regChange(new ArrayList<ASTNode>(), namespaceNode, 1, MetaTransaction.Type.REPLACE);
        }
        LSFCompoundID compoundIDFromText = LSFElementGenerator.createCompoundIDFromText(getProject(), getNameRef());
        getCompoundID().replace(compoundIDFromText);
    }
}
