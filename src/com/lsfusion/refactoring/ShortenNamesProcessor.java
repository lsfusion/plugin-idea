package com.lsfusion.refactoring;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageManagerImpl;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.lang.LSFFileType;
import com.lsfusion.lang.classes.*;
import com.lsfusion.lang.meta.MetaChangeDetector;
import com.lsfusion.lang.meta.MetaTransaction;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.lang.psi.LSFFormPropertyOptionsList;
import com.lsfusion.lang.psi.LSFMetaCodeStatement;
import com.lsfusion.lang.psi.declarations.*;
import com.lsfusion.lang.psi.references.*;
import com.lsfusion.migration.MigrationElementGenerator;
import com.lsfusion.migration.lang.MigrationFileType;
import com.lsfusion.migration.lang.psi.MigrationFile;
import com.lsfusion.migration.lang.psi.MigrationStatement;
import com.lsfusion.migration.lang.psi.MigrationVersionBlock;
import com.lsfusion.migration.lang.psi.MigrationVersionBlockBody;
import com.lsfusion.util.BaseUtils;
import com.lsfusion.util.LSFFileUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;

import static com.lsfusion.util.LSFPsiUtils.collectInjectedLSFFiles;
import static com.lsfusion.util.LSFPsiUtils.getLastChildOfType;

public class ShortenNamesProcessor {
    private static final String INITIAL_MIGRATION_VERSION = "1.0.0";
    private static PropInMetaRef[] exceptions = new PropInMetaRef[] {
            new PropInMetaRef(new PropRef(null, null, null, true), new MetacodeRef("implementConsignmentHeader", "Consignment", 1), false, true),
            new PropInMetaRef(new PropRef(null, null, null, true), new MetacodeRef("implementPriceTransactionDocument", "Label", 1), false, true),
            new PropInMetaRef(new PropRef(null, null, null, true), new MetacodeRef("implementSaleLedgerCustom", "SaleLedger", 3), false, true),
            new PropInMetaRef(new PropRef(null, null, null, true), new MetacodeRef("implementStockDocumentLedger", "StockDocument", 2), false, true),
            new PropInMetaRef(new PropRef(null, null, null, true), new MetacodeRef("implementSkuLedger", "Stock", 3), false, true),
            new PropInMetaRef(new PropRef(null, null, null, true), new MetacodeRef("implementDocument", "Stock", 1), false, true),
            new PropInMetaRef(new PropRef(null, null, null, true), new MetacodeRef("implementBatchCustom", "Stock", 4), false, true),
            new PropInMetaRef(new PropRef(null, null, null, true), new MetacodeRef("implementPurchaseLedgerCustom", "PurchaseLedger", 3), false, true),
            new PropInMetaRef(new PropRef("batch", "Stock", Collections.singletonList(new ClassRef("DocumentDetail", "Stock")), true), new MetacodeRef("implementDocumentBatch", "Stock", 1), false, true),
            new PropInMetaRef(new PropRef("name", "Box", Collections.singletonList(new ClassRef("Box", "Box")), false), new MetacodeRef("defineDocumentDetailBoxCustom", "Box", 3), true, false),
            new PropInMetaRef(new PropRef("name", "Box", Collections.singletonList(new ClassRef("Box", "Box")), false), new MetacodeRef("defineDocumentAbstractDetailBoxCustom", "Box", 3), true, false),
            new PropInMetaRef(new PropRef("skip", "Machinery", Collections.singletonList(new ClassRef("PriceTransactionDocument", "Machinery")), false), new MetacodeRef("defineDocumentMachineryPriceTransaction", "Machinery", 3), true, false),
            new PropInMetaRef(new PropRef("stock", "Invoice", Collections.singletonList(new ClassRef("InvoiceDetail", "Invoice")), true), new MetacodeRef("defineInvoice", "Invoice", 4), false, true)
    };

    private static List<String> getWords(String string) {
        if(string.isEmpty())
            return new ArrayList<String>();
            
        List<String> result = new ArrayList<String>();
        int prevStart = 0;
        for(int i=1;i<string.length();i++) {
            if(Character.isUpperCase(string.charAt(i))) {
                if(prevStart >= 0)
                    result.add(string.substring(prevStart, i));
                prevStart = i;
            }
        }
        if(prevStart >= 0)
            result.add(string.substring(prevStart, string.length()));
        return result;
    }

    private static String toWords(List<String> words) {
        String result = "";
        for(String word : words)
            result += word;
        return result;
    }

    private static String shortenName(LSFPropDeclaration decl) {
        String declName = decl.getDeclName();
        
        List<String> declWords = getWords(declName);
        int keep = declWords.size() - 1;
        
        List<String> result = new ArrayList<String>();

        List<String> paramNames = null;
        if(decl instanceof LSFGlobalPropDeclaration)
            paramNames = ((LSFGlobalPropDeclaration)decl).resolveParamNames();

        boolean firstAte = false;
        List<LSFClassSet> paramClasses = decl.resolveParamClasses();
        if(paramClasses!=null || paramNames!=null) {
            int size = (paramClasses != null ? paramClasses.size() : paramNames.size()) - 1;
            for(int i= size;i>=0;i--) {
                LSFClassSet prmClass = null;
                if(paramClasses!=null)
                    prmClass = paramClasses.get(i);
                String prmName = null;
                if(paramNames!=null)
                    prmName = paramNames.get(i);

                if(i==0 && size == 1 && (declName.equals("toDate") || declName.equals("toTime") || declName.equals("toDateTime"))) {
                    String prmCheck = null;
                    if(prmName!=null)
                        prmCheck = prmName.toUpperCase();
                    if(prmClass!=null)
                        prmCheck = prmClass.getCommonClass().getName();
                    if(prmCheck != null) {
                        if((declName.equals("toDate") && prmCheck.equals(DateTimeClass.instance.getName())) ||
                            (declName.equals("toTime") && prmCheck.equals(DateTimeClass.instance.getName())) ||
                            (declName.equals("toDateTime") && prmCheck.equals(DateClass.instance.getName())))
                            return declName;                            
                    }
                }

                int k = 0; // сколько слов нашли                    
                int skip = 0; // сколько слов пропустили
                int skipFull = 0; // сколько полных слов пропустили
                while(k == 0) {
                    List<String> sNames = new ArrayList<String>();
                    if(prmClass!=null)
                        sNames = prmClass.getCommonClass().getSNames();
                    if(paramNames!=null)
                        sNames = BaseUtils.add(sNames, StringUtils.capitalize(prmName));
                    for(String sname : sNames) {
                        k = equalWords(declWords, keep - skip, sname);
                        if(result.size()==0 && skip == 0 && k > keep) // если все съели, оставим одно слово
                            k = k-1;
                        if(k > 0)
                            break;
                    }
                    
                    if(k == 0) {
                        if(keep - skip >= 0) {
                            if(declWords.get(keep - skip).length() > 1)
                                skipFull++;
                        } else
                            keep = keep;
                        skip++;
                    }
                    
                    if(skipFull > 3 || (keep - skip) < 0) // пропускаем не больше 3 слов
                        break;
                }
                
                if(k > 0) { // нашли
                    for(int u=0;u<skip;u++)
                        result.add(declWords.get(keep - u));
                    keep -= skip;
                    if(k > keep) // если съели первую
                        firstAte = true;    
                        
                    keep -= k;
                }
            }
        }
        
        result.addAll(BaseUtils.reverse(declWords.subList(0, keep+1)));
        if(firstAte) {
            String s = result.get(result.size() - 1);
            if(s.length() > 1)
                result.set(result.size() - 1, s.toLowerCase());
        }
        return toWords(BaseUtils.reverse(result));
    }

    private static int equalWords(List<String> declWords, int keep, String sname) {
        List<String> prmWords = getWords(sname);
        int j = prmWords.size() - 1;
        int k = 0;
        int skipPrm = 0;
        while(true) {
            while(j - skipPrm - k >= 0 && keep - k >= 0 && equalWord(declWords.get(keep - k), prmWords.get(j - skipPrm - k), keep==k))
                k++;
            skipPrm++;
            if(k>0 || skipPrm > j)
                break;                                                         
        }
        return k;
    }

    private static boolean equalWord(String first, String second, boolean isFirst) {
        return first.equals(second) || (isFirst && first.toLowerCase().equals(second.toLowerCase()));
    }

    public static void shortenAllPropNames(Project project) {
        shortenAllPropNames(project, MigrationChangePolicy.INCREMENT_VERSION);
    }

    public static void shortenAllPropNames(Project project, MigrationChangePolicy migrationChangePolicy) {
        GlobalSearchScope scope = ProjectScope.getProjectScope(project);

        final List<LSFFile> files = new ArrayList<LSFFile>();

        for (VirtualFile lsfFile : FileTypeIndex.getFiles(LSFFileType.INSTANCE, scope)) {
            PsiFile psiFile = PsiManager.getInstance(project).findFile(lsfFile);
            if (psiFile instanceof LSFFile) {
                files.add((LSFFile) psiFile);
            }
        }
        
        for (VirtualFile javaFile : FileTypeIndex.getFiles(JavaFileType.INSTANCE, scope)) {
            files.addAll(collectInjectedLSFFiles(javaFile, project));
        }

        for (VirtualFile xmlFile : FileTypeIndex.getFiles(XmlFileType.INSTANCE, scope)) {
            if (xmlFile.getName().endsWith("jrxml")) {
                files.addAll(collectInjectedLSFFiles(xmlFile, project));
            }
        }
        
        List<PropertyMigration> migrations = new ArrayList<PropertyMigration>();

        MetaTransaction transaction = new MetaTransaction();

        shortenAllPropNames(files, migrations, transaction);

        transaction.apply();
        
        if (migrationChangePolicy != MigrationChangePolicy.DO_NOT_MODIFY) {
            modifyMigrationScripts(migrations, migrationChangePolicy, project, ProjectScope.getProjectScope(project));
        }
    }

    public static void modifyMigrationScripts(List<PropertyMigration> migrations, MigrationChangePolicy migrationChangePolicy, Project project, GlobalSearchScope scope) {
        Collection<VirtualFile> migrationFiles = FileTypeIndex.getFiles(MigrationFileType.INSTANCE, scope);
        for (VirtualFile file : migrationFiles) {
            PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
            if (psiFile instanceof MigrationFile) {
                boolean createNewVersionBlock = migrationChangePolicy == MigrationChangePolicy.INCREMENT_VERSION;
                if (migrationChangePolicy == MigrationChangePolicy.INCREMENT_VERSION_IF_COMMITED) {
                    createNewVersionBlock = LSFFileUtils.isFileCommited(project, file);
                }
                
                GlobalSearchScope moduleScope = LSFFileUtils.getModuleWithDependenciesScope(psiFile);
                
                MigrationFile migrationFile = (MigrationFile)psiFile;
                
                String statements = filterMigrationsToString(migrations, moduleScope);
                if (statements.isEmpty()) {
                    continue;
                }

                MigrationVersionBlock lastVersionBlock = getLastChildOfType(migrationFile, MigrationVersionBlock.class);
                if (createNewVersionBlock || lastVersionBlock == null) {
                    String newVersion = lastVersionBlock == null ? INITIAL_MIGRATION_VERSION : incrementedVersion(lastVersionBlock.getVersionLiteral().getText());

                    String prefix = "";
                    PsiElement lastChild = migrationFile.getLastChild();
                    if (lastVersionBlock != null && lastChild != null && !lastChild.getText().endsWith("\n\n")) {
                        prefix = "\n\n";
                    }

                    Pair<PsiElement, PsiElement> newElements = MigrationElementGenerator.createVersionBlock(project, prefix, newVersion, statements);
                    migrationFile.addRange(newElements.first, newElements.second);
                } else {

                    Pair<PsiElement, PsiElement> newStatements = MigrationElementGenerator.createStatementsElements(project, statements);

                    MigrationVersionBlockBody lastVersionBlockBody = lastVersionBlock.getVersionBlockBody();
                    PsiElement anchor = getLastChildOfType(lastVersionBlockBody, MigrationStatement.class);
                    if (anchor == null) {
                        anchor = lastVersionBlockBody.getFirstChild();
                    }
                    
                    lastVersionBlockBody.addRangeAfter(newStatements.first, newStatements.second, anchor);
                }
            }
        }
    }

    private static String incrementedVersion(String currentVersionWithPrefix) {
        String currenVersion = currentVersionWithPrefix.trim().substring(1);
        
        int dotIndex = currenVersion.lastIndexOf('.');
        String lastPart;
        String prefix;
        if (dotIndex != -1) {
            prefix = currenVersion.substring(0, dotIndex + 1);
            lastPart = currenVersion.substring(dotIndex + 1).trim();
        } else {
            prefix = "";
            lastPart = currenVersion.trim();
        }

        int lastPartInt;
        try {
            lastPartInt = Integer.parseInt(lastPart) + 1;
        } catch (NumberFormatException nfe) {
            lastPartInt = 1;
        }
        
        return prefix + lastPartInt;
    }

    private static String filterMigrationsToString(List<PropertyMigration> migrations, GlobalSearchScope moduleScope) {
        String result = "";
        for (PropertyMigration migration : migrations) {
            VirtualFile declarationFile = migration.getDeclarationFile();
            if (declarationFile != null && moduleScope.accept(declarationFile)) {
                if (!result.isEmpty()) {
                    result += "\n";
                }

                result += "    PROPERTY " + migration.getMigrationString();
            }
        }
        return result;
    }

    public static void shortenAllPropNames(Collection<LSFFile> files, List<PropertyMigration> migrations, MetaTransaction transaction) {

        Project project = files.iterator().next().getProject();
        MetaChangeDetector.getInstance(project).setMetaEnabled(false, false);

        Map<LSFPropDeclaration, String> propertyDecls = new HashMap<LSFPropDeclaration, String>();
        Map<LSFPropertyDrawDeclaration, String> propertyDrawDecls = new HashMap<LSFPropertyDrawDeclaration, String>();
        
        System.out.println("Collecting property decls...");
        int i = 0;
        for(LSFFile file : files) {
            i++;
            for(LSFPropDeclaration decl : PsiTreeUtil.findChildrenOfType(file, LSFPropDeclaration.class))
                propertyDecls.put(decl, shortenName(decl));
            if (i % 1000 == 0) {
                System.out.println(i + " of " + files.size() + ": " + (double) i / ((double) files.size()));
            }
        }

        System.out.println("Collecting and resolving property refs...");

        List<ExtPropRef> propRefs = new ArrayList<ExtPropRef>();
        i = 0;
        for (LSFFile file : files) {
            i++;
            for (LSFPropReference ref : PsiTreeUtil.findChildrenOfType(file, LSFPropReference.class)) {
                LSFPropDeclaration decl = ref.resolveDecl();
                if (decl != null) {
                    List<LSFClassSet> paramClasses = decl.resolveParamClasses();
                    if (paramClasses != null) {
                        propRefs.add(new ExtPropRef(ref, decl, paramClasses, ref.hasExplicitClasses(), ref.getFullNameRef() != null));
                    }
                }
            }
            if (i % 1000 == 0) {
                System.out.println(i + " of " + files.size() + ": " + (double) i / ((double) files.size()));
            }
        }
        
        System.out.println("Collecting property draw decls...");
        i = 0;
        for(ExtPropRef propRef : propRefs) {
            i++;

            PsiElement refParent = PsiTreeUtil.getParentOfType(propRef.ref.getElement(), LSFPropertyDrawDeclaration.class, LSFFormPropertyOptionsList.class);

            if (refParent instanceof LSFPropertyDrawDeclaration) {
                LSFPropertyDrawDeclaration drawDecl = (LSFPropertyDrawDeclaration) refParent;
                if (drawDecl.getSimpleName() == null) {
                    String newName = propertyDecls.get(propRef.decl);
                    propertyDrawDecls.put(drawDecl, newName);
                }
            }

            if (i % 1000 == 0) {
                System.out.println(i + " of " + propRefs.size() + ": " + (double) i / ((double) propRefs.size()));
            }
        }

        System.out.println("Collecting and resolving property draw refs...");
        List<ExtPropDrawRef> propDrawRefs = new ArrayList<ExtPropDrawRef>();
        i = 0;
        for(LSFFile file : files) {
            i++;
            for(LSFPropertyDrawReference ref : PsiTreeUtil.findChildrenOfType(file, LSFPropertyDrawReference.class)) {
                if (ref.getAliasUsage() == null) {
                    LSFPropertyDrawDeclaration decl = ref.resolveDecl();
                    if (decl != null && decl.getSimpleName() == null) {
                        propDrawRefs.add(new ExtPropDrawRef(ref, decl));
                    }
                }
            }
            if (i % 1000 == 0) {
                System.out.println(i + " of " + files.size() + ": " + (double) i / ((double) files.size()));
            }
        }

        System.out.println("Renaming property draw refs...");
        i = 0;
        for(ExtPropDrawRef propDrawRef : propDrawRefs) {
            i++;

            String newPropDrawName = propertyDrawDecls.get(propDrawRef.decl);
            if (newPropDrawName != null) {
                propDrawRef.ref.handleElementRename(newPropDrawName, transaction);
            }

            if (i % 1000 == 0) {
                System.out.println(i + " of " + propRefs.size() + ": " + (double) i / ((double) propRefs.size()));
            }
        }

        System.out.println("Sorting property refs...");
        final InjectedLanguageManager injectionManager = InjectedLanguageManagerImpl.getInstance(project);
        Collections.sort(propRefs, new Comparator<ExtPropRef>() {
            @Override
            public int compare(ExtPropRef o1, ExtPropRef o2) {
                PsiLanguageInjectionHost host1 = injectionManager.getInjectionHost(o1.ref);
                PsiLanguageInjectionHost host2 = injectionManager.getInjectionHost(o2.ref);
                if (host1 == null && host2 != null) return 1;
                if (host1 != null && host2 == null) return -1;
                if (host1 == null && host2 == null) return 0;
                
                PsiFile f1 = host1.getContainingFile();
                PsiFile f2 = host2.getContainingFile();
                
                if (f1 != f2) {
                    //в разных файлах - не важно как сортировать
                    return Integer.compare(System.identityHashCode(f1), System.identityHashCode(f2));
                }

                //позже должен обрабатываться тот, который выше по файлу
                return -Integer.compare(host1.getTextOffset(), host2.getTextOffset());
            }
        });
        
        System.out.println("Renaming and qualifying property refs...");
        i = 0;
        for(ExtPropRef resolvedRef : propRefs) {
            i++;

            String newPropName = propertyDecls.get(resolvedRef.decl);

            LSFPropReference propRef = resolvedRef.ref;

            propRef.handleElementRename(newPropName, transaction);
            if(!resolvedRef.qualClasses)
                propRef.setExplicitClasses(resolvedRef.classes, transaction);
            if(!resolvedRef.qualNamespace && (resolvedRef.decl instanceof LSFFullNameDeclaration))
                propRef.setFullNameRef(((LSFFullNameDeclaration) resolvedRef.decl).getNamespaceName(), transaction);

            if (i % 1000 == 0) {
                System.out.println(i + " of " + propRefs.size() + ": " + (double) i / ((double) propRefs.size()));
            }
        }

        System.out.println("Renaming decls...");
        i = 0;
        for(Map.Entry<LSFPropDeclaration, String> e : propertyDecls.entrySet()) {
            LSFPropDeclaration decl = e.getKey();
            String newName = e.getValue();

            if (decl instanceof LSFGlobalPropDeclaration) {
                LSFGlobalPropDeclaration globalDecl = (LSFGlobalPropDeclaration) decl;
                if (globalDecl.isDataStoredProperty()) {
                    String oldName = decl.getDeclName();
                    if (!oldName.equals(newName)) {
                        migrations.add(new PropertyMigration(globalDecl, oldName, newName));
                    }
                }
            }
            
            i++;
            decl.setName(newName, transaction);
            if (i % 1000 == 0) {
                System.out.println(i + " of " + propertyDecls.size() + ": " + (double) i / ((double) propertyDecls.size()));
            }
        }

        System.out.println("Unqualifying refs...");
        i = 0;
        for(ExtPropRef possibleConflict : propRefs) {
            i++;
            unqualifyConflict(possibleConflict.ref, possibleConflict.decl, possibleConflict.classes, transaction);
            if (i % 1000 == 0) {
                System.out.println(i + " of " + propRefs.size() + ": " + (double) i / ((double) propRefs.size()));
            }
        }
    }

    static void unqualifyConflict(LSFPropReference ref, LSFPropDeclaration decl, List<LSFClassSet> explicitClasses, MetaTransaction transaction) {
        PropInMetaRef excRef = null;
        for(PropInMetaRef exception : exceptions)
            if(exception.equalsRef(ref, decl)) {
                excRef = exception;
                break;
            }
        
        if(decl instanceof LSFFullNameDeclaration && ref.getFullNameRef() != null && (excRef == null || !excRef.qualNamespace)) {
            ref.dropFullNameRef(transaction);
            if(ref.resolveDecl() != decl)
                ref.setFullNameRef(((LSFFullNameDeclaration) decl).getNamespaceName(), transaction);
        }
        
        if(ref.getExplicitClasses() != null && !ref.isNoContext() && (excRef == null || !excRef.qualClasses)) {
            List<LSFClassSet> declClasses = decl.resolveParamClasses();
            if(declClasses != null)
                ref.dropExplicitClasses(transaction);
    
            if(ref.resolveDecl() != decl)
                ref.setExplicitClasses(explicitClasses, transaction);
        }
    }

    public static void unqualifyRefs(Collection<LSFFile> files, MetaTransaction transaction) {
        System.out.println("Collecting and resolving refs...");
        int i = 0;
        for(LSFFile file : files) {
            i++;
            for(LSFPropReference ref : PsiTreeUtil.findChildrenOfType(file, LSFPropReference.class)) {
                if(ref.getExplicitClasses()!=null || ref.getFullNameRef()!=null) {
                    LSFPropDeclaration decl = ref.resolveDecl();
                    if(decl!=null) {
                        List<LSFClassSet> declClasses = decl.resolveParamClasses();
                        if(declClasses!=null && declClasses.size() > 0)
                            unqualifyConflict(ref, decl, declClasses, transaction);
                    }
                }
            }
            System.out.println((double)i/((double)files.size()));
        }
    }

    private static class ExtPropRef {
        public final LSFPropReference ref;
        public final LSFPropDeclaration decl;
        public final List<LSFClassSet> classes;
        public final boolean qualClasses;
        public final boolean qualNamespace;

        private ExtPropRef(LSFPropReference ref, LSFPropDeclaration decl, List<LSFClassSet> classes, boolean qualClasses, boolean qualNamespace) {
            this.ref = ref;
            this.decl = decl;
            this.classes = classes;
            this.qualClasses = qualClasses;
            this.qualNamespace = qualNamespace;
        }
    }

    private static class ExtPropDrawRef {
        public final LSFPropertyDrawReference ref;
        public final LSFPropertyDrawDeclaration decl;

        private ExtPropDrawRef(LSFPropertyDrawReference ref, LSFPropertyDrawDeclaration decl) {
            this.ref = ref;
            this.decl = decl;
        }
    }

    private static abstract class NameRef<D extends LSFDeclaration, R extends LSFReference<D>> {
        protected String name;
        
        public boolean equalsDecl(D decl) {
            return name == null || decl.getDeclName().equals(name);
        }
        
        protected boolean isEmpty() { // оптимизация
            return name == null;
        }
        public boolean equalsRef(R ref, D decl) {
            if(name!=null && !name.equals(ref.getNameRef())) // оптимизация
                return false;
            
            if(isEmpty()) // оптимизация
                return true;
            
            if(decl==null) // оптимизация 
                decl = ref.resolveDecl();
            return decl != null && equalsDecl(decl);
        }

        protected NameRef(String name) {
            this.name = name;
        }
    }

    private static class FullNameRef<D extends LSFDeclaration, FD extends LSFFullNameDeclaration<FD, ?>, R extends LSFFullNameReference<D, FD>> extends NameRef<D, R> {
        protected String namespace;

        private FullNameRef(String name, String namespace) {
            super(name);
            this.namespace = namespace;
        }

        protected boolean isFullName(D decl) {
            return true;
        }

        public boolean equalsFullDecl(FD decl) {
            return namespace == null || namespace.equals(decl.getNamespaceName());
        }

        @Override
        protected boolean isEmpty() {
            return super.isEmpty() && namespace == null;
        }

        @Override
        public boolean equalsDecl(D decl) {
            return super.equalsDecl(decl) && (isFullName(decl) && equalsFullDecl((FD) decl));
        }
    }

    private static class ClassRef extends FullNameRef<LSFClassDeclaration, LSFClassDeclaration, LSFClassReference> {
        
        public boolean equalsValueClass(LSFValueClass valueClass) {
            if(valueClass instanceof LSFClassDeclaration)
                return equalsDecl((LSFClassDeclaration)valueClass);

            if(name == null)
                return true;

            if(valueClass instanceof ConcatenateClassSet)
                return name.equals("STRUCT");
            if(valueClass instanceof DataClass)
                return name.equals(((DataClass) valueClass).getName());
            
            throw new UnsupportedOperationException();
        }

        private ClassRef(String name, String namespace) {
            super(name, namespace);
        }
    }

    private static class MetacodeRef extends FullNameRef<LSFMetaDeclaration, LSFMetaDeclaration, LSFMetaReference> {
        private Integer metaCount;

        @Override
        public boolean equalsFullDecl(LSFMetaDeclaration decl) {
            return super.equalsFullDecl(decl) && (metaCount == null || decl.getParamCount() == metaCount);
        }

        private MetacodeRef(String name, String namespace, Integer metaCount) {
            super(name, namespace);
            this.metaCount = metaCount;
        }

        @Override
        protected boolean isEmpty() {
            return super.isEmpty() && metaCount == null;
        }
    }

    private static class PropRef extends FullNameRef<LSFPropDeclaration, LSFGlobalPropDeclaration, LSFPropReference> {
        private List<ClassRef> classes;
        private Boolean isImplement;

        @Override
        protected boolean isFullName(LSFPropDeclaration decl) {
            return decl instanceof LSFGlobalPropDeclaration;
        }

        @Override
        public boolean equalsRef(LSFPropReference ref, LSFPropDeclaration decl) {
            if(isImplement != null && (isImplement != ref.isImplement()))
                return false;
            return super.equalsRef(ref, decl);
        }

        @Override
        public boolean equalsFullDecl(LSFGlobalPropDeclaration decl) {
            if(!super.equalsFullDecl(decl))
                return false;
            
            if(classes == null)
                return true;

            List<LSFClassSet> paramClasses = decl.resolveParamClasses();
            if(paramClasses == null || paramClasses.size() != classes.size())
                return false;
                
            for(int i=0;i<classes.size();i++)
                if(!classes.get(i).equalsValueClass(paramClasses.get(i).getCommonClass()))
                    return false;
            
            return true;
        }

        private PropRef(String name, String namespace, List<ClassRef> classes, Boolean implement) {
            super(name, namespace);
            this.classes = classes;
            isImplement = implement;
        }

        @Override
        protected boolean isEmpty() {
            return super.isEmpty() && classes == null; 
        }
    }

    private static class PropInMetaRef {
        private PropRef propRef;
        private MetacodeRef metaRef;
        
        public boolean equalsRef(LSFPropReference prop, LSFPropDeclaration decl) {
            if(!propRef.equalsRef(prop, decl))
                return false;
            
            if(metaRef!=null) {
                LSFMetaCodeStatement inMeta = PsiTreeUtil.getParentOfType(prop, LSFMetaCodeStatement.class);
                if(inMeta == null || !metaRef.equalsRef(inMeta, null))
                    return false;                    
            }
            
            return true;
        }
        
        private boolean qualNamespace;
        private boolean qualClasses;

        private PropInMetaRef(PropRef propRef, MetacodeRef metaRef, boolean qualNamespace, boolean qualClasses) {
            this.propRef = propRef;
            this.metaRef = metaRef;
            this.qualNamespace = qualNamespace;
            this.qualClasses = qualClasses;
        }
    }
}
