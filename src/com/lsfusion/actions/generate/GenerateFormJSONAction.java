package com.lsfusion.actions.generate;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.vfs.VirtualFile;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GenerateFormJSONAction extends GenerateFormAction {

    protected Object getRootElement(AnActionEvent e) throws IOException {
        final FileChooserDescriptor fileChooser = FileChooserDescriptorFactory.createSingleFileDescriptor("json");
        VirtualFile file = FileChooser.chooseFile(fileChooser, e.getProject(), null);
        return file != null ? new JSONObject(new String(Files.readAllBytes(Paths.get(file.getPath())))) : null;
    }

    @Override
    protected ParseNode generateHierarchy(Object element, String key) {
        if (element instanceof JSONArray) {

            List<ParseNode> children = new ArrayList<>();
            for (Object obj : (JSONArray) element) {
                children.add(generateHierarchy(obj, key));
            }

            ParseNode mergedChild = deepMerge(children);
            boolean integrationKey = mergedChild != null && mergedChild.children.size() > 0;

            List<ParseNode> nChildren = new ArrayList<>();
            if(mergedChild != null) {
                nChildren.add(mergedChild);
            }

            return new GroupObjectParseNode(key, nChildren, null, integrationKey);

        } else if (element instanceof JSONObject) {

            List<ParseNode> localChildren = new ArrayList<>();

            Iterator<String> elementIterator = ((JSONObject) element).keys();
            while (elementIterator.hasNext()) {
                String childElementKey = elementIterator.next();
                Object childElementValue = ((JSONObject) element).get(childElementKey);

                localChildren.add(generateHierarchy(childElementValue, childElementKey));

            }

            return new PropertyGroupParseNode(key, localChildren, null, null);

        } else {
            return new PropertyParseNode(key, false);
        }


    }
}
