package com.lsfusion.references;

import com.intellij.codeInsight.navigation.ClassImplementationsSearch;
import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.lang.properties.IProperty;
import com.intellij.lang.properties.psi.PropertiesFile;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiElementProcessor;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Processor;
import com.lsfusion.lang.LSFElementGenerator;
import com.lsfusion.lang.LSFFileType;
import com.lsfusion.lang.LSFLanguage;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.lang.psi.Result;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import com.lsfusion.util.BaseUtils;
import com.lsfusion.util.LSFFileUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.intellij.psi.search.GlobalSearchScope.fileScope;
import static com.lsfusion.util.JavaPsiUtils.*;
import static com.lsfusion.util.LSFFileUtils.getModuleWithDependenciesScope;

public class LSFToJavaLanguageInjector implements MultiHostInjector {
    public static final String LSF_LOGICS_PARSER_FQN = "lsfusion.server.LsfLogicsParser";
    public static final String LSF_LOGICS_LEXER_FQN = "lsfusion.server.LsfLogicsLexer";
    public static final String SCRIPTING_ACTION_PROPERTY_FQN = "lsfusion.server.logics.scripted.ScriptingActionProperty";
    public static final String LOGICS_MODULE_FQN = "lsfusion.server.logics.LogicsModule";
    public static final String SCRIPTING_LOGICS_MODULE_FQN = "lsfusion.server.logics.scripted.ScriptingLogicsModule";

    public static final String SCRIPTING_ACTION_PROPERTY_LM_FIELD = "LM";

    public static final String GET_MODULE = "getModule";
    public static final String ADD_MODULE_FROM_RESOURCE = "addModuleFromResource";

    public static final String FIND_CLASS = "findClass";
    public static final String FIND_ACTION = "findAction";
    public static final String FIND_PROPERTY = "findProperty";
    public static final String FIND_PROPERTIES = "findProperties";


    public interface InjectedLanguagePlaces {
        void addStatementUsage(TextRange rangeInsideHost, String moduleName, String prefix);
    }

    private static class Injection {
        public final String prefix;
        public final PsiLanguageInjectionHost host;
        public final TextRange rangeInsideHost;

        private Injection(String prefix, PsiLanguageInjectionHost host, TextRange rangeInsideHost) {
            this.prefix = prefix;
            this.host = host;
            this.rangeInsideHost = rangeInsideHost;
        }
    }

    @Override
    public void getLanguagesToInject(final @NotNull MultiHostRegistrar registrar, @NotNull PsiElement context) {
        assert context instanceof PsiClass;
        
        PsiClass psiClass = (PsiClass)context;
        if (isClass(psiClass, LSF_LOGICS_PARSER_FQN) || isClass(psiClass, LSF_LOGICS_LEXER_FQN)) {
            //пропускаем эти классы, т.к. они очень долго разбираются, но для нас не актуальны
            return;
        }
        
        Collection<PsiLanguageInjectionHost> hosts = PsiTreeUtil.findChildrenOfType(psiClass, PsiLanguageInjectionHost.class);

        // группировка по префиксам по неизвестной причине не работает
//        final Map<String, Map<String, List<Injection>>> injectionsByModules = new HashMap<String, Map<String, List<Injection>>>();
        final Map<String, List<Injection>> injectionsByModules = new HashMap<String, List<Injection>>();
        final Result<Integer> count = new Result<Integer>(0);
        if(hosts.size() > 0) {
            InjectProcessor processor = new InjectProcessor();
            for (final PsiLanguageInjectionHost host : hosts) {
                processor.getLanguagesToInject(host, new InjectedLanguagePlaces() {
                    public void addStatementUsage(TextRange rangeInsideHost, String moduleName, String prefix) {
//                        Map<String, List<Injection>> injections = injectionsByModules.get(moduleName);
//                        if(injections == null) {
//                            injections = new HashMap<String, List<Injection>>();
//                            injectionsByModules.put(moduleName, injections);
//                        }
//                        List<Injection> prefixInjections = injections.get(prefix);
//                        if(prefixInjections == null) {
//                            prefixInjections = new ArrayList<Injection>();
//                            injections.put(prefix, prefixInjections);
//                        }
//                        prefixInjections.add(new Injection(host, rangeInsideHost));

                        List<Injection> injections = injectionsByModules.get(moduleName);
                        if (injections == null) {
                            injections = new ArrayList<Injection>();
                            injectionsByModules.put(moduleName, injections);
                        }
                        injections.add(new Injection(prefix, host, rangeInsideHost));
                        count.setResult(count.getResult() + 1);
                    }
                });
            }
//            for(Map.Entry<String, Map<String, List<Injection>>> injectionByModule : injectionsByModules.entrySet()) {
//                MultiHostRegistrar transReg = registrar.startInjecting(LSFLanguage.INSTANCE);
//                String module = injectionByModule.getKey();
//
//                boolean first = true;
//                for(Map.Entry<String, List<Injection>> injection : injectionByModule.getValue().entrySet()) {
//                    String prefix = injection.getKey();
//                    if(first) {
//                        if (!module.equals("MODULE"))
//                            prefix = "REQUIRE " + module + "; " + prefix;
//                        prefix = "MODULE x; " + prefix;
//                        first = false;
//                    }
//
//                    List<Injection> value = injection.getValue();
//                    for (int i = 0, size = value.size(); i < size; i++) {
//                        Injection inj = value.get(i);
//                        transReg.addPlace(prefix, i == size - 1 ? "; " : "", inj.host, inj.rangeInsideHost);
//                        prefix = ",";
//                    }
//                }
//
//                transReg.doneInjecting();
//            }
            boolean groupInjections = count.getResult() > 30; // из-за бага идеи с left \ right фрагментов в DocumentWindowImpl.injectHost(int)
            for (Map.Entry<String, List<Injection>> injectionByModule : injectionsByModules.entrySet()) {
                if(groupInjections)
                    registrar.startInjecting(LSFLanguage.INSTANCE);

                String module = injectionByModule.getKey();

                boolean first = true;
                for (Injection injection : injectionByModule.getValue()) {
                    String prefix = injection.prefix;
                    if (first) {
                        if (!module.equals("MODULE"))
                            prefix = "REQUIRE " + module + ";\n" + prefix;
                        prefix = "MODULE " + LSFElementGenerator.genName + ";\n" + prefix;

                        if(groupInjections)
                            first = false;
                        else
                            registrar.startInjecting(LSFLanguage.INSTANCE);
                    }

                    registrar.addPlace(prefix, ";\n", injection.host, injection.rangeInsideHost);

                    if(!groupInjections)
                        registrar.doneInjecting();
                }

                if(groupInjections)
                    registrar.doneInjecting();
            }
        }
    }

    @NotNull
    @Override
    public List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
        return Collections.singletonList(PsiClass.class);
    }

    private class InjectProcessor { // для кэшей

        private Map<PsiVariable, Set<String>> varModuleCaches = new HashMap<PsiVariable, Set<String>>();
        private Map<PsiClass, String> classModuleCaches = new HashMap<PsiClass, String>();
        private Map<PsiClass, Integer> superClassesCaches = new HashMap<PsiClass, Integer>();

        //    @Override
        public void getLanguagesToInject(@NotNull PsiLanguageInjectionHost element, @NotNull InjectedLanguagePlaces injectionPlacesRegistrar) {
            if (element instanceof PsiLiteralExpression) {
                PsiLiteralExpression literalExpression = (PsiLiteralExpression) element;
                Object value = literalExpression.getValue();
                if (value instanceof String) {
                    PsiMethodCallExpression methodCall = PsiTreeUtil.getParentOfType(literalExpression, PsiMethodCallExpression.class);
                    if (methodCall != null) {
                        PsiReferenceExpression methodExpression = methodCall.getMethodExpression();

                        resolveModuleRefs(methodExpression, literalExpression, injectionPlacesRegistrar);

                        String methodName = methodExpression.getReferenceName();
                        if (isOneOfStrings(methodName, FIND_ACTION, FIND_PROPERTY, FIND_CLASS, FIND_PROPERTIES)) {
                            boolean classRef = FIND_CLASS.equals(methodName);

                            PsiClass thisClass = resolveThisClass(methodExpression);
                            if (thisClass != null) {
                                Integer hasSuperClasses = superClassesCaches.get(thisClass);
                                if(hasSuperClasses == null) {
                                    hasSuperClasses = hasOneOfSuperClasses(thisClass, SCRIPTING_ACTION_PROPERTY_FQN, SCRIPTING_LOGICS_MODULE_FQN);
                                    superClassesCaches.put(thisClass, hasSuperClasses);
                                }
                                
                                if (hasSuperClasses.equals(0)) { // если наследуется от ScriptingActionProperty
                                    referenceFromScriptingActionProperty(thisClass, literalExpression, classRef, false, injectionPlacesRegistrar);
                                } else if (hasSuperClasses.equals(1)) { // если наследуется от ScriptingLogicsModule
                                    Set<String> modules = new HashSet<String>();
                                    getModulesFromConstructor(modules, thisClass);
                                    javaOrPropertyReference(literalExpression, classRef, BaseUtils.toString(modules, ","), false, injectionPlacesRegistrar);
                                }
                            } else {
                                resolveModuleMethodsRef(methodExpression.getQualifierExpression(), literalExpression, classRef, false, injectionPlacesRegistrar);
                            }
                        }
                    }
                }
            }
        }

        private PsiClass resolveThisClass(PsiReferenceExpression methodExpression) {
            PsiExpression qualifierExpression = methodExpression.getQualifierExpression();
            if (qualifierExpression instanceof PsiThisExpression) { // проверяем this
                PsiType type = qualifierExpression.getType();
                if (type != null) {
                    if (type instanceof PsiClassType) {
                        PsiClass psiClass = ((PsiClassType) type).resolve();
                        if (psiClass != null) {
                            return psiClass;
                        }
                    }
                }
            } else if (qualifierExpression == null) {
                PsiElement resolve = methodExpression.resolve();
                if (resolve instanceof PsiMethod) {
                    return findThisClass(methodExpression, (PsiMethod) resolve);
                }
            } else if (hasScriptingActionPropertyLMQualifier(qualifierExpression)) {
                return PsiTreeUtil.getParentOfType(methodExpression, PsiClass.class);
            }
            return null;
        }

        //является ли вызовом метода у поля ScriptingActionProperty.LM либо у параметра конструктора ScriptingActionProperty
        private boolean hasScriptingActionPropertyLMQualifier(PsiExpression qualifierExpression) {
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

        private boolean useThisClass(PsiExpression qualifierExpression) {
            if (qualifierExpression instanceof PsiReferenceExpression) { //является ли вызовом метода у поля ScriptingActionProperty.LM либо у параметра конструктора ScriptingActionProperty
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
                        if (declarationMethod.isConstructor() && isLogicsModuleVar(lmParam)) {
                            return true;
                        }
                    }
                }
            }
            return qualifierExpression == null;
        }

        private void resolveModuleRefs(PsiReferenceExpression methodExpression, PsiLiteralExpression element, InjectedLanguagePlaces injectionPlacesRegistrar) {
            String methodName = methodExpression.getReferenceName();
            if (isOneOfStrings(methodName, GET_MODULE)) {
                injectionPlacesRegistrar.addStatementUsage(new TextRange(1, element.getTextLength() - 1), "MODULE", "EXTERNAL MODULE ");
            }
        }

        private void resolveModuleMethodsRef(PsiExpression qualifierExpression, PsiLiteralExpression element, boolean classRef, boolean onlyModule, InjectedLanguagePlaces injectionPlacesRegistrar) {
            if (qualifierExpression != null) {
                String moduleName = getModuleName(qualifierExpression);
                if (moduleName == null)
                    moduleName = getTopModuleList(qualifierExpression);
                javaOrPropertyReference(element, classRef, moduleName, onlyModule, injectionPlacesRegistrar);
            }
        }

        private String getTopModuleList(PsiElement element) {

            List<PsiFile> filesByPath = LSFFileUtils.findFilesByPath(element, "lsfusion.properties");
            for (PsiFile file : filesByPath) {
                if (file instanceof PropertiesFile) {
                    PropertiesFile propFile = (PropertiesFile) file;
                    IProperty property = propFile.findPropertyByKey("logics.topModulesList");
                    if (property != null) {
                        return property.getValue();
                    }
                }
            }
            return "dumb";
        }

        private String getFileBaseName(String path) {
            int dir = path.lastIndexOf("/");
            if (dir > 0)
                path = path.substring(dir + 1);

            int sep = path.lastIndexOf(".");
            if (sep > 0)
                path = path.substring(0, sep);
            return path;
        }

        private PsiClass findThisClass(PsiReferenceExpression refExpr, PsiMember refMember) {
            final PsiClass refMemberClass = refMember.getContainingClass();
            if (refMemberClass == null) return null;
            PsiElement parent = refExpr.getContext();
            while (parent != null) {
                if (parent instanceof PsiClass) {
                    if (parent.equals(refMemberClass) || ((PsiClass) parent).isInheritor(refMemberClass, true)) {
                        return (PsiClass) parent;
                    }
                }
                parent = parent.getContext();
            }

            return refMemberClass;
        }

        private String getModuleName(PsiExpression qualifierExpression) {
            //проверяем на ссылку .getModule("ModuleName")
            Set<String> modules = new HashSet<String>();

            if (qualifierExpression instanceof PsiMethodCallExpression) {
                return extractModuleNameFromPossibleGetModuleCall((PsiMethodCallExpression) qualifierExpression);
            } else if (qualifierExpression instanceof PsiReferenceExpression) {
                PsiElement lmRef = ((PsiReferenceExpression) qualifierExpression).resolve();
                if (lmRef instanceof PsiVariable && isLogicsModuleVar((PsiVariable) lmRef)) {
                    //ссылка на переменную, ищем инициализацию этой перменной одним их двух способобов:
                    //  someLM = addModuleFromResource("scripts/path/Some.lsf")
                    //  someLM = BL.getModule("Some")

                    PsiVariable lmVar = (PsiVariable) lmRef;
                    Set<String> varModules = varModuleCaches.get(lmVar);
                    if (varModules == null) {
                        varModules = new HashSet<String>();

                        String usageName = getVarUsageModuleName(qualifierExpression, lmVar);
                        if (usageName != null)
                            varModules.add(usageName);
                        else
                            getModulesFromType(lmVar.getType(), varModules);

                        varModuleCaches.put(lmVar, varModules);
                    }
                    modules.addAll(varModules);
                }
            }
            //todo: if qualifierExpression == null or refToConcreateClassModule(i.e. EmailLogicsModule) => search for file path in constructor...

            if (!modules.isEmpty())
                return BaseUtils.toString(modules, ",");
            return null;
        }

        private boolean isLogicsModuleVar(PsiVariable lmVar) {
            PsiType type = lmVar.getType();
            return type instanceof PsiClassType && hasSuperClass(((PsiClassType) type).resolve(), SCRIPTING_LOGICS_MODULE_FQN);
        }

        private String getVarUsageModuleName(PsiExpression qualifierExpression, PsiVariable lmVar) {
            //todo: check type of lmVar
            List<PsiExpression> rightExprs = new ArrayList<PsiExpression>();
            if (lmVar.hasInitializer())
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
                                    List<PsiFile> files = LSFFileUtils.findFilesByPath(qualifierExpression, (String) argValue);
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
                if (moduleName.getResult() != null)
                    return moduleName.getResult();
            }
            return null;
        }

        private void getModulesFromType(PsiType varType, Set<String> modules) {
            if (varType instanceof PsiClassType) {
                PsiClass psiClass = ((PsiClassType) varType).resolve();
                if (psiClass != null) {
                    getModulesFromConstructor(modules, psiClass);
                }
            }
        }

        private void getModulesFromConstructor(Set<String> modules, PsiClass psiClass) {
            for (PsiMethod constructor : psiClass.getConstructors()) {
                PsiCodeBlock body = constructor.getBody();
                if (body != null) {
                    PsiStatement[] statements = body.getStatements();
                    if (statements.length > 0) {
                        PsiStatement statement = statements[0];
                        for (PsiLiteralExpression literalExpression : PsiTreeUtil.findChildrenOfType(statement, PsiLiteralExpression.class)) {
                            String filename = getFileBaseName(literalExpression.getText());
                            if (!filename.isEmpty() && !filename.equals("null"))
                                modules.add(filename);
                        }
                    }
                }
            }
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

        private void referenceFromScriptingActionProperty(PsiClass clazz, PsiLiteralExpression element, boolean classRef, boolean onlyModule, InjectedLanguagePlaces injectionPlacesRegistrar) {
            //прямой вызов => ищем модуль, в котором инстанцируется данный класс
            javaOrPropertyReference(element, classRef, getModuleForActionClass(clazz), onlyModule, injectionPlacesRegistrar);
        }

        private void javaOrPropertyReference(PsiLiteralExpression element, boolean classRef, String moduleName, boolean onlyModule, InjectedLanguagePlaces injectionPlacesRegistrar) {
            String prefix;
            if (classRef) {
                prefix = "CLASS";
            } else
                prefix = "PROPERTY";

            injectionPlacesRegistrar.addStatementUsage(new TextRange(1, element.getTextLength() - 1), moduleName, "EXTERNAL " + prefix + " ");
        }

        private String getModuleForActionClass(final @NotNull PsiClass clazz) {
            String module = classModuleCaches.get(clazz);
            if (module != null) {
//            if(!module.equals(calcModuleActionForClass(clazz)))
//                module = module;
                return module;
            }

            module = calcModuleActionForClass(clazz);
            classModuleCaches.put(clazz, module);
            return module;
        }

        private String calcModuleActionForClass(PsiClass clazz) {
            GlobalSearchScope scope = getModuleWithDependenciesScope(clazz);

            final List<PsiClass> result = new ArrayList<PsiClass>();
            result.add(clazz);
            ClassImplementationsSearch.processImplementations(clazz, new Processor<PsiElement>() {
                public boolean process(PsiElement psiElement) {
                    if (psiElement instanceof PsiClass)
                        result.add((PsiClass) psiElement);
                    return true;
                }
            }, scope);
            Set<String> moduleNames = new HashSet<String>();
            for (PsiClass iclazz : result) {
                GlobalSearchScope searchScope = GlobalSearchScope.getScopeRestrictedByFileTypes(scope, LSFFileType.INSTANCE);
                Collection<PsiReference> refs = ReferencesSearch.search(iclazz, searchScope, false).findAll();
                for (PsiReference ref : refs) {
                    PsiFile file = ref.getElement().getContainingFile();
                    if (file instanceof LSFFile) {
                        moduleNames.add(((LSFFile) file).getModuleDeclaration().getName());
                    }
                }
            }

            if (moduleNames.isEmpty())
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
}
