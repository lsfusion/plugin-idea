package com.simpleplugin.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.CollectionQuery;
import com.intellij.util.Query;
import com.simpleplugin.BaseUtils;
import com.simpleplugin.LSFPsiImplUtil;
import com.simpleplugin.LSFReferenceAnnotator;
import com.simpleplugin.classes.LSFClassSet;
import com.simpleplugin.psi.Finalizer;
import com.simpleplugin.psi.LSFGlobalResolver;
import com.simpleplugin.psi.context.PropertyUsageContext;
import com.simpleplugin.psi.declarations.LSFGlobalPropDeclaration;
import com.simpleplugin.psi.declarations.LSFLocalPropDeclaration;
import com.simpleplugin.psi.declarations.LSFPropDeclaration;
import com.simpleplugin.psi.references.LSFPropReference;
import com.simpleplugin.psi.stubs.types.FullNameStubElementType;
import com.simpleplugin.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class LSFPropReferenceImpl extends LSFFullNameReferenceImpl<LSFPropDeclaration, LSFGlobalPropDeclaration> implements LSFPropReference {

    public LSFPropReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    protected FullNameStubElementType<?, LSFGlobalPropDeclaration> getType() {
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
            if(decl.getName().equals(name) && condition.value(decl))
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
    public Query<LSFPropDeclaration> resolveNoCache() {
        if(getFullNameRef() == null) {
            LocalResolveProcessor processor = new LocalResolveProcessor(getNameRef(), getDeclCondition());
            PsiTreeUtil.treeWalkUp(processor, this, null, new ResolveState());
            if(processor.found.size() > 0) {
                Finalizer<LSFLocalPropDeclaration> finalizer = BaseUtils.immutableCast(getDeclFinalizer());
                return new CollectionQuery<LSFPropDeclaration>(BaseUtils.<LSFPropDeclaration, LSFLocalPropDeclaration>immutableCast(finalizer.finalize(processor.found)));
            }
        }                       
        return super.resolveNoCache();
    }

    private static class VariantsProcessor implements PsiScopeProcessor {

        private Collection<String> found;

        private VariantsProcessor(Collection<String> found) {
            this.found = found;
        }

        @Override
        public boolean execute(@NotNull PsiElement element, ResolveState state) {
            LSFLocalPropDeclaration decl = (LSFLocalPropDeclaration) element;
            found.add(decl.getDeclName());
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
    protected void fillListVariants(Collection<String> variants) {
        if(getFullNameRef() == null) {
            VariantsProcessor processor = new VariantsProcessor(variants);
            PsiTreeUtil.treeWalkUp(processor, this, null, new ResolveState());
        }
        super.fillListVariants(variants);
    }

    @Nullable
    private List<LSFClassSet> getUsageContext() {
        return PsiTreeUtil.getParentOfType(this, PropertyUsageContext.class).resolveParamClasses();        
    }
    
    private PropertyUsageContext getPropertyUsageContext() {
        return PsiTreeUtil.getParentOfType(this, PropertyUsageContext.class);
    }
    
    private Finalizer<LSFPropDeclaration> getDeclFinalizer() {
        return new Finalizer<LSFPropDeclaration>() {
            public Collection<LSFPropDeclaration> finalize(Collection<LSFPropDeclaration> decls) {
                final List<LSFClassSet> usageClasses = getUsageContext();
                if(usageClasses==null) // невозможно определить прямое или обратное использование, соответственно непонятно как "экранировать"
                    return decls;

                Map<LSFPropDeclaration, List<LSFClassSet>> mapClasses = new HashMap<LSFPropDeclaration, List<LSFClassSet>>();
                for(LSFPropDeclaration decl : decls) {
                    List<LSFClassSet> declClasses = decl.resolveParamClasses();
                    if(declClasses != null) {
                        if(LSFPsiImplUtil.containsAll(declClasses, usageClasses, true)) // подходят по классам
                            mapClasses.put(decl, declClasses);
                    }
                }
                
                if(!mapClasses.isEmpty()) { // есть прямые наследования
                    Collection<LSFPropDeclaration> result = new ArrayList<LSFPropDeclaration>();
                    
                    List<LSFPropDeclaration> listMapClasses = new ArrayList<LSFPropDeclaration>(mapClasses.keySet()); 
                    for(int i=0,size=listMapClasses.size();i<size;i++) {
                        LSFPropDeclaration decl = listMapClasses.get(i);
                        List<LSFClassSet> classesI = mapClasses.get(decl);
                        boolean found = false;
                        for(int j=i+1;j<size;j++) {
                            List<LSFClassSet> classesJ = mapClasses.get(listMapClasses.get(j));
                            if(LSFPsiImplUtil.containsAll(classesI, classesJ, true)) {
                                found = true;
                                break;
                            }
                        }
                        if(!found)
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
        if(usageClasses == null)
            return Condition.TRUE;

        return new Condition<LSFPropDeclaration>() {
            public boolean value(LSFPropDeclaration decl) {
                List<LSFClassSet> declClasses = decl.resolveParamClasses();
                if(declClasses == null)
                    return true;
                
                if(declClasses.size()!=usageClasses.size())
                    return false;
                
                return LSFPsiImplUtil.haveCommonChilds(declClasses, usageClasses);
            }
        };
    }

    private Map<LSFPropDeclaration, Integer> getDeclarations() {
        final List<LSFClassSet> usageClasses = getUsageContext();
        if(usageClasses == null)
            return null;
        
        Map<LSFPropDeclaration, Integer> result = new HashMap<LSFPropDeclaration, Integer>();

        CollectionQuery<LSFPropDeclaration> declarations = new CollectionQuery<LSFPropDeclaration >(LSFGlobalResolver.findElements(getNameRef(), getFullNameRef(), getLSFFile(), getTypes(), Condition.TRUE, Finalizer.EMPTY));

        for (Iterator<LSFPropDeclaration> iterator = declarations.iterator(); iterator.hasNext();) {
            LSFPropDeclaration decl = iterator.next();
            List<LSFClassSet> declClasses = decl.resolveParamClasses();
            if(declClasses == null) {
                result.put(decl, 0);
                continue;
            }

            result.put(decl, LSFPsiImplUtil.getCommonChildrenCount(declClasses, usageClasses));    
        }
        
        return result;
    }
    
    public boolean isDirect() {
        List<LSFClassSet> usageContext = getUsageContext();
        if(usageContext == null)
            return true;

        LSFPropDeclaration decl = resolveDecl();
        assert decl != null; // предполагается что ошибка resolve'а уже отработана

        List<LSFClassSet> declClasses = decl.resolveParamClasses();
        if(declClasses == null)
            return true;
        
        if(LSFPsiImplUtil.containsAll(declClasses, usageContext, false)) // подходят по классам
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
    public Annotation resolveNotFoundErrorAnnotation(AnnotationHolder holder) {
        Map<LSFPropDeclaration, Integer> declarations = getDeclarations();
        if (declarations == null) {
            return null;
        }
        
        int commonClasses = 0;
        List<LSFPropDeclaration> decls = new ArrayList<LSFPropDeclaration>();
        for (Map.Entry<LSFPropDeclaration, Integer> entry : declarations.entrySet()) {
            if (entry.getValue() > commonClasses) {
                commonClasses = entry.getValue();
                decls = new ArrayList<LSFPropDeclaration>();
                decls.add(entry.getKey());
            } else if (entry.getValue() == commonClasses) {
                decls.add(entry.getKey());
            }
        }
        
        if (decls.size() != 1) {
            return holder.createErrorAnnotation(getTextRange(), "Cannot resolve property reference");  
        } else {
            Annotation error = holder.createErrorAnnotation(getTextRange(), listClassesToString(decls.get(0).resolveParamClasses()) + " cannot be applied to " + listClassesToString(getUsageContext()));
            error.setEnforcedTextAttributes(LSFReferenceAnnotator.WAVE_UNDERSCORED_ERROR);
            return error;
        }
    }
    
    private String listClassesToString(List<LSFClassSet> classes) {
        String result = "(";
        for (Iterator<LSFClassSet> iterator = classes.iterator(); iterator.hasNext();) {
            LSFClassSet set = iterator.next();
            result += set == null ? "?" : set;
            result += iterator.hasNext() ? ", " : "";
        }
        return result += ")";
    }
}
