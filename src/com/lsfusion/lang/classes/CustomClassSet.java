package com.lsfusion.lang.classes;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.lsfusion.design.model.PropertyDrawView;
import com.lsfusion.lang.psi.LSFGlobalResolver;
import com.lsfusion.lang.psi.cache.ChildrenCache;
import com.lsfusion.lang.psi.cache.ParentsCache;
import com.lsfusion.lang.psi.declarations.LSFClassDeclaration;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import com.lsfusion.lang.psi.extend.LSFClassExtend;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import com.lsfusion.refactoring.ClassCanonicalNameUtils;
import com.lsfusion.util.BaseUtils;

import java.awt.*;
import java.util.List;
import java.util.*;

public class CustomClassSet implements LSFClassSet {

    private final Set<LSFClassDeclaration> classes;

    public CustomClassSet(LSFClassDeclaration classDecl) {
        this.classes = Collections.singleton(classDecl);
    }

    public CustomClassSet(Set<LSFClassDeclaration> classes) {
        this.classes = classes;
    }

    public Set<LSFClassDeclaration> getClasses() {
        return classes;
    }

    public CustomClassSet op(CustomClassSet set, boolean or) {
        if (or) {
            Set<LSFClassDeclaration> orSet = new HashSet<>(classes);
            orSet.addAll(set.classes);
            return new CustomClassSet(orSet);
        } else {
            Set<LSFClassDeclaration> andSet = new HashSet<>();
            for (LSFClassDeclaration aClass : classes)
                for (LSFClassDeclaration bClass : set.classes)
                    andSet.addAll(commonChilds(aClass, bClass));
            return new CustomClassSet(andSet);
        }
    }

    public static Collection<LSFValueClass> getClassParentsRecursively(LSFValueClass valueClass) {
        Set<LSFValueClass> result = new LinkedHashSet<>();
        result.add(valueClass);
        if (valueClass instanceof LSFClassDeclaration) {
            result.addAll(getParentsRecursively((LSFClassDeclaration) valueClass));
        }
        return result;
    }

    public static Collection<LSFClassDeclaration> getParentsRecursively(LSFClassDeclaration decl) {
        Set<LSFClassDeclaration> result = new HashSet<>();
        for (LSFClassDeclaration parent : getParents(decl)) {
            result.add(parent);
            result.addAll(getParentsRecursively(parent));
        }
        return result;
    }

    public static Collection<LSFClassDeclaration> getParents(LSFClassDeclaration decl) {
        return ParentsCache.getInstance(decl.getProject()).getParentsWithCaching(decl);
    }

    public static Collection<LSFClassDeclaration> getParentsNoCache(LSFClassDeclaration decl) {
        Set<LSFClassDeclaration> result = new HashSet<>();
        for (LSFClassExtend extDecl : LSFGlobalResolver.findParentExtends(decl))
            result.addAll(extDecl.resolveExtends());
        return result;
    }

    public static Collection<LSFClassDeclaration> getChildren(LSFClassDeclaration decl, GlobalSearchScope scope) {
        Project project = decl.getProject();
        if (scope != null && scope.equals(GlobalSearchScope.allScope(project)))
            return getChildrenAll(decl);
        return LSFGlobalResolver.findChildrenExtends(decl, project, scope);
    }

    public static Collection<LSFClassDeclaration> getChildrenAll(LSFClassDeclaration decl) {
        return ChildrenCache.getInstance(decl.getProject()).getChildrenWithCaching(decl);
    }

    public static Collection<LSFClassDeclaration> getChildrenAllNoCache(LSFClassDeclaration decl) {
        Project project = decl.getProject();
        return LSFGlobalResolver.findChildrenExtends(decl, project, GlobalSearchScope.allScope(project));
    }

    public LSFClassDeclaration getCommonChild(CustomClassSet set, GlobalSearchScope scope) {
        for (LSFClassDeclaration setClass : set.classes) // оптимизация
            if (containsAll(setClass))
                return setClass;
        for (LSFClassDeclaration setClass : classes) // оптимизация
            if (set.containsAll(setClass))
                return setClass;

        Map<LSFClassDeclaration, Boolean> map = new HashMap<>();
        for (LSFClassDeclaration aClass : classes)
            map.put(aClass, true);
        for (LSFClassDeclaration aClass : set.classes)
            map.put(aClass, false);

        int i = 0;
        List<LSFClassDeclaration> list = new ArrayList<>();
        list.addAll(classes);
        list.addAll(set.classes);
        while (i < list.size()) {
            LSFClassDeclaration decl = list.get(i);
            boolean side = map.get(decl);

            for (LSFClassDeclaration child : getChildren(decl, scope)) {
                Boolean childSide = map.get(child);
                if (childSide != null && side != childSide)
                    return child;
                if (childSide == null) {
                    list.add(child);
                    map.put(child, side);
                }
            }

            i++;
        }
        return null;
    }

    private static boolean isObject(LSFClassDeclaration decl) {
        return decl.getGlobalName().equals("Object") && decl.getLSFFile().getModuleDeclaration().getGlobalName().equals("System");
    }

    public boolean containsAll(LSFClassDeclaration decl) {
        for (LSFClassDeclaration declClass : classes)
            if (isObject(declClass))
                return true;

        return recContainsAll(decl, new HashSet<LSFClassDeclaration>());
    }

    public boolean recContainsAll(LSFClassDeclaration decl, Set<LSFClassDeclaration> recursionGuard) {
        if (!recursionGuard.add(decl))
            return false;

        if (classes.contains(decl))
            return true;

        for (LSFClassDeclaration inhDecl : getParents(decl))
            if (recContainsAll(inhDecl, recursionGuard))
                return true;
        return false;
    }

    public boolean containsAll(CustomClassSet set) {
        for (LSFClassDeclaration setClass : set.classes)
            if (!containsAll(setClass))
                return false;
        return true;
    }

    public boolean equals(Object o) {
        return this == o || o instanceof CustomClassSet && classes.equals(((CustomClassSet) o).classes);
    }

    public int hashCode() {
        return classes.hashCode();
    }

    // АЛГОРИТМ из сервера

    private static int getCheck(LSFClassDeclaration decl, Map<LSFClassDeclaration, Integer> checks) {
        Integer result = checks.get(decl);
        if (result == null)
            return 0;
        return result;
    }

    public static Set<LSFClassDeclaration> commonParents(LSFClassDeclaration decl, LSFClassDeclaration toCommon) {
        Map<LSFClassDeclaration, Integer> checks = new HashMap<>();

        GlobalSearchScope scope = GlobalSearchScope.allScope(decl.getProject());
        commonClassSet1(decl, checks, true, scope);
        commonClassSet2(toCommon, checks, false, null, true, scope);

        Set<LSFClassDeclaration> result = new HashSet<>();
        commonClassSet3(decl, checks, result, null, true, scope);
        return result;
    }

    public static Set<LSFClassDeclaration> commonChilds(LSFClassDeclaration decl, LSFClassDeclaration toCommon) {
        if (isObject(decl))
            return Collections.singleton(toCommon);
        if (isObject(toCommon))
            return Collections.singleton(decl);

        Map<LSFClassDeclaration, Integer> checks = new HashMap<>();

        GlobalSearchScope scope = GlobalSearchScope.allScope(decl.getProject());
        commonClassSet1(decl, checks, false, scope);
        commonClassSet2(toCommon, checks, false, null, false, scope);

        Set<LSFClassDeclaration> result = new HashSet<>();
        commonClassSet3(decl, checks, result, null, false, scope);
        return result;
    }


    // 1-й шаг расставляем пометки 1
    protected static void commonClassSet1(LSFClassDeclaration decl, Map<LSFClassDeclaration, Integer> checks, boolean up, GlobalSearchScope scope) {
        int check = getCheck(decl, checks);

        if (check == 1) return;
        checks.put(decl, 1);
        for (LSFClassDeclaration child : (up ? getParents(decl) : getChildren(decl, scope)))
            commonClassSet1(child, checks, up, scope);
    }

    // 2-й шаг пометки
    // 2 - верхний общий класс
    // 3 - просто общий класс
    protected static void commonClassSet2(LSFClassDeclaration decl, Map<LSFClassDeclaration, Integer> checks, boolean set, Set<LSFClassDeclaration> free, boolean up, GlobalSearchScope scope) {
        int check = getCheck(decl, checks);

        if (!set) {
            if (check > 0) {
                if (check != 1) return;
                checks.put(decl, 2);
                set = true;
            } else if (free != null) free.add(decl);
        } else {
            if (check == 3 || check == 2) {
                checks.put(decl, 3);
                return;
            }

            checks.put(decl, 3);
        }

        for (LSFClassDeclaration child : (up ? getParents(decl) : getChildren(decl, scope)))
            commonClassSet2(child, checks, set, free, up, scope);
    }

    // 3-й шаг выводит в Set, и сбрасывает пометки
    protected static void commonClassSet3(LSFClassDeclaration decl, Map<LSFClassDeclaration, Integer> checks, Set<LSFClassDeclaration> common, Set<LSFClassDeclaration> free, boolean up, GlobalSearchScope scope) {
        int check = getCheck(decl, checks);

        if (check == 0) return;
        if (common != null && check == 2) common.add(decl);
        if (free != null && check == 1) free.add(decl);

        checks.put(decl, 0);

        for (LSFClassDeclaration child : (up ? getParents(decl) : getChildren(decl, scope)))
            commonClassSet3(child, checks, common, free, up, scope);
    }

    private static void addMinInt(Map<LSFClassDeclaration, Integer> mPathes, LSFClassDeclaration decl, Integer add) {
        Integer value = mPathes.get(decl);
        if (value != null)
            add = BaseUtils.min(add, value);
        mPathes.put(decl, add);
    }

    // возвращает до каких путей можно дойти и с каким минимальным путем
    private static Map<LSFClassDeclaration, Integer> recCommonClass(LSFClassDeclaration customClass, Set<LSFClassDeclaration> used, Set<LSFClassDeclaration> commonSet, Map<LSFClassDeclaration, Map<LSFClassDeclaration, Integer>> mPathes, Set<LSFClassDeclaration> firstFulls, Set<LSFClassDeclaration> firstChildren) {
        Map<LSFClassDeclaration, Integer> cachedResult = mPathes.get(customClass);
        if (cachedResult != null)
            return cachedResult;

        Map<LSFClassDeclaration, Integer> childPathes = new HashMap<>();
        if (commonSet.contains(customClass))
            addMinInt(childPathes, customClass, 0);

        boolean hasFullChild = false;
        for (LSFClassDeclaration childClass : (firstChildren != null ? firstChildren : getChildren(customClass, GlobalSearchScope.allScope(customClass.getProject()))))
            if (used.contains(childClass)) {
                Map<LSFClassDeclaration, Integer> recChildPathes = recCommonClass(childClass, used, commonSet, mPathes, firstFulls, null);
                hasFullChild = hasFullChild || recChildPathes.keySet().containsAll(commonSet);
                for (Map.Entry<LSFClassDeclaration, Integer> recChildPath : recChildPathes.entrySet())
                    addMinInt(childPathes, recChildPath.getKey(), recChildPath.getValue() + 1);
            } else
                addMinInt(childPathes, childClass, 1);

        if (!hasFullChild && childPathes.keySet().containsAll(commonSet))
            firstFulls.add(customClass);
        mPathes.put(customClass, childPathes);
        return childPathes;
    }

    public static void fillParents(LSFClassDeclaration decl, Set<LSFClassDeclaration> parentSet, Set<LSFClassDeclaration> topParents) {
        if (!parentSet.add(decl)) return;

        Collection<LSFClassDeclaration> declParents = getParents(decl);
        if (declParents.size() == 0)
            topParents.add(decl);
        for (LSFClassDeclaration parent : declParents)
            fillParents(parent, parentSet, topParents);
    }

    public static LSFClassDeclaration getBaseClass(Project project) {
        LSFModuleDeclaration systemModule = LSFGlobalResolver.findModules("System", GlobalSearchScope.allScope(project)).findFirst();
        return LSFGlobalResolver.findSystemElement("Object", systemModule.getLSFFile(), LSFStubElementTypes.CLASS);
    }

    @Override
    public LSFClassDeclaration getCommonClass() {
        final Set<LSFClassDeclaration> commonSet;
        commonSet = classes;

        LSFClassDeclaration firstClass = commonSet.iterator().next();
        if (commonSet.size() == 1) // иначе firstFulls не заполнится
            return firstClass;

        LSFClassDeclaration baseClass = getBaseClass(firstClass.getProject()); // базовая вершина

        Set<LSFClassDeclaration> used = new HashSet<>();
        Set<LSFClassDeclaration> topParents = new HashSet<>();
        for (LSFClassDeclaration commonClass : commonSet) // ищем все использованные вершины
            fillParents(commonClass, used, topParents);

        Map<LSFClassDeclaration, Map<LSFClassDeclaration, Integer>> pathes = new HashMap<>();
        final Set<LSFClassDeclaration> firstFulls = new HashSet<>();
        recCommonClass(baseClass, used, commonSet, pathes, firstFulls, topParents);

        final Map<LSFClassDeclaration, Integer> pathCounts = new HashMap<>();
        for (Map.Entry<LSFClassDeclaration, Map<LSFClassDeclaration, Integer>> path : pathes.entrySet()) {
            LSFClassDeclaration key = path.getKey();
            Map<LSFClassDeclaration, Integer> value = path.getValue();

            assert !firstFulls.contains(key) || value.keySet().containsAll(commonSet);
            int countCommon = 0;
            int countOthers = 0;
            for (Map.Entry<LSFClassDeclaration, Integer> entry : value.entrySet()) {
                LSFClassDeclaration customClass = entry.getKey();
                if (commonSet.contains(customClass))
                    countCommon += entry.getValue();
                else
                    countOthers += entry.getValue();
            }

            pathCounts.put(path.getKey(), countOthers * 1000 + countCommon);
        }

        List<LSFClassDeclaration> sortFirstFulls = new ArrayList<>(firstFulls);
        Collections.sort(sortFirstFulls, new Comparator<LSFClassDeclaration>() {
            public int compare(LSFClassDeclaration o1, LSFClassDeclaration o2) {
                int cnt1 = pathCounts.get(o1);
                int cnt2 = pathCounts.get(o2);
                if (cnt1 > cnt2)
                    return 1;
                if (cnt1 < cnt2)
                    return -1;
                return o1.getGlobalName().compareTo(o2.getGlobalName());
            }
        });
        return sortFirstFulls.iterator().next();
    }

    public LSFClassSet op(LSFClassSet set, boolean or, boolean string) {
        if (!(set instanceof CustomClassSet))
            return null;
        return op((CustomClassSet) set, or);
    }

    public boolean containsAll(LSFClassSet set, boolean implicitCast) {
        if (!(set instanceof CustomClassSet))
            return false;
        return containsAll((CustomClassSet) set);
    }

    @Override
    public boolean haveCommonChildren(LSFClassSet set, GlobalSearchScope scope) {
        if (!(set instanceof CustomClassSet))
            return false;
        return getCommonChild((CustomClassSet) set, scope) != null;
    }

    @Override
    public boolean isCompatible(LSFClassSet set) {
        return set instanceof CustomClassSet && haveCommonChildren(set, null);
    }

    @Override
    public boolean isAssignable(LSFClassSet set) {
        return isCompatible(set);
    }

    @Override
    public boolean isFlex() {
        return false;
    }

    @Override
    public String toString() {
        String result = "";
        for (Iterator<LSFClassDeclaration> iterator = classes.iterator(); iterator.hasNext(); ) {
            result += iterator.next().getDeclName() + (iterator.hasNext() ? ", " : "");
        }
        return result;
    }

    @Override
    public int getDefaultWidth(FontMetrics fontMetrics, PropertyDrawView propertyDraw) {
        return getFullWidthString("0000000", fontMetrics);
    }

    @Override
    public int getFullWidthString(String widthString, FontMetrics fontMetrics) {
        return fontMetrics.stringWidth(widthString) + 8;
    }

    @Override
    public int getDefaultHeight(FontMetrics fontMetrics, int charHeight) {
        return fontMetrics.getHeight() * charHeight + 1;
    }

    @Override
    public String getCanonicalName() {
        return ClassCanonicalNameUtils.createName(this);
    }

    public ExtInt getCharLength() {
        return new ExtInt(10);
    }
}
