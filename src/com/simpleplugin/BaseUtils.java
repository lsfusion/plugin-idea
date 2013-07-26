package com.simpleplugin;

import com.intellij.util.Query;

import java.util.Collection;
import java.util.List;

public class BaseUtils {
    public static <G, I extends G> List<G> immutableCast(List<I> object) {
        return (List<G>) object;
    }

    public static <G, I extends G> Collection<G> immutableCast(Collection<I> object) {
        return (Collection<G>) object;
    }

    public static <G, I extends G> Query<G> immutableCast(Query<I> object) {
        return (Query<G>) object;
    }

}
