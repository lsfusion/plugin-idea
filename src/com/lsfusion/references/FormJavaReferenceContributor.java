package com.lsfusion.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiElementProcessor;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.lsfusion.lang.LSFFileType;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.lang.psi.Result;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import com.lsfusion.util.LSFFileUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

import static com.intellij.psi.search.GlobalSearchScope.fileScope;
import static com.lsfusion.util.JavaPsiUtils.hasSuperClass;
import static com.lsfusion.util.JavaPsiUtils.isClass;
import static com.lsfusion.util.LSFFileUtils.getModuleWithDependenciesScope;

//Search for usages in getLP, etc..
//    + lsfusion.server.logics.scripted.ScriptingActionProperty#getClass, getLCP, getLAP
//    * lsfusion.server.logics.LogicsModule. getClassByName, getLCPByOldName, getLAPByOldName
//        + BL.getModule("...").getClassByName()
//        + ScriptingActionProperty.LM.findClassByCompoundName
//        + constructor of concreate ScriptingActionProperty
//        + RomanBL.Utils.getClassByName('SomeClass'), where Utils = addModuleFromResource("scripts/utils/Utils.lsf")
//        + EquipmentServer.equLM.getClassByName('SomeClass'), where equLM = BL.getModule("ModuleName")
//        - someConcreateClassModule.getClassByName (i.e. AuthenticationLogicsModule LM ..... LM.getLCP)
//        - this.getClassByName
//    * lsfusion.server.logics.scripted.ScriptingLogicsModule#findClassByCompoundName, findLCPByCompoundOldName
//        + ScriptingActionProperty.LM.findClassByCompoundName
//        + constructor of concreate ScriptingActionProperty
//        + EquipmentServer.equLM.findClassByCompoundName('SomeClass'), where equLM = BL.getModule("ModuleName")
//        - someConcreateClassModule.getClassByName (i.e. AuthenticationLogicsModule LM ..... LM.getLCP)
//        - this.getClassByName

public class FormJavaReferenceContributor extends PsiReferenceContributor {

    public static final String SCRIPTING_ACTION_PROPERTY_FQN = "lsfusion.server.logics.scripted.ScriptingActionProperty";
    public static final String LOGICS_MODULE_FQN = "lsfusion.server.logics.LogicsModule";
    public static final String SCRIPTING_LOGICS_MODULE_FQN = "lsfusion.server.logics.scripted.ScriptingLogicsModule";

    public static final String SCRIPTING_ACTION_PROPERTY_LM_FIELD = "LM";

    public static final String GET_MODULE = "getModule";
    public static final String ADD_MODULE_FROM_RESOURCE = "addModuleFromResource";

    public static final String GET_LCP = "getLCP";
    public static final String GET_LAP = "getLAP";
    public static final String GET_CLASS = "getClass";

    public static final String GET_LCPBY_OLD_NAME = "getLCPByOldName";
    public static final String GET_LAPBY_OLD_NAME = "getLAPByOldName";
    public static final String GET_CLASS_BY_NAME = "getClassByName";

    public static final String FIND_LCPBY_COMPOUND_OLD_NAME = "findLCPByCompoundOldName";
    public static final String FIND_LAPBY_COMPOUND_OLD_NAME = "findLAPByCompoundOldName";
    public static final String FIND_CLASS_BY_COMPOUND_NAME = "findClassByCompoundName";

    @Override
    public void registerReferenceProviders(PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(
                PlatformPatterns.psiElement(PsiLiteralExpression.class),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                        PsiLiteralExpression literalExpression = (PsiLiteralExpression) element;
                        Object value = literalExpression.getValue();
                        if (value instanceof String) {
                            String text = (String) value;

                            PsiMethodCallExpression methodCall = PsiTreeUtil.getParentOfType(literalExpression, PsiMethodCallExpression.class);
                            if (methodCall != null) {
                                PsiReferenceExpression methodExpression = methodCall.getMethodExpression();
                                
                                PsiReference[] psiReferences = resolveModuleRefs(methodExpression, element, text);
                                if (psiReferences != null) {
                                    return psiReferences;
                                }

                                psiReferences = resolveLogicsModuleMethodsRefs(methodExpression, element, text);
                                if (psiReferences != null) {
                                    return psiReferences;
                                }

                                psiReferences = resolveScriptingLogicsModuleMethodsRefs(methodExpression, element, text);
                                if (psiReferences != null) {
                                    return psiReferences;
                                }

                                psiReferences = resolveScriptingActionPropertyMethodsRefs(methodExpression, element, text);
                                if (psiReferences != null) {
                                    return psiReferences;
                                }
                            }
                        }
                        return new PsiReference[0];
                    }
                }
        );
    }
    
    private PsiReference[] resolveModuleRefs(PsiReferenceExpression methodExpression, PsiElement element, String elementText) {
        String methodName = methodExpression.getReferenceName();
        if (isOneOfStrings(methodName, GET_MODULE)) {
            return new PsiReference[]{new FromJavaModuleReference(element, new TextRange(1, elementText.length() + 1))};
        }
        return null;
    }

    private PsiReference[] resolveLogicsModuleMethodsRefs(PsiReferenceExpression methodExpression, PsiElement element, String elementText) {
        String methodName = methodExpression.getReferenceName();
        if (isOneOfStrings(methodName, GET_LCPBY_OLD_NAME, GET_LAPBY_OLD_NAME, GET_CLASS_BY_NAME)) {
            boolean classRef = GET_CLASS_BY_NAME.equals(methodName);
            return resolveModuleMethodsRef(methodExpression, element, elementText, classRef, true);
        }
        return null;
    }

    private PsiReference[] resolveScriptingLogicsModuleMethodsRefs(PsiReferenceExpression methodExpression, PsiElement element, String elementText) {
        String methodName = methodExpression.getReferenceName();
        if (isOneOfStrings(methodName, FIND_LCPBY_COMPOUND_OLD_NAME, FIND_LAPBY_COMPOUND_OLD_NAME, FIND_CLASS_BY_COMPOUND_NAME)) {
            boolean classRef = FIND_CLASS_BY_COMPOUND_NAME.equals(methodName);
            return resolveModuleMethodsRef(methodExpression, element, elementText, classRef, false);
        }
        return null;
    }

    private PsiReference[] resolveModuleMethodsRef(PsiReferenceExpression methodExpression, PsiElement element, String elementText, boolean classRef, boolean onlyModule) {
        if (hasScriptingActionPropertyLMQualifier(methodExpression)) {
            return referenceFromScriptingActionProperty(methodExpression, element, elementText, classRef, onlyModule);
        }
        String moduleName = getModuleName(methodExpression);
        return javaOrPropertyReference(element, elementText, classRef, moduleName, onlyModule);
    }

    private String getModuleName(PsiReferenceExpression methodExpression) {
        //проверяем на ссылку .getModule("ModuleName")
        PsiExpression qualifierExpression = methodExpression.getQualifierExpression();
        if (qualifierExpression instanceof PsiMethodCallExpression) {
            return extractModuleNameFromPossibleGetModuleCall((PsiMethodCallExpression) qualifierExpression);
        } else if (qualifierExpression instanceof PsiReferenceExpression) {
            PsiElement lmRef = ((PsiReferenceExpression) qualifierExpression).resolve();
            if (lmRef instanceof PsiVariable) {
                //ссылка на переменную, ищем инициализацию этой перменной одним их двух способобов:
                //  someLM = addModuleFromResource("scripts/path/Some.lsf")
                //  someLM = BL.getModule("Some")

                PsiVariable lmVar = (PsiVariable) lmRef;
                //todo: check type of lmVar
                
                GlobalSearchScope fileScope = fileScope(lmVar.getContainingFile());
                Collection<PsiReference> refs = ReferencesSearch.search(lmVar, fileScope, false).findAll();
                for (PsiReference ref : refs) {
                    PsiElement refElement = ref.getElement();
                    PsiAssignmentExpression assignmentExpression = PsiTreeUtil.getParentOfType(refElement, PsiAssignmentExpression.class);
                    if (assignmentExpression != null && assignmentExpression.getLExpression() == refElement) {
                        PsiExpression rExpression = assignmentExpression.getRExpression();
                        if (rExpression instanceof PsiMethodCallExpression) {
                            PsiMethodCallExpression methodCall = (PsiMethodCallExpression) rExpression;
                            String methodName = methodCall.getMethodExpression().getReferenceName();

                            if (isOneOfStrings(methodName, ADD_MODULE_FROM_RESOURCE)) {
                                //  someLM = addModuleFromResource("scripts/path/Some.lsf")
                                PsiExpression[] methodArgs = methodCall.getArgumentList().getExpressions();
                                if (methodArgs.length == 1) {
                                    PsiExpression expr = methodArgs[0];
                                    if (expr instanceof PsiLiteralExpression) {
                                        Object argValue = ((PsiLiteralExpression) expr).getValue();
                                        if (argValue instanceof String) {
                                            List<PsiFile> files = LSFFileUtils.findFilesByPath(refElement, (String) argValue);
                                            for (PsiFile file : files) {
                                                if (file instanceof LSFFile) {
                                                    LSFFile lsfFile = (LSFFile) file;
                                                    LSFModuleDeclaration moduleDecl = lsfFile.getModuleDeclaration();
                                                    if (moduleDecl != null) {
                                                        return moduleDecl.getName();
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                return "1_nonexistent_name";
                            }
                        }

                        //  someLM = some.long().qualifiers(var).Line.getModule("Some")
                        final Result<String> moduleName = new Result<String>();
                        PsiTreeUtil.processElements(rExpression, new PsiElementProcessor() {
                            @Override
                            public boolean execute(@NotNull PsiElement element) {
                                if (element instanceof PsiMethodCallExpression) {
                                    String name = extractModuleNameFromPossibleGetModuleCall((PsiMethodCallExpression) element);
                                    if (name != null) {
                                        moduleName.setResult(name);
                                        return false;
                                    }
                                }
                                return true;
                            }
                        });
                        return moduleName.getResult();
                    }
                }
            }
        }
        //todo: if qualifierExpression == null or refToConcreateClassModule(i.e. EmailLogicsModule) => search for file path in constructor...
        
        return null;
    }

    private String extractModuleNameFromPossibleGetModuleCall(PsiMethodCallExpression getModuleCall) {
        if (isOneOfStrings(getModuleCall.getMethodExpression().getReferenceName(), GET_MODULE)) {
            PsiExpression[] exprs = getModuleCall.getArgumentList().getExpressions();
            if (exprs.length == 1 && exprs[0] instanceof PsiLiteralExpression) {
                PsiLiteralExpression psiLiteral = (PsiLiteralExpression) exprs[0];
                Object value = psiLiteral.getValue();
                if (value instanceof String) {
                    return (String) value;
                }
            }
        }
        return null;
    }

    //является ли вызовом метода у поля ScriptingActionProperty.LM либо у параметра конструктора ScriptingActionProperty
    private boolean hasScriptingActionPropertyLMQualifier(PsiReferenceExpression methodExpression) {
        PsiExpression qualifierExpression = methodExpression.getQualifierExpression();
        if (qualifierExpression instanceof PsiReferenceExpression) {
            PsiReferenceExpression referenceExpression = (PsiReferenceExpression) qualifierExpression;

            String refName = referenceExpression.getReferenceName();

            PsiElement lmRef = referenceExpression.resolve();

            //проверяем ссылку на ScriptingActionProperty.LM
            if (SCRIPTING_ACTION_PROPERTY_LM_FIELD.equals(refName)) {
                if (lmRef instanceof PsiField) {
                    PsiField lmField = (PsiField) lmRef;
                    PsiClass clazz = lmField.getContainingClass();
                    if (isClass(clazz, SCRIPTING_ACTION_PROPERTY_FQN)) {
                        return true;
                    }
                }
            }

            //проверяем ссыклку на параметр конструктора
            if (lmRef instanceof PsiParameter) {
                PsiParameter lmParam = (PsiParameter) lmRef;
                PsiElement declarationScope = lmParam.getDeclarationScope();
                if (declarationScope instanceof PsiMethod) {
                    PsiMethod declarationMethod = (PsiMethod) declarationScope;
                    if (declarationMethod.isConstructor()) {
                        PsiType type = lmParam.getType();
                        if (type instanceof PsiClassType && hasSuperClass(((PsiClassType) type).resolve(), SCRIPTING_LOGICS_MODULE_FQN)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private PsiReference[] resolveScriptingActionPropertyMethodsRefs(PsiReferenceExpression methodExpression, PsiElement element, String elementText) {
        String methodName = methodExpression.getReferenceName();
        if (isOneOfStrings(methodName, GET_LAP, GET_LCP, GET_CLASS)) {
            PsiElement qualifier = methodExpression.getQualifier();
            if (qualifier == null) {
                return referenceFromScriptingActionProperty(methodExpression, element, elementText, GET_CLASS.equals(methodName), false);
            }
        }
        return null;
    }

    private PsiReference[] referenceFromScriptingActionProperty(PsiReferenceExpression methodExpression, PsiElement element, String elementText, boolean classRef, boolean onlyModule) {
        //прямой вызов getLAP, getLCP => ищем модуль, в котором инстанцируется данный класс
        PsiClass clazz = PsiTreeUtil.getParentOfType(methodExpression, PsiClass.class);
        if (clazz != null) {
            return javaOrPropertyReference(element, elementText, classRef, getModuleForActionClass(clazz), onlyModule);
        }
        return null;
    }

    private PsiReference[] javaOrPropertyReference(PsiElement element, String elementText, boolean classRef, String moduleName, boolean onlyModule) {
        String namespaceName;
        TextRange textRange;
        if (!onlyModule && elementText.contains(".")) {
            int dotIndex = elementText.indexOf('.');
            namespaceName = elementText.substring(0, dotIndex);
            textRange = new TextRange(dotIndex + 2, elementText.length() + 1);
        } else {
            namespaceName = null;
            textRange = new TextRange(1, elementText.length() + 1);
        }

        return classRef
               ? new PsiReference[]{new FromJavaClassReference(element, textRange, moduleName, namespaceName, !onlyModule)}
               : new PsiReference[]{new FromJavaPropertyReference(element, textRange, moduleName, namespaceName, !onlyModule)};
    }

    private String getModuleForActionClass(@NotNull PsiClass clazz) {
        GlobalSearchScope searchScope = GlobalSearchScope.getScopeRestrictedByFileTypes(getModuleWithDependenciesScope(clazz), LSFFileType.INSTANCE);
        Collection<PsiReference> refs = ReferencesSearch.search(clazz, searchScope, false).findAll();
        for (PsiReference ref : refs) {
            PsiFile file = ref.getElement().getContainingFile();
            if (file instanceof LSFFile) {
                return ((LSFFile) file).getModuleDeclaration().getName();
            }
        }
        return null;
    }

    private boolean isOneOfStrings(String checkString, String... variants) {
        for (String v : variants) {
            if (v.equals(checkString)) {
                return true;
            }
        }
        return false;
    }
}
