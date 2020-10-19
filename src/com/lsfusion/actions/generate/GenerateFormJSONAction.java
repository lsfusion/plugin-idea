package com.lsfusion.actions.generate;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.vfs.VirtualFile;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class GenerateFormJSONAction extends GenerateFormAction {

    protected Object getRootElement(AnActionEvent e) throws IOException {
        final FileChooserDescriptor fileChooser = FileChooserDescriptorFactory.createSingleFileDescriptor("json");
        VirtualFile file = FileChooser.chooseFile(fileChooser, e.getProject(), null);

        if (file != null) {
            Object object = new JSONTokener(new String(Files.readAllBytes(Paths.get(file.getPath())), StandardCharsets.UTF_8)).nextValue();
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
            boolean integrationKey = mergedChild != null && mergedChild.children.size() > 0;

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
            return Collections.singletonList(new PropertyParseNode(key, null, false));
        }


    }
}
