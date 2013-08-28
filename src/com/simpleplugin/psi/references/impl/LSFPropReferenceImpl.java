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
import com.simpleplugin.LSFPsiImplUtil;
import com.simpleplugin.classes.LSFClassSet;
import com.simpleplugin.psi.context.PropertyUsageContext;
import com.simpleplugin.psi.declarations.LSFGlobalPropDeclaration;
import com.simpleplugin.psi.declarations.LSFLocalPropDeclaration;
import com.simpleplugin.psi.declarations.LSFPropDeclaration;
import com.simpleplugin.psi.references.LSFPropReference;
import com.simpleplugin.psi.stubs.types.FullNameStubElementType;
import com.simpleplugin.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class LSFPropReferenceImpl extends LSFFullNameReferenceImpl<LSFPropDeclaration, LSFGlobalPropDeclaration> implements LSFPropReference {

    public LSFPropReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    protected FullNameStubElementType<?, LSFGlobalPropDeclaration> getType() {
        return LSFStubElementTypes.PROP;
    }

    private static class LocalResolveProcessor implements PsiScopeProcessor {
        
        private final String name;
        private LSFLocalPropDeclaration found;
        private final Condition<LSFPropDeclaration> condition; 

        private LocalResolveProcessor(String name, Condition<LSFPropDeclaration> condition) {
            this.name = name;
            this.condition = condition;
        }

        @Override
        public boolean execute(@NotNull PsiElement element, ResolveState state) {
            LSFLocalPropDeclaration decl = (LSFLocalPropDeclaration) element;
            if(decl.getName().equals(name) && condition.value(decl)) {
                found = decl;
                return false;
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
    public Query<LSFPropDeclaration> resolveNoCache() {
        if(getFullNameRef() == null) {
            LocalResolveProcessor processor = new LocalResolveProcessor(getNameRef(), getDeclCondition());
            PsiTreeUtil.treeWalkUp(processor, this, null, new ResolveState());
            if(processor.found!=null)
                return new CollectionQuery<LSFPropDeclaration>(Collections.<LSFPropDeclaration>singleton(processor.found));
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

    private List<LSFClassSet> getUsageContext() {
        return PsiTreeUtil.getParentOfType(this, PropertyUsageContext.class).resolveParamClasses();        
    }
    
    private Condition<LSFPropDeclaration> getDeclCondition() {
        final List<LSFClassSet> usageClasses = getUsageContext();
        return new Condition<LSFPropDeclaration>() {
            public boolean value(LSFPropDeclaration decl) {
                List<LSFClassSet> declClasses = decl.resolveParamClasses();
                if(declClasses.size()!=usageClasses.size())
                    return false;
                
                for(int i=0,size=declClasses.size();i<size;i++)
                    if(!LSFPsiImplUtil.containsAll(declClasses.get(i), usageClasses.get(i)))
                        return false;
                return true;
            }
        };
    }
            
    @Override
    protected Condition<LSFGlobalPropDeclaration> getCondition() {
        return (Condition<LSFGlobalPropDeclaration>)((Condition<? extends LSFPropDeclaration>)getDeclCondition());
    }
}
