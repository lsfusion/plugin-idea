package com.lsfusion.refactoring;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.listeners.RefactoringElementListener;
import com.intellij.refactoring.rename.RenamePsiElementProcessor;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.MultiMap;
import com.lsfusion.lang.LSFFileType;
import com.lsfusion.lang.classes.*;
import com.lsfusion.lang.meta.MetaChangeDetector;
import com.lsfusion.lang.meta.MetaTransaction;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.*;
import com.lsfusion.lang.psi.references.*;
import com.lsfusion.util.BaseUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.lsfusion.util.LSFPsiUtils.collectInjectedLSFFiles;

public class LSFRenameFullNameProcessor extends RenamePsiElementProcessor {

    @Override
    public boolean canProcessElement(@NotNull PsiElement element) {
        return element instanceof LSFId || getFullNameDecl(element) != null;
    }
    
    private static LSFFullNameDeclaration getFullNameDecl(PsiElement element) {
        return PsiTreeUtil.getParentOfType(element, LSFFullNameDeclaration.class);
    }
    
/*    private static class PossibleConflict extends UsageInfo {
        private PossibleConflict(@NotNull PsiReference reference) {
            super(reference);
        }
    }

    @Override
    public void findCollisions(PsiElement element, String newName, Map<? extends PsiElement, String> allRenames, List<UsageInfo> result) {
        LSFFullNameDeclaration decl = getFullNameDecl(element);
        
        for(LSFFullNameReference possibleConflict : LSFResolver.findFullNameUsages(newName, decl))
            result.add(new PossibleConflict(possibleConflict));
    }*/

    @Override
    public void prepareRenaming(PsiElement element, String newName, Map<PsiElement, String> allRenames) {
        LSFPropertyDeclaration propDecl = PsiTreeUtil.getParentOfType(element, LSFPropertyDeclaration.class);
        if (propDecl != null) {
            //переименование свойства => нужно переименовать и соответствующие propertyDraw

            GlobalSearchScope scope = GlobalSearchScope.projectScope(element.getProject());
            Collection<PsiReference> refs = ReferencesSearch.search(element, scope, false).findAll();
            for (PsiReference ref : refs) {
                PsiElement refElement = ref.getElement();
                LSFPropertyDrawDeclaration propDrawDecl = PsiTreeUtil.getParentOfType(refElement, LSFPropertyDrawDeclaration.class);
                //ищем propertyDraw без alias'а
                if (propDrawDecl != null && propDrawDecl.getSimpleName() == null) {
                    LSFPropertyUsage propertyUsage = propDrawDecl.getFormPropertyName().getPropertyUsage();
                    if (propertyUsage != null) {
                        LSFSimpleName propUsageId = propertyUsage.getCompoundID().getSimpleName();
                        allRenames.put(propUsageId, newName);
                    }
                }
            }
        } else {
            LSFPropertyDrawDeclaration propDrawDecl = PsiTreeUtil.getParentOfType(element, LSFPropertyDrawDeclaration.class);
            if (propDrawDecl != null && propDrawDecl.getSimpleName() == null) {
                //переименование propertyDraw без alias => нужно переименовать и соответствующее свойство
                LSFPropertyUsage propertyUsage = propDrawDecl.getFormPropertyName().getPropertyUsage();
                if (propertyUsage != null) {
                    LSFPropDeclaration decl = propertyUsage.resolveDecl();
                    if (decl != null) {
                        allRenames.put(decl.getNameIdentifier(), newName);
                    }
                }
            }
        }
    }

    @Override
    public void renameElement(PsiElement element, String newName, UsageInfo[] usages, @Nullable RefactoringElementListener listener) throws IncorrectOperationException {
        LSFFullNameDeclaration decl = getFullNameDecl(element);
        if(decl != null) {
            List<Pair<LSFFullNameReference, LSFDeclaration>> possibleConflicts = new ArrayList<Pair<LSFFullNameReference, LSFDeclaration>>();
            for (LSFFullNameReference possibleConflict : LSFResolver.findFullNameUsages(newName, decl)) {
                possibleConflicts.add(new Pair<LSFFullNameReference, LSFDeclaration>(possibleConflict, possibleConflict.resolveDecl()));
            }

            for (UsageInfo usage : usages) {
                LSFFullNameReference reference = (LSFFullNameReference) usage.getReference();
                if (reference != null) {
//                if(usage instanceof PossibleConflict) {
//                    LSFDeclaration refDecl = reference.resolveDecl();
//                    possibleConflicts.add(new Pair<LSFFullNameReference, LSFDeclaration>(reference, refDecl));
//                } else
                    possibleConflicts.add(new Pair<LSFFullNameReference, LSFDeclaration>((LSFFullNameReference) reference.handleElementRename(newName), decl));
                }
            }

            decl.setName(newName);

            for (Pair<LSFFullNameReference, LSFDeclaration> possibleConflict : possibleConflicts)
                qualifyPossibleConflict(possibleConflict.first, possibleConflict.second, null);

            if (listener != null)
                listener.elementRenamed(decl);
        } else {
            super.renameElement(element, newName, usages, listener);
        }
    }
                                            
    private static void qualifyPossibleConflict(LSFFullNameReference ref, LSFDeclaration decl, MetaTransaction transaction) {
        if(ref.resolveDecl() == decl)
            return;
        
        // работаем везде на "горячем" PSI (то есть без копий, dummy и т.п.)

        if(ref instanceof LSFPropReference && ((LSFPropReference)ref).getExplicitClasses()==null) {
            LSFPropDeclaration propDecl = (LSFPropDeclaration) decl;
            List<LSFClassSet> declClasses = propDecl.resolveParamClasses();
            if(declClasses != null)
                ((LSFPropReference)ref).setExplicitClasses(declClasses, transaction);

            if(ref.resolveDecl() == decl)
                return;
        }

        if(decl instanceof LSFFullNameDeclaration && ref.getFullNameRef() == null) {
            ref.setFullNameRef(((LSFFullNameDeclaration)decl).getNamespaceName(), transaction);

            if(ref.resolveDecl() == decl)
                return;
        }

        Notifications.Bus.notify(new Notification("rename", "Rename", "Cannot qualify reference " + ref.getText() + " to declaration" + (decl == null ? "null" : decl.getText()), NotificationType.ERROR));
    }
    
    @Override
    public void findExistingNameConflicts(PsiElement element, String newName, MultiMap<PsiElement, String> conflicts) {
        // todo: implement
    }
    
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
    
    public static void shortenAllPropNames(Project project) {
        shortenAllPropNames(project, MigrationVersionChangePolicy.CHANGE_IF_COMMITED);
    }
    
    public static void shortenAllPropNames(Project project, MigrationVersionChangePolicy migrationVersionChangePolicy) {
        //todo: use migrationVersionChangePolicy
        
        
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

        MetaTransaction transaction = new MetaTransaction();

        shortenAllPropNames(files, transaction);

        transaction.apply();
    }

    public static void shortenAllPropNames(Collection<LSFFile> files, MetaTransaction transaction) {

        MetaChangeDetector.getInstance(files.iterator().next().getProject()).setMetaEnabled(false, false);

        Map<LSFPropDeclaration, String> propertyDecls = new HashMap<LSFPropDeclaration, String>();
        Map<LSFPropertyDrawDeclaration, String> propertyDrawDecls = new HashMap<LSFPropertyDrawDeclaration, String>();
        
        System.out.println("Collecting property decls...");
        int i = 0;
        for(LSFFile file : files) {
            i++;
            for(LSFPropDeclaration decl : PsiTreeUtil.findChildrenOfType(file, LSFPropDeclaration.class))
                propertyDecls.put(decl, shortenName(decl));
            System.out.println((double)i/((double)files.size()));
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
            System.out.println((double) i / ((double) files.size()));
        }
        
        System.out.println("Collecting property draw decls...");
        i = 0;
        for(ExtPropRef propRef : propRefs) {
            i++;

            LSFPropertyDrawDeclaration drawDecl = PsiTreeUtil.getParentOfType(propRef.ref.getElement(), LSFPropertyDrawDeclaration.class);
            if (drawDecl != null) {
                propertyDrawDecls.put(drawDecl, propertyDecls.get(propRef.decl));
            }

            System.out.println((double)i/((double)propRefs.size()));
        }


        System.out.println("Collecting and resolving property draw refs...");
        List<ExtPropDrawRef> propDrawRefs = new ArrayList<ExtPropDrawRef>();
        i = 0;
        for(LSFFile file : files) {
            i++;
            for(LSFPropertyDrawReference ref : PsiTreeUtil.findChildrenOfType(file, LSFPropertyDrawReference.class)) {
                if (ref.getAliasUsage() == null) {
                    LSFPropertyDrawDeclaration decl = ref.resolveDecl();
                    if (decl != null) {
                        propDrawRefs.add(new ExtPropDrawRef(ref, decl));
                    }
                }
            }
            System.out.println((double)i/((double)files.size()));
        }

        System.out.println("Renaming property draw refs...");
        i = 0;
        for(ExtPropDrawRef propDrawRef : propDrawRefs) {
            i++;

            propDrawRef.ref.handleElementRename(propertyDrawDecls.get(propDrawRef.decl), transaction);

            System.out.println((double)i/((double)propRefs.size()));
        }

        System.out.println("Renaming and qualifying property refs...");
        i = 0;
        for(ExtPropRef resolvedRef : propRefs) {
            i++;

            String newPropName = propertyDecls.get(resolvedRef.decl);

            LSFPropReference propRef = resolvedRef.ref;
            PsiElement propRefElement = propRef.getElement();

            LSFPropertyDrawDeclaration drawDecl = PsiTreeUtil.getParentOfType(propRefElement, LSFPropertyDrawDeclaration.class);
            if (drawDecl != null) {
                propertyDrawDecls.put(drawDecl, newPropName);
            }

            propRef.handleElementRename(newPropName, transaction);
            if(!resolvedRef.qualClasses)
                propRef.setExplicitClasses(resolvedRef.classes, transaction);
            if(!resolvedRef.qualNamespace && (resolvedRef.decl instanceof LSFFullNameDeclaration))
                propRef.setFullNameRef(((LSFFullNameDeclaration) resolvedRef.decl).getNamespaceName(), transaction);


            System.out.println((double)i/((double)propRefs.size()));
        }

        System.out.println("Renaming decls...");
        i = 0;
        for(Map.Entry<LSFPropDeclaration, String> decl : propertyDecls.entrySet()) {
            i++;
            decl.getKey().setName(decl.getValue(), transaction);
            System.out.println((double)i/((double)propertyDecls.size()));
        }

        System.out.println("Unqualifying refs...");
        i = 0;
        for(ExtPropRef possibleConflict : propRefs) {
            i++;
            if(possibleConflict.ref.resolveDecl()!=possibleConflict.decl)
                possibleConflict = possibleConflict;
            unqualifyConflict(possibleConflict.ref, possibleConflict.decl, possibleConflict.classes, transaction);
            System.out.println((double)i/((double)propRefs.size()));
        }
    }

    private static void unqualifyConflict(LSFPropReference ref, LSFPropDeclaration decl, List<LSFClassSet> explicitClasses, MetaTransaction transaction) {
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

            if(valueClass instanceof StructClassSet)
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
}
