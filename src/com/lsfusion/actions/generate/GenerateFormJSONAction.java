package com.lsfusion.actions.generate;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class GenerateFormJSONAction extends GenerateFormAction {

    @Override
    String getExtension() {
        return "json";
    }

    @Override
    boolean showFullNamespaceCheckBox() {
        return false;
    }

    @Override
    protected Object getRootElement(String file) {
        if (file != null) {
            Object object = new JSONTokener(file).nextValue();
            if (object instanceof JSONObject) {
                return object;
            } else {
                JSONObject virtObject = new JSONObject();
                virtObject.put("value", object);
                return virtObject;
            }
        } else return null;
    }

    @Override
    protected List<ParseNode> generateHierarchy(Object element, String key) {
        if (element instanceof JSONArray) {

            List<ParseNode> children = new ArrayList<>();
            for (Object obj : (JSONArray) element) {
                children.addAll(generateHierarchy(obj, key));
            }

            ParseNode mergedChild = deepMerge(children);
            boolean integrationKey = mergedChild != null && !mergedChild.children.isEmpty();

            List<ParseNode> nChildren = new ArrayList<>();
            if(mergedChild != null) {
                nChildren.add(mergedChild);
            }

            return Collections.singletonList(new GroupObjectParseNode(key, nChildren, null, integrationKey));

        } else if (element instanceof JSONObject) {

            List<ParseNode> localChildren = new ArrayList<>();

            Iterator<String> elementIterator = ((JSONObject) element).keys();
            while (elementIterator.hasNext()) {
                String childElementKey = elementIterator.next();
                Object childElementValue = ((JSONObject) element).get(childElementKey);

                localChildren.addAll(generateHierarchy(childElementValue, childElementKey));

            }

            return Collections.singletonList(new PropertyGroupParseNode(key, localChildren, null, null));

        } else {
            return Collections.singletonList(new PropertyParseNode(key, null, getPropertyType(element), false));
        }


    }
}
