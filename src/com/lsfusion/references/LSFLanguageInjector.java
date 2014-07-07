package com.lsfusion.references;

import com.intellij.codeInsight.navigation.ClassImplementationsSearch;
import com.intellij.lang.properties.IProperty;
import com.intellij.lang.properties.psi.PropertiesFile;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiElementProcessor;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Processor;
import com.lsfusion.lang.LSFFileType;
import com.lsfusion.lang.LSFLanguage;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.lang.psi.Result;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import com.lsfusion.util.BaseUtils;
import com.lsfusion.util.LSFPsiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.intellij.psi.search.GlobalSearchScope.fileScope;
import static com.lsfusion.util.JavaPsiUtils.hasSuperClass;
import static com.lsfusion.util.JavaPsiUtils.isClass;
import static com.lsfusion.util.LSFPsiUtils.getModuleScope;

public class LSFLanguageInjector implements LanguageInjector {

    @Override
    public void getLanguagesToInject(@NotNull PsiLanguageInjectionHost element, @NotNull InjectedLanguagePlaces injectionPlacesRegistrar) {
        if(element instanceof PsiLiteralExpression) {
            PsiLiteralExpression literalExpression = (PsiLiteralExpression) element;
            Object value = literalExpression.getValue();
            if (value instanceof String) {
                PsiMethodCallExpression methodCall = PsiTreeUtil.getParentOfType(literalExpression, PsiMethodCallExpression.class);
                if (methodCall != null) {
                    PsiReferenceExpression methodExpression = methodCall.getMethodExpression();

                    resolveModuleRefs(methodExpression, literalExpression, injectionPlacesRegistrar);

                    resolveLogicsModuleMethodsRefs(methodExpression, literalExpression, injectionPlacesRegistrar);

                    resolveScriptingLogicsModuleMethodsRefs(methodExpression, literalExpression, injectionPlacesRegistrar);

                    resolveScriptingActionPropertyMethodsRefs(methodExpression, literalExpression, injectionPlacesRegistrar);
                }
            }
        }
    }

    private void resolveModuleRefs(PsiReferenceExpression methodExpression, PsiLiteralExpression element, InjectedLanguagePlaces injectionPlacesRegistrar) {
        String methodName = methodExpression.getReferenceName();
        if (isOneOfStrings(methodName, GET_MODULE)) {
            injectionPlacesRegistrar.addPlace(LSFLanguage.INSTANCE, new TextRange(1, element.getTextLength() - 1), "MODULE x; REQUIRE ", ";");
        }
    }

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

    private void resolveLogicsModuleMethodsRefs(PsiReferenceExpression methodExpression, PsiLiteralExpression element, InjectedLanguagePlaces injectionPlacesRegistrar) {
        String methodName = methodExpression.getReferenceName();
        if (isOneOfStrings(methodName, GET_LCPBY_OLD_NAME, GET_LAPBY_OLD_NAME, GET_CLASS_BY_NAME)) {
            boolean classRef = GET_CLASS_BY_NAME.equals(methodName);
            resolveModuleMethodsRef(methodExpression, element, classRef, true, injectionPlacesRegistrar);
        }
    }

    private void resolveScriptingLogicsModuleMethodsRefs(PsiReferenceExpression methodExpression, PsiLiteralExpression element, InjectedLanguagePlaces injectionPlacesRegistrar) {
        String methodName = methodExpression.getReferenceName();
        if (isOneOfStrings(methodName, FIND_LCPBY_COMPOUND_OLD_NAME, FIND_LAPBY_COMPOUND_OLD_NAME, FIND_CLASS_BY_COMPOUND_NAME)) {
            boolean classRef = FIND_CLASS_BY_COMPOUND_NAME.equals(methodName);
            resolveModuleMethodsRef(methodExpression, element, classRef, false, injectionPlacesRegistrar);
        }
    }

    private void resolveModuleMethodsRef(PsiReferenceExpression methodExpression, PsiLiteralExpression element, boolean classRef, boolean onlyModule, InjectedLanguagePlaces injectionPlacesRegistrar) {
        if (hasScriptingActionPropertyLMQualifier(methodExpression)) {
            referenceFromScriptingActionProperty(methodExpression, element, classRef, onlyModule, injectionPlacesRegistrar);
            return;
        }
        String moduleName = getModuleName(methodExpression);
        if(moduleName == null)
            moduleName = getTopModuleList(methodExpression);
        javaOrPropertyReference(element, classRef, moduleName, onlyModule, injectionPlacesRegistrar);
    }
    
    private String getTopModuleList(PsiElement element) {

        List<PsiFile> filesByPath = LSFPsiUtils.findFilesByPath(element, "lsfusion.properties");
        for(PsiFile file : filesByPath) {
            if(file instanceof PropertiesFile) {
                PropertiesFile propFile = (PropertiesFile)file ;
                IProperty property = propFile.findPropertyByKey("logics.topModulesList");
                if(property != null) {
                    return property.getValue();
                }
            }
        }
        return "dumb";
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
                
                List<PsiExpression> rightExprs = new ArrayList<PsiExpression>();
                if(lmVar.hasInitializer())
                    rightExprs.add(lmVar.getInitializer());

                GlobalSearchScope fileScope = fileScope(lmVar.getContainingFile());
                Collection<PsiReference> refs = ReferencesSearch.search(lmVar, fileScope, false).findAll();
                for (PsiReference ref : refs) {
                    PsiElement refElement = ref.getElement();
                    PsiAssignmentExpression assignmentExpression = PsiTreeUtil.getParentOfType(refElement, PsiAssignmentExpression.class);
                    if (assignmentExpression != null && assignmentExpression.getLExpression() == refElement) {
                        rightExprs.add(assignmentExpression.getRExpression());
                    }
                }
                
                for (PsiExpression rightExpr : rightExprs) {
                    if (rightExpr instanceof PsiMethodCallExpression) {
                        PsiMethodCallExpression methodCall = (PsiMethodCallExpression) rightExpr;
                        String methodName = methodCall.getMethodExpression().getReferenceName();

                        if (isOneOfStrings(methodName, ADD_MODULE_FROM_RESOURCE)) {
                            //  someLM = addModuleFromResource("scripts/path/Some.lsf")
                            PsiExpression[] methodArgs = methodCall.getArgumentList().getExpressions();
                            if (methodArgs.length == 1) {
                                PsiExpression expr = methodArgs[0];
                                if (expr instanceof PsiLiteralExpression) {
                                    Object argValue = ((PsiLiteralExpression) expr).getValue();
                                    if (argValue instanceof String) {
                                        List<PsiFile> files = LSFPsiUtils.findFilesByPath(methodExpression, (String) argValue);
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
                    PsiTreeUtil.processElements(rightExpr, new PsiElementProcessor() {
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
                    if (moduleName.getResult() != null) {
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

    private void resolveScriptingActionPropertyMethodsRefs(PsiReferenceExpression methodExpression, PsiLiteralExpression element, InjectedLanguagePlaces injectionPlacesRegistrar) {
        String methodName = methodExpression.getReferenceName();
        if (isOneOfStrings(methodName, GET_LAP, GET_LCP, GET_CLASS, GET_LCP + "s")) {
            PsiElement qualifier = methodExpression.getQualifier();
            if (qualifier == null) {
                referenceFromScriptingActionProperty(methodExpression, element, GET_CLASS.equals(methodName), false, injectionPlacesRegistrar);
            }
        }
    }

    private void referenceFromScriptingActionProperty(PsiReferenceExpression methodExpression, PsiLiteralExpression element, boolean classRef, boolean onlyModule, InjectedLanguagePlaces injectionPlacesRegistrar) {
        //прямой вызов getLAP, getLCP => ищем модуль, в котором инстанцируется данный класс
        PsiClass clazz = PsiTreeUtil.getParentOfType(methodExpression, PsiClass.class);
        if (clazz != null) {
            javaOrPropertyReference(element, classRef, getModuleForActionClass(clazz), onlyModule, injectionPlacesRegistrar);
        }
    }

    private void javaOrPropertyReference(PsiLiteralExpression element, boolean classRef, String moduleName, boolean onlyModule, InjectedLanguagePlaces injectionPlacesRegistrar) {
        String prefix;
        if(classRef) {
            prefix = "CLASS";
        } else
            prefix = "PROPERTY";

        injectionPlacesRegistrar.addPlace(LSFLanguage.INSTANCE, new TextRange(1, element.getTextLength() - 1), "MODULE x; REQUIRE " + moduleName + "; EXTERNAL " + prefix + " ", ";");
    }

    private String getModuleForActionClass(final @NotNull PsiClass clazz) {
        GlobalSearchScope scope = getModuleScope(clazz);

        final List<PsiClass> result = new ArrayList<PsiClass>();
        result.add(clazz);
        ClassImplementationsSearch.processImplementations(clazz, new Processor<PsiElement>() {
            public boolean process(PsiElement psiElement) {
                if(psiElement instanceof PsiClass)
                    result.add((PsiClass) psiElement);
                return true;
            }
        }, scope);
        Set<String> moduleNames = new HashSet<String>();
        for(PsiClass iclazz : result) {
            GlobalSearchScope searchScope = GlobalSearchScope.getScopeRestrictedByFileTypes(scope, LSFFileType.INSTANCE);
            Collection<PsiReference> refs = ReferencesSearch.search(iclazz, searchScope, false).findAll();
            for (PsiReference ref : refs) {
                PsiFile file = ref.getElement().getContainingFile();
                if (file instanceof LSFFile) {
                    return ((LSFFile) file).getModuleDeclaration().getName();
                }
            }
        }
        
        if(moduleNames.isEmpty())
            return getTopModuleList(clazz);
        
        return BaseUtils.toString(moduleNames, ",");
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
