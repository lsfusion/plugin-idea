package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.DumbService;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.context.ModifyParamContext;
import com.lsfusion.lang.psi.declarations.LSFActionOrGlobalPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFAggrParamGlobalPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFGlobalPropDeclaration;
import com.lsfusion.lang.psi.stubs.AggrParamPropStubElement;
import com.lsfusion.lang.typeinfer.LSFExClassSet;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collections;
import java.util.List;
import java.util.Set;

// здесь не надо городить огород со стабами так как тут все явно и можно использовать одну реализацию как для heavy так и для light
public abstract class LSFAggrParamGlobalPropDeclarationImpl extends LSFFullNameDeclarationImpl<LSFAggrParamGlobalPropDeclaration, AggrParamPropStubElement> implements LSFAggrParamGlobalPropDeclaration {

    public LSFAggrParamGlobalPropDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    public LSFAggrParamGlobalPropDeclarationImpl(@NotNull AggrParamPropStubElement propStubElement, @NotNull IStubElementType nodeType) {
        super(propStubElement, nodeType);
    }

    @Override
    public boolean isStoredProperty() {
        return true;
    }

    @Override
    public boolean isMaterializedProperty() {
        return true;
    }

    @Override
    public boolean isDataProperty() {
        return true;
    }

    @Override
    public boolean isDataStoredProperty() {
        return true;
    }

    @NotNull
    protected abstract LSFClassName getClassName();
    protected abstract LSFParamDeclare getParamDeclare();
    
    @Override
    public LSFExClassSet resolveExValueClassNoCache(boolean infer) {
        return LSFExClassSet.toEx(LSFPsiImplUtil.resolveClass(getClassName()));
    }
    
    @Nullable
    @Override
    public LSFNonEmptyPropertyOptions getNonEmptyPropertyOptions() {
        return null;
    }

    @Override
    public boolean isAbstract() {
        return false;
    }

    public Set<String> getExplicitValues() {
        AggrParamPropStubElement stub = getStub();
        if(stub != null) {
            return stub.getParamExplicitValues();
        }
        
        return Collections.singleton(LSFPsiImplUtil.getClassName(getClassName()));
    }

    @Override
    public String getValuePresentableText() {
        Set<String> paramExplicitValues = getExplicitValues();
        String valueText = "?";
        if(paramExplicitValues != null) {
            valueText = StringUtils.join(paramExplicitValues,",");
        }
        return ": " + valueText;
    }

    @Override
    public String getPresentableText() {
        return DumbService.getInstance(getProject()).runReadActionInSmartMode(() -> getDeclName() + getParamPresentableText());
    }

    @Override
    public String getParamPresentableText() {
        LSFExplicitClasses paramExplicitClasses = getExplicitParams();
        if(paramExplicitClasses instanceof LSFExplicitSignature)
            return LSFStringClassRef.getParamPresentableText(((LSFExplicitSignature)paramExplicitClasses).signature);

        // super
        return LSFActionOrGlobalPropDeclarationImpl.getParamPresentableText(resolveParamClasses());    
    }        

    @Override
    public boolean isAction() {
        return false;
    }

    @Override
    public String getCaption() {
        return null;
    }

    @Override
    public Icon getIcon(int flags) {
        return DumbService.getInstance(getProject()).runReadActionInSmartMode(() -> LSFActionOrGlobalPropDeclarationImpl.getIcon(getPropType()));
    }

    @Override
    public LSFExplicitClasses getExplicitParams() {
        AggrParamPropStubElement stub = getStub();
        if(stub != null)
            return stub.getParamExplicitClasses();

        LSFAggrPropertyDefinition aggrProp = getAggrPropertyDefinition();
        if(aggrProp != null) {
            LSFCustomClassUsage customClassUsage = aggrProp.getCustomClassUsage();
            if(customClassUsage != null)
                return new LSFExplicitSignature(Collections.singletonList(LSFPsiImplUtil.getClassNameRef(customClassUsage)));
        }
        return null;
    }

    @Override
    public Set<LSFActionOrGlobalPropDeclaration<?, ?>> getDependencies() {
        return Collections.EMPTY_SET;
    }
    
    public LSFAggrPropertyDefinition getAggrPropertyDefinition() {
        ModifyParamContext paramContext = PsiTreeUtil.getParentOfType(this, ModifyParamContext.class);
        if(paramContext instanceof LSFPropertyStatement) {
            LSFExpressionUnfriendlyPD expressionUnfriendlyPD = ((LSFPropertyStatement)paramContext).getPropertyCalcStatement().getExpressionUnfriendlyPD();
            if(expressionUnfriendlyPD != null)
                return expressionUnfriendlyPD.getAggrPropertyDefinition();
        }
        return null;
    }

    @Override
    public List<LSFClassSet> resolveParamClasses() {
        LSFExplicitClasses explicitParams = getExplicitParams();
        if(explicitParams instanceof LSFExplicitSignature) {
            return LSFStringClassRef.resolve(((LSFExplicitSignature)explicitParams).signature, getLSFFile());
        }

        return LSFExClassSet.fromEx(resolveExParamClasses());
    }

//    @Override
//    public LSFClassSet resolveValueClass() {
//        dffd
//    }

    @Override
    public List<LSFExClassSet> resolveExParamClassesNoCache() {
        LSFAggrPropertyDefinition aggrProp = getAggrPropertyDefinition();
        if(aggrProp != null)
            return Collections.singletonList(LSFExClassSet.toEx(LSFPsiImplUtil.resolveClass(aggrProp.getCustomClassUsage())));
        return null;
    }
    
    @Nullable
    @Override
    public List<LSFExClassSet> inferParamClasses(LSFExClassSet valueClass) {
        return resolveExParamClasses();
    }

    @Override
    public boolean isNoParams() {
        return false;
    }

    @Override
    public LSFGlobalPropDeclaration getDeclaration() {
        return this;
    }

    @Nullable
    @Override
    public LSFId getNameIdentifier() {
        return getParamDeclare().getNameIdentifier();
    }
}
