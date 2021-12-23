package com.lsfusion.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetaSubstitutor {
    public final static MetaSubstitutor EMPTY = new MetaSubstitutor(new ArrayList<>(), new ArrayList<>());

    private final Map<String, String> map = new HashMap<>();
    public MetaSubstitutor(List<String> from, List<String> to) {
        for(int i=0;i<from.size();i++) {
            if(i<to.size())
                map.put(from.get(i), to.get(i));
        }
    }

    public String substitute(String text) {
        for(Map.Entry<String, String> entry : map.entrySet())
            text = text.replace("##"+entry.getKey(), entry.getValue());
        return text;
    }

    public List<String> substitute(List<String> text) {
        List<String> result = new ArrayList<>();
        for(String element: text)
            result.add(substitute(element));
        return result;
    }

}
