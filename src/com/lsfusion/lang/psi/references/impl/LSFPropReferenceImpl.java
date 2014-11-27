package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Conditions;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.CollectionQuery;
import com.lsfusion.lang.LSFElementGenerator;
import com.lsfusion.lang.LSFReferenceAnnotator;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.meta.MetaTransaction;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.context.PropertyUsageContext;
import com.lsfusion.lang.psi.declarations.LSFDeclaration;
import com.lsfusion.lang.psi.declarations.LSFGlobalPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFLocalPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFPropDeclaration;
import com.lsfusion.lang.psi.references.LSFPropReference;
import com.lsfusion.lang.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import com.lsfusion.util.BaseUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class LSFPropReferenceImpl extends LSFFullNameReferenceImpl<LSFPropDeclaration, LSFGlobalPropDeclaration> implements LSFPropReference {

    public LSFPropReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    protected FullNameStubElementType<?, LSFGlobalPropDeclaration> getStubElementType() {
        return LSFStubElementTypes.PROP;
    }

    private static class LocalResolveProcessor implements PsiScopeProcessor {

        private final String name;
        private Collection<LSFLocalPropDeclaration> found = new ArrayList<LSFLocalPropDeclaration>();
        private final Condition<LSFPropDeclaration> condition;

        private LocalResolveProcessor(String name, Condition<LSFPropDeclaration> condition) {
            this.name = name;
            this.condition = condition;
        }

        @Override
        public boolean execute(@NotNull PsiElement element, ResolveState state) {
            if(element instanceof LSFLocalPropDeclaration) {
                LSFLocalPropDeclaration decl = (LSFLocalPropDeclaration) element;
                if (decl.getName().equals(name) && condition.value(decl))
                    found.add(decl);
            }
            return true;
        }

        @Nullable
        @Override
        public <T> T getHint(@NotNull Key<T> hintKey) {
            return null;
        }

        @Override
        public void handleEvent(Event event, @Nullable Object associated) {
        }
    }

    @Override
    public LSFResolveResult resolveNoCache() {
        Collection<? extends LSFDeclaration> declarations = BaseUtils.emptyList();

        if (getFullNameRef() == null)
            declarations = resolveLocals(BaseUtils.<Condition<LSFPropDeclaration>>immutableCast(getCondition()), getFinalizer());

        if (declarations.isEmpty()) {
            declarations = super.resolveNoCache().declarations;
        }

        if(canBeUsedInDirect()) {
            if(declarations.isEmpty())
                declarations = resolveLocals(getInDirectCondition(), Finalizer.EMPTY);

            if(declarations.isEmpty())
                declarations = LSFGlobalResolver.findElements(getNameRef(), getFullNameRef(), getLSFFile(), getStubElementTypes(), BaseUtils.<Condition<LSFGlobalPropDeclaration>>immutableCast(getInDirectCondition()), Finalizer.EMPTY);
        }

        LSFResolveResult.ErrorAnnotator errorAnnotator = null;
        if (declarations.size() > 1) {
            errorAnnotator = new LSFResolveResult.AmbigiousErrorAnnotator(this, declarations);
        } else if (declarations.isEmpty()) {
            declarations = resolveNoConditionDeclarations();

            errorAnnotator = new LSFResolveResult.NotFoundErrorAnnotator(this, declarations);
        }

        return new LSFResolveResult(declarations, errorAnnotator);
    }

    private Collection<LSFPropDeclaration> resolveNoConditionDeclarations() {
        final List<LSFClassSet> usageClasses = getUsageContext();
        if (usageClasses != null) {
            CollectionQuery<LSFPropDeclaration> declarations = new CollectionQuery<LSFPropDeclaration>(LSFGlobalResolver.findElements(getNameRef(), getFullNameRef(), getLSFFile(), getStubElementTypes(), Condition.TRUE, new Finalizer() {
                @Override
                public Collection finalize(Collection decls) {
                    Map<LSFPropDeclaration, Integer> declMap = new HashMap<LSFPropDeclaration, Integer>();

                    GlobalSearchScope scope = GlobalSearchScope.allScope(getProject());

                    for (Iterator<LSFPropDeclaration> iterator = decls.iterator(); iterator.hasNext(); ) {
                        LSFPropDeclaration decl = iterator.next();
                        List<LSFClassSet> declClasses = decl.resolveParamClasses();
                        if (declClasses == null) {
                            declMap.put(decl, 0);
                            continue;
                        }

                        declMap.put(decl, LSFPsiImplUtil.getCommonChildrenCount(declClasses, usageClasses, scope));
                    }

                    int commonClasses = 0;
                    List<LSFDeclaration> result = new ArrayList<LSFDeclaration>();
                    for (Map.Entry<LSFPropDeclaration, Integer> entry : declMap.entrySet()) {
                        if (entry.getValue() > commonClasses) {
                            commonClasses = entry.getValue();
                            result = new ArrayList<LSFDeclaration>();
                            result.add(entry.getKey());
                        } else if (entry.getValue() == commonClasses) {
                            result.add(entry.getKey());
                        }
                    }
                    return result;
                }
            }));

            return declarations.findAll();

        }
        return new ArrayList<LSFPropDeclaration>();
    }

    @Nullable
    private PropertyUsageContext getPropertyUsageContext() {
        return PsiTreeUtil.getParentOfType(this, PropertyUsageContext.class);
    }

    public boolean isNoContext() {
        return getPropertyUsageContext() instanceof LSFNoContextPropertyUsage;
    }

    @Nullable
    protected List<LSFClassSet> getUsageContext() {
        PropertyUsageContext propertyUsageContext = getPropertyUsageContext();
        return propertyUsageContext == null ? null : propertyUsageContext.resolveParamClasses();
    }

    protected abstract LSFExplicitPropClassUsage getExplicitPropClassUsage();

    @Override
    public boolean hasExplicitClasses() {
        return getExplicitPropClassUsage() != null;
    }

    @Nullable
    public List<LSFClassSet> getExplicitClasses() {
        LSFExplicitPropClassUsage explicitUsage = getExplicitPropClassUsage();
        if(explicitUsage == null)
            return null;

        LSFEmptyExplicitPropClassList eList = explicitUsage.getEmptyExplicitPropClassList();
        LSFNonEmptyExplicitPropClassList neList = eList.getNonEmptyExplicitPropClassList();
        List<LSFClassSet> result = new ArrayList<LSFClassSet>();
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

    public static final boolean enableAbstractImpl = true;

    private Condition<LSFPropDeclaration> getDirectCondition() {
        List<LSFClassSet> directClasses = getExplicitClasses();
        if(directClasses == null) {
            directClasses = getUsageContext();
            if(directClasses==null) // невозможно определить классы, подходят все
                return Condition.TRUE;
        }

        final List<LSFClassSet> fDirectClasses = directClasses;
        final boolean isImplement = enableAbstractImpl && isImplement();
        return new Condition<LSFPropDeclaration>() {
            public boolean value(LSFPropDeclaration decl) {
                if(isImplement && !decl.isAbstract())
                    return false;

                List<LSFClassSet> declClasses = decl.resolveParamClasses();
                return declClasses == null || (declClasses.size() == fDirectClasses.size() && LSFPsiImplUtil.containsAll(declClasses, fDirectClasses, false));
            }
        };
    }

    private boolean canBeUsedInDirect() {
        return getExplicitClasses() == null && getUsageContext() != null && !isImplement();
    }

    private Condition<LSFPropDeclaration> getInDirectCondition() {
        final List<LSFClassSet> usageClasses = getUsageContext();
        assert canBeUsedInDirect(); // потому как иначе direct бы подошел  
        final boolean isImplement = enableAbstractImpl && isImplement();

        return new Condition<LSFPropDeclaration>() {
            public boolean value(LSFPropDeclaration decl) {
                if(isImplement && !decl.isAbstract())
                    return false;

                List<LSFClassSet> declClasses = decl.resolveParamClasses();
                assert declClasses != null; // потому как иначе direct бы подошел 
                return declClasses.size() == usageClasses.size() && LSFPsiImplUtil.haveCommonChilds(declClasses, usageClasses, GlobalSearchScope.allScope(getProject()));
            }
        };
    }

    private Finalizer<LSFPropDeclaration> getDirectFinalizer() {
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
        return new Finalizer<LSFPropDeclaration>() {
            public Collection<LSFPropDeclaration> finalize(Collection<LSFPropDeclaration> decls) {

                Map<LSFPropDeclaration, List<LSFClassSet>> mapClasses = new HashMap<LSFPropDeclaration, List<LSFClassSet>>();
                Set<LSFPropDeclaration> equals = isNotEquals ? new HashSet<LSFPropDeclaration>() : null;
                for (LSFPropDeclaration decl : decls) {
                    List<LSFClassSet> declClasses = decl.resolveParamClasses();
                    if(declClasses != null) {
                        if(isNotEquals && declClasses.equals(fDirectClasses))
                            equals.add(decl);
                        mapClasses.put(decl, declClasses);
                    }
                }

                if (!mapClasses.isEmpty()) { // есть прямые наследования
                    Collection<LSFPropDeclaration> result = new ArrayList<LSFPropDeclaration>();

                    if((isNotEquals && equals.size() < mapClasses.size()) || onlyNotEquals) // оставим только не равные 
                        mapClasses = BaseUtils.filterNotKeys(mapClasses, equals);

                    List<LSFPropDeclaration> listMapClasses = new ArrayList<LSFPropDeclaration>(mapClasses.keySet());
                    for (int i = 0, size = listMapClasses.size(); i < size; i++) {
                        LSFPropDeclaration decl = listMapClasses.get(i);
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

    public boolean isImplement() {
        PropertyUsageContext usageContext = getPropertyUsageContext();
        if(usageContext instanceof LSFMappedPropertyClassParamDeclare)
            return usageContext.getParent() instanceof LSFOverrideStatement;
        return false;
    }

    public boolean onlyNotEquals() {
        return false;
    }

    public boolean isDirect() {
        List<LSFClassSet> usageContext = getExplicitClasses();
        if(usageContext == null) {
            usageContext = getUsageContext();
            if(usageContext==null) // невозможно определить классы, подходят все
                return true;
        }

        LSFPropDeclaration decl = resolveDecl();
        assert decl != null; // предполагается что ошибка resolve'а уже отработана

        List<LSFClassSet> declClasses = decl.resolveParamClasses();
        if (declClasses == null)
            return true;

        if (LSFPsiImplUtil.containsAll(declClasses, usageContext, false)) // подходят по классам
            return true;

        return false;
    }

    @Override
    public Condition<LSFGlobalPropDeclaration> getCondition() {
        return BaseUtils.immutableCast(getDirectCondition());
    }

    @Override
    public Condition<LSFGlobalPropDeclaration> getFullCondition() {
        Condition<LSFGlobalPropDeclaration> result = super.getFullCondition();
        if(canBeUsedInDirect())
            result = Conditions.or(result, BaseUtils.<Condition<LSFGlobalPropDeclaration>>immutableCast(getInDirectCondition()));
        return result;
    }

    @Override
    protected Finalizer<LSFGlobalPropDeclaration> getFinalizer() {
        return BaseUtils.immutableCast(getDirectFinalizer());
    }

    private Collection<? extends LSFDeclaration> resolveLocals(Condition<LSFPropDeclaration> condition, Finalizer<LSFGlobalPropDeclaration> finalizer) {
        LocalResolveProcessor processor = new LocalResolveProcessor(getNameRef(), BaseUtils.<Condition<LSFPropDeclaration>>immutableCast(condition));
        PsiTreeUtil.treeWalkUp(processor, this, null, new ResolveState());
        Finalizer<LSFLocalPropDeclaration> castFinalizer = BaseUtils.immutableCast(finalizer);
        return castFinalizer.finalize(processor.found);
    }

    @Override
    public Annotation resolveAmbiguousErrorAnnotation(AnnotationHolder holder, Collection<? extends LSFDeclaration> declarations) {
        String ambError = "Ambiguous reference";

        String description = "";
        int i = 1;
        List<LSFPropDeclaration> decls = new ArrayList<LSFPropDeclaration>((Collection<? extends LSFPropDeclaration>) declarations);
        for (LSFPropDeclaration decl : decls) {
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

    @Override
    public Annotation resolveNotFoundErrorAnnotation(AnnotationHolder holder, Collection<? extends LSFDeclaration> similarDeclarations) {
        String errorText;
        boolean noSuchProperty = similarDeclarations.size() == 0;
        if (similarDeclarations.size() != 1) {
            if (noSuchProperty) {
                errorText = "Property '" + getNameRef() + "' not found";
            } else {
                errorText = "Cannot resolve property " + getNameRef() + listClassesToString(getUsageContext());
            }
        } else {
            errorText = similarDeclarations.iterator().next().getPresentableText() + " cannot be applied to " +
                    getNameRef() + listClassesToString(getUsageContext());
        }

        return resolveErrorTarget(holder, errorText, noSuchProperty);
    }

    public String listClassesToString(List<LSFClassSet> classes) {
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
}
