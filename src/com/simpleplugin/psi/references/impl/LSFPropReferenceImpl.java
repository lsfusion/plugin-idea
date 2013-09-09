package com.simpleplugin.psi.references.impl;

import com.intellij.lang.ASTNode;
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
import com.simpleplugin.classes.LSFClassSet;
import com.simpleplugin.psi.Finalizer;
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
}
