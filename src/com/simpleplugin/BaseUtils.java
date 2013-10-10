package com.simpleplugin;

import com.intellij.util.Query;

import java.util.*;

public class BaseUtils {
    public static <G, I extends G> List<G> immutableCast(List<I> object) {
        return (List<G>) object;
    }

    public static <G, I extends G> Collection<G> immutableCast(Collection<I> object) {
        return (Collection<G>) object;
    }
    
    public static <G, I extends G> Set<G> immutableCast(Set<I> object) {
        return (Set<G>) object;
    }
    
    public static <G, I extends G> Query<G> immutableCast(Query<I> object) {
        return (Query<G>) object;
    }

    public static <T> T immutableCast(Object object) {
        return (T) object;
    }

    public static int max(int a, int b) {
        return a > b ? a : b; 
    }

    public static int min(int a, int b) {
        return a < b ? a : b;
    }

    public static int cmp(int a, int b, boolean max) {
        return max ? max(a, b) : min(a, b);
    }

    public static boolean cmp(boolean a, boolean b, boolean max) {
        return max ? a || b : a && b;
    }
    
    public static <K, V> Map<K, V> filterNullable(Map<K, V> map, Set<K> set) {
        if(set == null)
            return map;
        
        Map<K, V> result = new HashMap<K, V>();
        for(Map.Entry<K, V> entry : map.entrySet())
            if(set.contains(entry.getKey()))
                result.put(entry.getKey(), entry.getValue());
        return result;
    }

    public static <K, V> Map<K, V> override(Map<K, V> map, Map<K, V> override) {
        Map<K, V> result = new HashMap<K, V>(map);
        result.putAll(override);
        return result;
    }
    
    public static <K> K nvl(K el1, K el2) {
        return el1 != null ? el1 : el2; 
    }

    public static <K> Set<K> merge(Set<K> set1, Set<K> set2) {
        Set<K> result = new HashSet<K>(set1);
        result.addAll(set2);
        return result;
    }

    public static <K> List<K> add(List<K> set1, List<K> set2) {
        List<K> result = new ArrayList<K>(set1);
        result.addAll(set2);
        return result;
    }

    public static <K> List<K> add(List<K> set, K element) {
        List<K> result = new ArrayList<K>(set);
        result.add(element);
        return result;
    }

    public static <K> List<K> add(K element, List<K> set) {
        List<K> result = new ArrayList<K>();
        result.add(element);
        result.addAll(set);
        return result;
    }

    public static <K> List<K> toList(K el1, K el2) {
        List<K> result = new ArrayList<K>();
        result.add(el1);
        result.add(el2);
        return result;        
    }

    public static <K> Set<K> remove(Set<? extends K> set, K remove) {
        Set<K> result = new HashSet<K>(set);
        result.remove(remove);
        return result;
    }

    public static <K> Set<K> split(Set<K> set1, Set<K> set2) {

        Set<K> common = new HashSet<K>(); 
                
        Iterator<K> it = set1.iterator();
        while(it.hasNext()) {
            K next = it.next();
            if(set2.remove(next)) {
                common.add(next);
                it.remove();
            }
        }
        return common;
    }

    public static <K> K single(Collection<K> col) {
        assert col.size() == 1;
        return col.iterator().next();
    }
}
