package com.lsfusion.actions.generate;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.lsfusion.util.BaseUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.intellij.testFramework.LightPlatformTestCase.getProject;

public class GenerateFormAction extends AnAction {

    private Set<String> usedObjectIds = new HashSet<>();

    @Override
    public void actionPerformed(final AnActionEvent e) {

        final FileChooserDescriptor fileChooser = FileChooserDescriptorFactory.createSingleFileDescriptor("json");
        FileChooser.chooseFiles(fileChooser, getProject(), null, paths -> {
            if (!paths.isEmpty()) {
                String jsonPath = paths.get(0).getPath();

                try {

                    usedObjectIds = new HashSet<>();

                    JSONObject jsonObject = new JSONObject(new String(Files.readAllBytes(Paths.get(jsonPath))));

                    Form form = generateFormFromCodeBlocks(generateForm(jsonObject, null, null, null, false, null, null, null));

                    String formScript = (form.groupDeclarationScripts.isEmpty() ? "" : (StringUtils.join(form.groupDeclarationScripts, "\n") + "\n\n")) +
                            (form.propertyDeclarationScripts.isEmpty() ? "" : (StringUtils.join(form.propertyDeclarationScripts, "\n") + "\n\n")) +
                            "FORM generated\n" + (form.formScripts.isEmpty() ? "" : StringUtils.join(form.formScripts, "\n")) + ";";

                    JTextPane textPane = new JTextPane();
                    textPane.setText(formScript);
                    textPane.setEditable(false);
                    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                    int width = (int) (screenSize.getWidth() * 0.3);
                    textPane.setSize(new Dimension(width, 10));
                    int height = Math.min((int) (screenSize.getHeight() * 0.8), textPane.getPreferredSize().height);
                    textPane.setPreferredSize((new Dimension(width, height)));

                    textPane.setBackground(null);
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), new JScrollPane(textPane), "Generation result", JOptionPane.PLAIN_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), ex.getMessage(), "Generation failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private Form generateFormFromCodeBlocks(CodeBlock codeBlock) {
        Set<String> groupDeclarationScripts = new LinkedHashSet<>(codeBlock.groupDeclarationScripts);
        Set<String> propertyDeclarationScripts = new LinkedHashSet<>(codeBlock.propertyDeclarationScripts);
        List<String> formScripts = new ArrayList<>(codeBlock.objectsScripts);

        if(codeBlock.propertiesScript != null) {
            formScripts.add(codeBlock.propertiesScript);
        }
        if(codeBlock.filtersScript != null) {
            formScripts.add(codeBlock.filtersScript);
        }

        if(codeBlock.children != null) {
            for(CodeBlock child : codeBlock.children) {
                Form blocks = generateFormFromCodeBlocks(child);
                groupDeclarationScripts.addAll(blocks.groupDeclarationScripts);
                propertyDeclarationScripts.addAll(blocks.propertyDeclarationScripts);
                formScripts.addAll(blocks.formScripts);
            }
        }
        return new Form(groupDeclarationScripts, propertyDeclarationScripts, formScripts);

    }

    private CodeBlock generateForm(Object element, ElementKey key, ElementKey parentKey, ElementKey parentGroupKey, boolean parentIsJSONObject, ElementKey childrenInGroup, ElementKey childrenPropertiesObject, ElementKey grandParentElementKey) {
        Set<String> groupDeclarationScripts = new LinkedHashSet<>();
        Set<String> propertyDeclarationScripts = new LinkedHashSet<>();
        Set<String> objectsScripts = new LinkedHashSet<>();
        Set<ElementKey> properties = new LinkedHashSet<>();
        List<CodeBlock> children = new ArrayList<>();

        //process children
        if(element instanceof JSONArray) {
            //nothing to do
        } else if(element instanceof JSONObject) {

            Iterator<String> elementIterator = ((JSONObject) element).keys();
            while (elementIterator.hasNext()) {
                ElementKey childElementKey = new ElementKey(elementIterator.next());
                Object childElementValue = ((JSONObject) element).get(childElementKey.extID);

                if (childElementValue instanceof JSONArray) {

                    JSONObject mergedObject = deepMerge((JSONArray) childElementValue);

                    CodeBlock codeBlock;

                    if(mergedObject.isEmpty()) { //primitive array

                        String filterProperty = getFilterProperty(key, childElementKey);
                        codeBlock = new CodeBlock(new LinkedHashSet<>(),
                                new LinkedHashSet<>(Arrays.asList(getStringPropertyDeclarationScript(childElementKey, "INTEGER"), getIntegerPropertyDeclarationScript(filterProperty))),
                                new LinkedHashSet<>(Collections.singletonList(getObjectsScript(childElementKey, parentIsJSONObject ? key : null))),
                                getPropertiesScript(childElementKey, null, new LinkedHashSet<>(Collections.singletonList(new ElementKey("value", childElementKey.ID)))),
                                getFiltersScript(filterProperty, childElementKey, parentIsJSONObject ? grandParentElementKey : key), null);

                    } else {

                        codeBlock = generateForm(mergedObject, childElementKey, key, null, false, null, childElementKey, parentKey);

                        codeBlock.objectsScripts.add(getObjectsScript(childElementKey, key));

                        String filterProperty = getFilterProperty(parentKey, childElementKey);
                        if(filterProperty != null) {
                            codeBlock.propertyDeclarationScripts.add(getIntegerPropertyDeclarationScript(filterProperty));
                            codeBlock.filtersScript = getFiltersScript(filterProperty, childElementKey, parentKey);
                        }

                    }

                    children.add(codeBlock);

                } else if(childElementValue instanceof JSONObject) {
                    objectsScripts.add(getObjectsScript(key, parentKey));

                    groupDeclarationScripts.add(getGroupDeclarationScript(childElementKey, parentGroupKey));

                    if(parentKey != null) {
                        groupDeclarationScripts.add(getGroupDeclarationScript(parentKey, null));
                    }

                    children.add(generateForm(childElementValue, childElementKey, key, childElementKey, true, childElementKey, parentIsJSONObject ? grandParentElementKey : key,
                            parentIsJSONObject ? grandParentElementKey : key));
                } else {
                    properties.add(childElementKey);
                    propertyDeclarationScripts.add(getStringPropertyDeclarationScript(childElementKey, key == null ? null : "INTEGER"));
                    children.add(generateForm(childElementValue, childElementKey, key, null, true, null, childElementKey, parentKey));
                }

            }

        } else {
            //nothing to do
        }

        String propertiesScript = getPropertiesScript(childrenPropertiesObject, childrenInGroup, properties);
        return new CodeBlock(groupDeclarationScripts, propertyDeclarationScripts, objectsScripts, propertiesScript, null, children);
    }

    private JSONObject deepMerge(JSONArray array) {
        JSONObject mergedObject = new JSONObject();
        for (Object obj : array) {
            if(obj instanceof JSONObject) {
                mergedObject = deepMerge(mergedObject, (JSONObject) obj);
            }
        }
        return mergedObject;
    }

    private JSONObject deepMerge(JSONObject source, JSONObject target) throws JSONException {
        if(!source.isEmpty()) {
            for (String key : JSONObject.getNames(source)) {
                Object value = source.get(key);
                if (!target.has(key)) {
                    // new value for "key":
                    target.put(key, value);
                } else {
                    // existing value for "key" - recursively deep merge:
                    if (value instanceof JSONObject) {
                        JSONObject valueJson = (JSONObject) value;
                        deepMerge(valueJson, target.getJSONObject(key));
                    } else {
                        target.put(key, value);
                    }
                }
            }
        }
        return target;
    }

    private String getFilterProperty(ElementKey key, ElementKey childKey) {
        return key != null ? (key.ID + BaseUtils.capitalize(childKey.ID)) : null;
    }

    private String getGroupDeclarationScript(ElementKey groupKey, ElementKey parentGroupKey) {
        if(groupKey != null) {
            String extID = groupKey.ID.equals(groupKey.extID) ? "" : (" EXTID '" + groupKey.extID + "'");
            String parent = parentGroupKey == null ? "" : (" : " + parentGroupKey.ID);
            return "GROUP " + groupKey.ID + extID + parent + ";";
        } else return null;
    }

    private String getStringPropertyDeclarationScript(ElementKey propertyKey, String parameter) {
        return getPropertyDeclarationScript(propertyKey.ID, "STRING", parameter);
    }

    private String getIntegerPropertyDeclarationScript(String property) {
        return getPropertyDeclarationScript(property, "INTEGER", "INTEGER");
    }

    private String getPropertyDeclarationScript(String property, String type, String parameter) {
        return property + " = DATA LOCAL " + type + "(" + (parameter != null ? parameter : "") + ");";
    }

    private String generateObjectId(String id) {
        int count = 0;
        while(usedObjectIds.contains(id + (count == 0 ? "" : count))) {
            count++;
        }
        id = id + (count == 0 ? "" : count);
        usedObjectIds.add(id);
        return id;
    }

    private String getObjectsScript(ElementKey elementKey, ElementKey inGroupKey) {
        if(elementKey != null) {
            String extID = elementKey.ID.equals(elementKey.extID) ? "" : (" EXTID '" + elementKey.extID + "'");
            String inGroup = (inGroupKey == null || inGroupKey.ID == null ? "" : (" IN " + inGroupKey.ID));
            return "\nOBJECTS " + elementKey.ID + " = INTEGER" + extID + inGroup;
        } else return null;
    }

    private String getPropertiesScript(ElementKey object, ElementKey inGroup, Set<ElementKey> properties) {
        if(!properties.isEmpty()) {
            return "PROPERTIES(" + (object == null ? "" : object.ID) + ") " + (inGroup == null ? "" : ("IN " + inGroup.ID + " ")) + properties.stream().map(ElementKey::getScript).collect(Collectors.joining(", "));
        } else return null;
    }

    private String getFiltersScript(String name, ElementKey parameterKey, ElementKey parentKey) {
        if(parameterKey != null && parentKey != null) {
            return "FILTERS " + name + "(" + parameterKey.ID + ") == " + parentKey.ID;
        } else return null;
    }

    private class ElementKey {
        String extID;
        String ID;

        ElementKey(String extID) {
            this(extID, generateObjectId(extID));
        }

        ElementKey(String extID, String ID) {
            this.extID = extID;
            this.ID = ID;
        }

        public String getScript() {
            return ID + (ID.equals(extID) ? "" : (" EXTID '" + extID + "'"));
        }

        @Override
        public String toString() {
            return "ElementKey{" + "extID='" + extID + '\'' + ", ID='" + ID + '\'' + '}';
        }
    }

    private class CodeBlock {
        Set<String> groupDeclarationScripts;
        Set<String> propertyDeclarationScripts;
        Set<String> objectsScripts;
        String propertiesScript;
        String filtersScript;

        List<CodeBlock> children;

        CodeBlock(Set<String> groupDeclarationScripts, Set<String> propertyDeclarationScripts, Set<String> objectsScripts, String propertiesScript, String filtersScript, List<CodeBlock> children) {
            this.groupDeclarationScripts = groupDeclarationScripts;
            this.propertyDeclarationScripts = propertyDeclarationScripts;
            this.objectsScripts = objectsScripts;
            this.propertiesScript = propertiesScript;
            this.filtersScript = filtersScript;
            this.children = children;
        }
    }

    private class Form {
        Set<String> groupDeclarationScripts;
        Set<String> propertyDeclarationScripts;
        List<String> formScripts;

        Form(Set<String> groupDeclarationScripts, Set<String> propertyDeclarationScripts, List<String> formScripts) {
            this.groupDeclarationScripts = groupDeclarationScripts;
            this.propertyDeclarationScripts = propertyDeclarationScripts;
            this.formScripts = formScripts;
        }
    }
}