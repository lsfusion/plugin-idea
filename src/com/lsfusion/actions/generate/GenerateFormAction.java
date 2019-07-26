package com.lsfusion.actions.generate;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.lsfusion.util.BaseUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("ComponentNotRegistered")
public class GenerateFormAction extends AnAction {

    private Set<String> usedObjectIds = new HashSet<>();
    private Set<String> usedGroupIds = new HashSet<>();

    @Override
    public void actionPerformed(final AnActionEvent e) {
        try {
            Object rootElement = getRootElement(e);
            if (rootElement != null) {
                usedObjectIds = new HashSet<>();
                usedGroupIds = new HashSet<>();
                ParseNode hierarchy = generateHierarchy(rootElement, null);
                CodeBlock formCodeBlock = generateForm(hierarchy, null, null, null, null, null);
                showFormScript(formCodeBlock);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), ex.getMessage(), "Generation failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    //need to implement
    protected Object getRootElement(AnActionEvent e) throws Exception {
        throw new NotImplementedException();
    }

    //need to implement
    protected ParseNode generateHierarchy(Object element, String key) {
        throw new NotImplementedException();
    }

    private void showFormScript(CodeBlock formCodeBlock) {
        Form form = generateFormFromCodeBlocks(formCodeBlock);

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
                for(String formScript : blocks.formScripts) {
                    if(!formScripts.contains(formScript)) { //temp check
                        formScripts.add(formScript);
                    }
                }
            }
        }
        return new Form(groupDeclarationScripts, propertyDeclarationScripts, formScripts);

    }

    private CodeBlock generateForm(ParseNode element, ElementKey key, ParseNode parentElement, ElementKey parentKey, ElementKey parentInGroupKey, ElementKey lastGroupObjectParent) {
        Set<String> groupDeclarationScripts = new LinkedHashSet<>();
        Set<String> propertyDeclarationScripts = new LinkedHashSet<>();
        Set<String> objectsScripts = new LinkedHashSet<>();
        Set<ElementKey> properties = new LinkedHashSet<>();
        String propertiesScript = null;
        String filtersScript = null;
        List<CodeBlock> children = new ArrayList<>();

        if(element instanceof GroupObjectParseNode) {

            String filterProperty;
            if(((GroupObjectParseNode) element).isIndex()) {
                filterProperty = getFilterProperty(parentKey, key);
                propertyDeclarationScripts.add(getStringPropertyDeclarationScript(key, "INTEGER"));
                objectsScripts.add(getObjectsScript(key, parentInGroupKey));
                propertiesScript = getPropertiesScript(key, null, new LinkedHashSet<>(Collections.singletonList(new ElementKey("value", key.ID))));
            } else {
                filterProperty = getFilterProperty(lastGroupObjectParent, key);
                objectsScripts.add(getObjectsScript(key, parentKey));

                for (ParseNode childElement : element.children) {
                    children.add(generateForm(childElement, key, element, parentKey, parentElement instanceof PropertyGroupParseNode ? key : null, key));
                }
            }

            if(filterProperty != null) {
                filtersScript = getFiltersScript(filterProperty, key, lastGroupObjectParent);
                propertyDeclarationScripts.add(getIntegerPropertyDeclarationScript(filterProperty));
            }

        } else if(element instanceof PropertyGroupParseNode) {

            if(hasPropertyGroupParseNodeChildren(element)) {
                if(lastGroupObjectParent != null) {
                    objectsScripts.add(getObjectsScript(key, parentKey));
                    if (parentKey != null) {
                        groupDeclarationScripts.add(getGroupDeclarationScript(parentKey, null));
                    }
                }
            }

            if(parentElement instanceof PropertyGroupParseNode) {
                groupDeclarationScripts.add(getGroupDeclarationScript(key, parentInGroupKey));
            }

            for(ParseNode childElement : element.children) {
                ElementKey childElementKey = new ElementKey(childElement.key);

                if(childElement instanceof PropertyParseNode) {
                    if(((PropertyParseNode) childElement).isAttr())
                        childElementKey.attr = true;
                    properties.add(childElementKey);
                }

                children.add(generateForm(childElement, childElementKey, element, key, parentElement instanceof PropertyGroupParseNode ? key : null, lastGroupObjectParent));

            }

        } else {
            propertyDeclarationScripts.add(getStringPropertyDeclarationScript(key, lastGroupObjectParent == null ? null : "INTEGER"));
        }

        propertiesScript = propertiesScript != null ? propertiesScript : getPropertiesScript(lastGroupObjectParent, parentElement instanceof PropertyGroupParseNode ? key : null, properties);
        return new CodeBlock(groupDeclarationScripts, propertyDeclarationScripts, objectsScripts, propertiesScript, filtersScript, children);
    }

    private boolean hasPropertyGroupParseNodeChildren(ParseNode element) {
        for(ParseNode childElement : element.children) {
            if(childElement instanceof PropertyGroupParseNode) {
                return true;
            }
        }
        return false;
    }

    ParseNode deepMerge(List<ParseNode> children) {
        ParseNode mergedObject = null;
        for (ParseNode obj : children) {
            mergedObject = mergedObject == null ? obj : deepMerge(mergedObject, obj);
        }
        return mergedObject;
    }

    private ParseNode deepMerge(ParseNode source, ParseNode target) throws JSONException {
        for (ParseNode targetChild : target.children) {
            ParseNode sourceChild = source.getChild(targetChild);
            if (sourceChild == null) {
                source.children.add(targetChild);
            } else {
                source.removeChild(targetChild);
                source.children.add(deepMerge(sourceChild, targetChild));
            }
        }
        return source;
    }

    private String getFilterProperty(ElementKey key, ElementKey childKey) {
        return key != null ? (key.ID + BaseUtils.capitalize(childKey.ID)) : null;
    }

    private String getGroupDeclarationScript(ElementKey groupKey, ElementKey parentGroupKey) {
        if(groupKey != null && !usedGroupIds.contains(groupKey.ID)) {
            usedGroupIds.add(groupKey.ID);
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
        if(property != null) {
            return property + " = DATA LOCAL " + type + "(" + (parameter != null ? parameter : "") + ");";
        } else return null;
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
        if(name != null && parameterKey != null && parentKey != null) {
            return "FILTERS " + name + "(" + parameterKey.ID + ") == " + parentKey.ID;
        } else return null;
    }

    private class ElementKey {
        String extID;
        String ID;
        boolean attr;

        ElementKey(String extID) {
            this(extID, generateObjectId(extID));
        }

        ElementKey(String extID, String ID) {
            this.extID = extID;
            this.ID = ID;
        }

        public String getScript() {
            return ID + (ID.equals(extID) ? "" : (" EXTID '" + extID + "'")) + (attr ? " ATTR" : "");
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

    class ParseNode {
        String key;
        List<ParseNode> children;
        ParseNode(String key, List<ParseNode> children) {
            this.key = key;
            this.children = children;
        }

        ParseNode getChild(ParseNode child) {
            for(ParseNode c : children) {
                if(c.key != null && c.key.equals(child.key))
                    return c;
            }
            return null;
        }

        void removeChild(ParseNode child) {
            children.removeIf(c -> c.key != null && c.key.equals(child.key));
        }
    }

    class GroupObjectParseNode extends ParseNode {
        private boolean integrationKey; // key (key in JSON, tag in XML, fields in plain formats) or index (array in JSON, multiple object name tags in xml, order in plain formats)

        GroupObjectParseNode(String key, List<ParseNode> children, boolean integrationKey) {
            super(key, children);
            this.integrationKey = integrationKey;
        }

        public boolean isIndex() {
            return !integrationKey;
        }
    }

    class PropertyGroupParseNode extends ParseNode {
        PropertyGroupParseNode(String key, List<ParseNode> children) {
            super(key, children);
        }
    }

    class PropertyParseNode extends ParseNode {
        private boolean attr;
        PropertyParseNode(String key, boolean attr) {
            super(key, new ArrayList<>());
            this.attr = attr;
        }

        public boolean isAttr() {
            return attr;
        }
    }
}