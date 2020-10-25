package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.LSFIcons;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFActionOrGlobalPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFActionOrPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFExprParamDeclaration;
import com.lsfusion.lang.psi.declarations.LSFParamDeclaration;
import com.lsfusion.lang.psi.references.LSFActionOrPropReference;
import com.lsfusion.lang.psi.stubs.ActionOrPropStubElement;
import com.lsfusion.lang.typeinfer.InferExResult;
import com.lsfusion.lang.typeinfer.LSFExClassSet;
import com.lsfusion.refactoring.ElementMigration;
import com.lsfusion.refactoring.PropertyCanonicalNameUtils;
import com.lsfusion.refactoring.PropertyMigration;
import com.lsfusion.util.BaseUtils;
import com.lsfusion.util.LSFPsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;

public abstract class LSFActionOrGlobalPropDeclarationImpl<Decl extends LSFActionOrGlobalPropDeclaration<Decl, Stub>, Stub extends ActionOrPropStubElement<Stub, Decl>> extends LSFFullNameDeclarationImpl<Decl, Stub> implements LSFActionOrGlobalPropDeclaration<Decl, Stub> {

    public LSFActionOrGlobalPropDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    public LSFActionOrGlobalPropDeclarationImpl(@NotNull Stub stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
    }

    public static List<LSFClassSet> finishParamClasses(LSFActionOrPropDeclaration decl) {
        return LSFExClassSet.fromEx(decl.resolveExParamClasses());
    }

    public static String getParamPresentableText(List<?> paramClasses) {
        String result = "";
        if(paramClasses != null) {
            for (Object paramClass : paramClasses)
                result = (result.isEmpty() ? "" : result + ",") + (paramClass != null ? paramClass.toString() : "?");
        } else
            result = "?";
        return "(" + result + ")";
    }

    public static boolean resolveEquals(List<LSFClassSet> list1, List<LSFClassSet> list2) {
        if (list1 == null && list2 == null) {
            return true;
        }
        if (list1 == null || list2 == null || list1.size() != list2.size()) {
            return false;
        }

        for (int i = 0; i < list1.size(); i++) {
            if (list1.get(i) == null) {
                if (list2.get(i) != null) {
                    return false;
                }
            } else {
                if (!list1.get(i).equals(list2.get(i))) {
                    return false;
                }
            }
        }

        return true;
    }

    public static Icon getIcon(byte propType) {
        switch (propType) {
            case 3:
                return LSFIcons.ACTION;
            case 2:
                return LSFIcons.ABSTRACT_PROPERTY;
            case 1:
                return LSFIcons.DATA_PROPERTY;
            default:
                return LSFIcons.PROPERTY;
        }
    }

    public abstract boolean isUnfriendly();

    @Override
    public boolean isNoParams() {
        Stub stub = getStub();
        if(stub != null)
            return stub.isNoParams();

        LSFExplicitClasses explicitParams = getExplicitParams();
        if(explicitParams instanceof LSFExplicitSignature)
            return ((LSFExplicitSignature) explicitParams).isNoParams();
        return false;
    }

    public abstract LSFPropertyDeclaration getPropertyDeclaration();

    private LSFExplicitSignature getDeclExplicitParams() {
        List<LSFParamDeclaration> params = getPropertyDeclaration().resolveParamDecls();
        if(params != null)
            return LSFPsiImplUtil.getClassNameRefs(params);
        return null;
    }
    
    protected abstract LSFExplicitClasses getImplicitExplicitParams();
    protected abstract List<LSFExClassSet> resolveValueParamClasses(List<LSFParamDeclaration> declareParams);

    public LSFExplicitClasses getExplicitParams() {
        LSFExplicitSignature declParams = getDeclExplicitParams();
        if (declParams != null && declParams.allClassesDeclared()) // оптимизация
            return declParams;

        LSFExplicitClasses implicitParams = getImplicitExplicitParams();
        if(implicitParams == null)
            return declParams;
        if(declParams == null || !(implicitParams instanceof LSFExplicitSignature)) // если группировка забиваем на explicit
            return implicitParams;

        return declParams.merge((LSFExplicitSignature) implicitParams);
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return getIcon(getPropType());
    }

    @Override
    public List<LSFExClassSet> resolveExParamClassesNoCache() {
        LSFPropertyDeclaration decl = getPropertyDeclaration();
        LSFPropertyDeclParams cpd = decl.getPropertyDeclParams();
        List<LSFExClassSet> declareClasses = null;
        if (cpd != null) {
            declareClasses = LSFExClassSet.toEx(LSFPsiImplUtil.resolveExplicitClass(cpd.getClassParamDeclareList()));
            if (LSFPsiUtils.allClassesDeclared(declareClasses)) // оптимизация
                return declareClasses;
        }
        
        List<LSFParamDeclaration> declareParams = null;
        if (cpd != null)
            declareParams = LSFPsiImplUtil.resolveParams(cpd.getClassParamDeclareList());

        List<LSFExClassSet> valueClasses = resolveValueParamClasses(declareParams);
        if (valueClasses == null)
            return declareClasses;

        if (declareClasses == null)
            return valueClasses;

        List<LSFExClassSet> mixed = new ArrayList<>(declareClasses);
        for (int i = 0, size = declareClasses.size(); i < size; i++) {
            if (i >= valueClasses.size())
                break;

            if (declareClasses.get(i) == null)
                mixed.set(i, valueClasses.get(i));
        }
        return Collections.unmodifiableList(mixed);
    }

    @Override
    protected Condition<Decl> getFindDuplicatesCondition() {
        return new Condition<Decl>() {
            @Override
            public boolean value(Decl decl) {
                LSFId nameIdentifier = getNameIdentifier();
                LSFId otherNameIdentifier = decl.getNameIdentifier();
                return nameIdentifier != null && otherNameIdentifier != null &&
                        nameIdentifier.getText().equals(otherNameIdentifier.getText()) &&
                        resolveEquals(resolveParamClasses(), decl.resolveParamClasses());
            }
        };
    }

    @Override
    public String getPresentableText() {
        return getDeclName() + getParamPresentableText();
    }

    @Override
    public PsiElement[] processImplementationsSearch() {
        LSFId nameIdentifier = getNameIdentifier();
        if (nameIdentifier == null) {
            return PsiElement.EMPTY_ARRAY;
        }
        List<PsiElement> result = new ArrayList<>();
        for(LSFActionOrPropReference impl : findImplementations(nameIdentifier))
            result.add(impl.getWrapper());         
        return result.toArray(new PsiElement[result.size()]);
    }

    protected List<LSFActionOrPropReference> findImplementations(LSFId nameIdentifier) {
        Collection<PsiReference> refs = ReferencesSearch.search(nameIdentifier, GlobalSearchScope.allScope(getProject())).findAll();

        List<LSFActionOrPropReference> impls = new ArrayList<>();
        for (PsiReference ref : refs) {
            LSFActionOrPropReference impl = getImplementation(ref);
            if(impl != null)
                impls.add(impl);
        }
        return impls;
    }

    protected abstract LSFActionOrPropReference getImplementation(PsiReference ref);

    @Override
    public String getCaption() {
        LSFLocalizedStringLiteral stringLiteral = getPropertyDeclaration().getSimpleNameWithCaption().getLocalizedStringLiteral();
        return stringLiteral != null ? stringLiteral.getValue() : null;
    }

    @Override
    @Nullable
    public LSFId getNameIdentifier() {
        return getPropertyDeclaration().getSimpleNameWithCaption().getSimpleName();
    }

    @Override
    @Nullable
    public List<LSFExClassSet> inferParamClasses(LSFExClassSet valueClass) {
        List<LSFExClassSet> resultClasses = resolveExParamClasses();
        if (resultClasses == null)
            return null;

        resultClasses = new ArrayList<>(resultClasses);

        //        LSFActionStatement action = sourceStatement.getActionPropertyDefinition();
//        return 

        List<LSFExprParamDeclaration> params = null;
        LSFPropertyDeclaration decl = getPropertyDeclaration();
        LSFPropertyDeclParams cpd = decl.getPropertyDeclParams();
        if (cpd != null)
            params = BaseUtils.<List<LSFExprParamDeclaration>>immutableCast(LSFPsiImplUtil.resolveParams(cpd.getClassParamDeclareList()));
        Result<List<LSFExprParamDeclaration>> rParams = new Result<>(params);

        InferExResult inferredClasses = inferExParamClasses(valueClass, resultClasses, rParams);
        if (inferredClasses != null) {
            assert resultClasses.size() == params.size();
            for (int i = 0; i < resultClasses.size(); i++)
                if (resultClasses.get(i) == null) { // не определены, возьмем выведенные
                    resultClasses.set(i, inferredClasses.get(params.get(i)));
                }
        }

        return resultClasses;
    }

    protected abstract InferExResult inferExParamClasses(LSFExClassSet valueClass, List<LSFExClassSet> resultClasses, Result<List<LSFExprParamDeclaration>> rParams);

    @Override
    public ElementMigration getMigration(String newName) {
        return PropertyMigration.create(this, getGlobalName(), newName);
    }

    public List<String> resolveParamNames() {
        LSFPropertyDeclaration decl = getPropertyDeclaration();
        LSFPropertyDeclParams declList = decl.getPropertyDeclParams();
        List<LSFExprParamDeclaration> params = null;
        if(declList!=null)
            params = BaseUtils.<List<LSFExprParamDeclaration>>immutableCast(LSFPsiImplUtil.resolveParams(declList.getClassParamDeclareList()));
        else
            params = resolveValueParamNames();
        return LSFPsiImplUtil.resolveParamNames(params);
    }
    
    protected List<LSFExprParamDeclaration> resolveValueParamNames() {
        return null;
    }
    
    protected abstract PsiElement getDependenciesBody();
    protected abstract void fillImplementationDependencies(LSFActionOrPropReference impRef, Collection<LSFActionOrPropReference> references);
    @Override
    public Set<LSFActionOrGlobalPropDeclaration> getDependencies() {
        Set<LSFActionOrGlobalPropDeclaration> result = new HashSet<>();

        Collection<LSFActionOrPropReference> propReferences;

        if (isAbstract()) {
            propReferences = new ArrayList<>();
            for (LSFActionOrPropReference reference : findImplementations(getNameIdentifier())) {
                fillImplementationDependencies(reference, propReferences);
            }
        } else {
            propReferences = PsiTreeUtil.findChildrenOfType(getDependenciesBody(), LSFActionOrPropReference.class);
        }

        for (LSFActionOrPropReference<?, ?> propReference : propReferences) {
            LSFActionOrPropDeclaration propDeclaration = propReference.resolveDecl();
            if (propDeclaration != null && propDeclaration instanceof LSFActionOrGlobalPropDeclaration) {
                result.add((LSFActionOrGlobalPropDeclaration) propDeclaration);
            }
        }

        return result;
    }

    @Override
    public String getCanonicalName() {
        return PropertyCanonicalNameUtils.createName(getNamespaceName(), getGlobalName(), resolveParamClasses());
    }
}
