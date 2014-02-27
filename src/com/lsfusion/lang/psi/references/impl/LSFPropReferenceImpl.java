package com.lsfusion.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.CollectionQuery;
import com.lsfusion.util.BaseUtils;
import com.lsfusion.psi.LSFResolveResult;
import com.lsfusion.psi.LSFPsiImplUtil;
import com.lsfusion.LSFReferenceAnnotator;
import com.lsfusion.classes.LSFClassSet;
import com.lsfusion.psi.*;
import com.lsfusion.psi.context.PropertyUsageContext;
import com.lsfusion.psi.declarations.LSFDeclaration;
import com.lsfusion.psi.declarations.LSFGlobalPropDeclaration;
import com.lsfusion.psi.declarations.LSFLocalPropDeclaration;
import com.lsfusion.psi.declarations.LSFPropDeclaration;
import com.lsfusion.psi.references.LSFPropReference;
import com.lsfusion.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.psi.stubs.types.LSFStubElementTypes;
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
            LSFLocalPropDeclaration decl = (LSFLocalPropDeclaration) element;
            if (decl.getName().equals(name) && condition.value(decl))
                found.add(decl);
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
        Collection<? extends LSFDeclaration> declarations = null;

        if (getFullNameRef() == null) {
            LocalResolveProcessor processor = new LocalResolveProcessor(getNameRef(), getDeclCondition());
            PsiTreeUtil.treeWalkUp(processor, this, null, new ResolveState());
            if (processor.found.size() > 0) {
                Finalizer<LSFLocalPropDeclaration> finalizer = BaseUtils.immutableCast(getDeclFinalizer());
                declarations = finalizer.finalize(processor.found);
            }
        }
        if (declarations == null) {
            declarations = super.resolveNoCache().declarations;
        }

        LSFResolveResult.ErrorAnnotator errorAnnotator = null;
        if (declarations.size() > 1) {
            final Collection<? extends LSFDeclaration> finalDeclarations = declarations;
            errorAnnotator = new LSFResolveResult.ErrorAnnotator() {
                @Override
                public Annotation resolveErrorAnnotation(AnnotationHolder holder) {
                    return resolveAmbiguousErrorAnnotation(holder, finalDeclarations);
                }
            };
        } else if (declarations.isEmpty()) {
            declarations = resolveNoConditionDeclarations();

            final Collection<? extends LSFDeclaration> finalDeclarations = declarations;
            errorAnnotator = new LSFResolveResult.ErrorAnnotator() {
                @Override
                public Annotation resolveErrorAnnotation(AnnotationHolder holder) {
                    return resolveNotFoundErrorAnnotation(holder, finalDeclarations);
                }
            };
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

    @Nullable
    private List<LSFClassSet> getUsageContext() {
        PropertyUsageContext propertyUsageContext = getPropertyUsageContext();
        return propertyUsageContext == null ? null : propertyUsageContext.resolveParamClasses();
    }

    protected abstract LSFNonEmptyExplicitPropClassList getNonEmptyExplicitPropClassList();

    @Nullable
    private List<LSFClassSet> getExplicitClasses() {
        LSFNonEmptyExplicitPropClassList neList = getNonEmptyExplicitPropClassList();
        if (neList == null)
            return null;
        List<LSFClassSet> result = new ArrayList<LSFClassSet>();
        for (LSFExplicitPropClass explPropClass : neList.getExplicitPropClassList()) {
            LSFClassName className = explPropClass.getClassName();
            if (className != null)
                result.add(LSFPsiImplUtil.resolveClass(className));
            else
                result.add(null);
        }
        return result;
    }

    private Finalizer<LSFPropDeclaration> getDeclFinalizer() {
        return new Finalizer<LSFPropDeclaration>() {
            public Collection<LSFPropDeclaration> finalize(Collection<LSFPropDeclaration> decls) {
                final List<LSFClassSet> usageClasses = getUsageContext();
                if (usageClasses == null) // невозможно определить прямое или обратное использование, соответственно непонятно как "экранировать"
                    return decls;

                Map<LSFPropDeclaration, List<LSFClassSet>> mapClasses = new HashMap<LSFPropDeclaration, List<LSFClassSet>>();
                for (LSFPropDeclaration decl : decls) {
                    List<LSFClassSet> declClasses = decl.resolveParamClasses();
                    if (declClasses != null) {
                        if (LSFPsiImplUtil.containsAll(declClasses, usageClasses, true)) // подходят по классам
                            mapClasses.put(decl, declClasses);
                    }
                }

                if (!mapClasses.isEmpty()) { // есть прямые наследования
                    Collection<LSFPropDeclaration> result = new ArrayList<LSFPropDeclaration>();

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

    private Condition<LSFPropDeclaration> getDeclCondition() {
        final List<LSFClassSet> usageClasses = getUsageContext();
        final List<LSFClassSet> explicitClasses = getExplicitClasses();

        if (usageClasses == null && explicitClasses == null)
            return Condition.TRUE;

        return new Condition<LSFPropDeclaration>() {
            public boolean value(LSFPropDeclaration decl) {
                List<LSFClassSet> declClasses = decl.resolveParamClasses();
                if (declClasses == null)
                    return true;

                if (explicitClasses != null) {
                    if (declClasses.size() != explicitClasses.size())
                        return false;

                    if (!LSFPsiImplUtil.containsAll(declClasses, explicitClasses, false)) // подходят по классам
                        return false;
                }

                if (usageClasses == null)
                    return true;

                if (declClasses.size() != usageClasses.size())
                    return false;

                return LSFPsiImplUtil.haveCommonChilds(declClasses, usageClasses, GlobalSearchScope.allScope(getProject()));
            }
        };
    }

    public boolean isDirect() {
        List<LSFClassSet> usageContext = getUsageContext();
        if (usageContext == null)
            return true;

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
    protected Condition<LSFGlobalPropDeclaration> getCondition() {
        return BaseUtils.immutableCast(getDeclCondition());
    }

    @Override
    protected Finalizer<LSFGlobalPropDeclaration> getFinalizer() {
        return BaseUtils.immutableCast(getDeclFinalizer());
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
