package com.lsfusion.actions.generate;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBScrollPane;
import com.lsfusion.util.BaseUtils;
import net.gcardone.junidecode.Junidecode;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.beans.Introspector;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public abstract class GenerateFormAction extends AnAction {

    private Set<String> usedObjectIds = new HashSet<>();
    private Set<String> usedObjectsIds = new HashSet<>();
    private Set<String> usedGroupIds = new HashSet<>();
    private Set<String> usedNamespacePrefixes = new HashSet<>();


    abstract String getExtension();

    abstract Object getRootElement(String file) throws Exception;

    abstract List<ParseNode> generateHierarchy(Object element, String key);

    @Override
    public void actionPerformed(final AnActionEvent e) {
        new GenerateFormDialog(e).setVisible(true);
    }

    public class GenerateFormDialog extends JDialog {
        JTextPane sourceTextPane;
        JTextPane targetTextPane;
        JButton generateFromTextButton;

        public GenerateFormDialog(AnActionEvent actionEvent) {
            super(null, "Generate form", ModalityType.APPLICATION_MODAL);
            setMinimumSize(new Dimension(600, 800));

            setLocationRelativeTo(null);

            sourceTextPane = new JTextPane();
            sourceTextPane.setBackground(null);
            JBScrollPane sourceScrollPane = new JBScrollPane(sourceTextPane);

            generateFromTextButton = new JButton("Generate from text");
            generateFromTextButton.addActionListener(e -> generate(sourceTextPane.getText()));
            generateFromTextButton.setEnabled(false);

            JButton generateFromFileButton = new JButton("Generate from file");
            generateFromFileButton.addActionListener(e -> onGenerateFromFile(actionEvent));

            targetTextPane = new JTextPane();
            targetTextPane.setBackground(null);
            targetTextPane.setEditable(false);
            JBScrollPane targetScrollPane = new JBScrollPane(targetTextPane);

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            mainPanel.add(sourceScrollPane, getGridBagConstraints(0));
            JPanel buttonsPanel = new JPanel();
            buttonsPanel.add(generateFromTextButton, BorderLayout.EAST);
            buttonsPanel.add(generateFromFileButton, BorderLayout.EAST);
            buttonsPanel.setMinimumSize(new Dimension());

            JPanel bottomPanel = new JPanel();
            bottomPanel.setLayout(new BorderLayout());
            bottomPanel.add(buttonsPanel, BorderLayout.NORTH);
            bottomPanel.add(targetScrollPane, BorderLayout.CENTER);

            mainPanel.add(bottomPanel, getGridBagConstraints(1));

            sourceTextPane.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    enableGenerateButton();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    enableGenerateButton();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    enableGenerateButton();
                }
            });

            add(mainPanel, BorderLayout.CENTER);
        }

        private GridBagConstraints getGridBagConstraints(int row) {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridy = row;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 0.1;
            gbc.weighty = 1.0;
            return gbc;
        }

        private void generate(String text) {
            try {
                if(text != null && !text.isEmpty()) {
                    Object rootElement = getRootElement(text);
                    if (rootElement != null) {
                        usedObjectIds = new HashSet<>();
                        usedObjectsIds = new HashSet<>();
                        usedGroupIds = new HashSet<>();
                        usedNamespacePrefixes = new HashSet<>();
                        List<ParseNode> hierarchy = generateHierarchy(rootElement, null);
                        CodeBlock formCodeBlock = generateForm(hierarchy, null, null, null, null, null);
                        showFormScript(formCodeBlock);
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), e.getMessage(), "Generation failed", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void enableGenerateButton() {
            generateFromTextButton.setEnabled(!sourceTextPane.getText().isEmpty());
        }

        private void onGenerateFromFile(AnActionEvent actionEvent) {
            try {
                final FileChooserDescriptor fileChooser = FileChooserDescriptorFactory.createSingleFileDescriptor(getExtension());
                VirtualFile file = FileChooser.chooseFile(fileChooser, actionEvent.getProject(), null);
                String inputFile = file != null ? Files.readString(Paths.get(file.getPath())) : null;
                generate(inputFile);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), e.getMessage(), "Read file failed", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void showFormScript(CodeBlock formCodeBlock) {
            Form form = generateFormFromCodeBlocks(formCodeBlock);
            String formId = form.formName == null ? "generated" : Introspector.decapitalize(form.formName);
            String formExtId = form.formName == null || formId.equals(form.formName) ? "" : (" FORMEXTID '" + form.formName + "'");

            String formScript = (form.groupDeclarationScripts.isEmpty() ? "" : (StringUtils.join(form.groupDeclarationScripts, "\n") + "\n\n")) +
                    (form.propertyDeclarationScripts.isEmpty() ? "" : (StringUtils.join(form.propertyDeclarationScripts, "\n") + "\n\n")) +
                    "FORM " + formId + formExtId + "\n" + (form.formScripts.isEmpty() ? "" : StringUtils.join(form.formScripts, "\n")) + ";";

            targetTextPane.setText(formScript);
        }
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
        return new Form(codeBlock.formName, groupDeclarationScripts, propertyDeclarationScripts, formScripts);

    }

    private CodeBlock generateForm(List<ParseNode> elements, ElementKey key, ParseNode parentElement, ElementKey parentKey, ElementKey parentInGroupKey, ElementKey lastGroupObjectParent) {
        String formName = null;
        Set<String> groupDeclarationScripts = new LinkedHashSet<>();
        Set<String> propertyDeclarationScripts = new LinkedHashSet<>();
        Set<String> objectsScripts = new LinkedHashSet<>();
        Set<ElementProperty> properties = new LinkedHashSet<>();
        String propertiesScript = null;
        String filtersScript = null;
        List<CodeBlock> children = new ArrayList<>();

        for(ParseNode element : elements) {

            if (element instanceof GroupObjectParseNode) {

                String filterProperty;
                if (((GroupObjectParseNode) element).isIndex()) {
                    filterProperty = getFilterProperty(parentKey, key);
                    propertyDeclarationScripts.add(getStringPropertyDeclarationScript(key, "INTEGER"));
                    objectsScripts.add(getObjectsScript(key, ((GroupObjectParseNode) element).namespace, parentInGroupKey));
                    propertiesScript = getPropertiesScript(key, null, new LinkedHashSet<>(Collections.singletonList(new ElementProperty(new ElementKey("value", key.ID), ((GroupObjectParseNode) element).namespace, false))));
                } else {
                    filterProperty = getFilterProperty(lastGroupObjectParent, key);
                    objectsScripts.add(getObjectsScript(key, ((GroupObjectParseNode) element).namespace, parentInGroupKey));

                    for (ParseNode childElement : element.children) {
                        children.add(generateForm(Collections.singletonList(childElement), key, element, parentKey, parentElement instanceof PropertyGroupParseNode ? key : null, key));
                    }
                }

                if (filterProperty != null) {
                    filtersScript = getFiltersScript(filterProperty, key, lastGroupObjectParent);
                    propertyDeclarationScripts.add(getIntegerPropertyDeclarationScript(filterProperty));
                }

            } else if (element instanceof PropertyGroupParseNode) {
                formName = ((PropertyGroupParseNode) element).formName;

                if (hasPropertyGroupParseNodeChildren(element)) {
                    if (lastGroupObjectParent != null) {
                        objectsScripts.add(getObjectsScript(key, ((PropertyGroupParseNode) element).namespace, parentInGroupKey));
                        if (parentKey != null) {
                            groupDeclarationScripts.add(getGroupDeclarationScript(parentKey, ((PropertyGroupParseNode) element).namespace, null));
                        }
                    }
                }

                if (parentElement instanceof PropertyGroupParseNode) {
                    groupDeclarationScripts.add(getGroupDeclarationScript(key, ((PropertyGroupParseNode) element).namespace, parentInGroupKey));
                }

                for (ParseNode childElement : element.children) {
                    ElementKey childElementKey = new ElementKey(childElement.key);

                    if (childElement instanceof PropertyParseNode) {
                        properties.add(new ElementProperty(childElementKey, ((PropertyParseNode) childElement).namespace, ((PropertyParseNode) childElement).attr));
                    }

                    children.add(generateForm(Arrays.asList(childElement), childElementKey, element, key, parentElement instanceof PropertyGroupParseNode ? key : null, lastGroupObjectParent));

                }

            } else {
                propertyDeclarationScripts.add(getStringPropertyDeclarationScript(key, lastGroupObjectParent == null ? null : "INTEGER"));
            }
        }

        propertiesScript = propertiesScript != null ? propertiesScript : getPropertiesScript(lastGroupObjectParent, parentElement instanceof PropertyGroupParseNode ? key : null, properties);
        return new CodeBlock(formName, groupDeclarationScripts, propertyDeclarationScripts, objectsScripts, propertiesScript, filtersScript, children);
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

    private String getGroupDeclarationScript(ElementKey groupKey, ElementNamespace groupNamespace, ElementKey parentGroupKey) {
        if(groupKey != null && !usedGroupIds.contains(groupKey.ID)) {
            usedGroupIds.add(groupKey.ID);
            String extID = getExtIDScript(groupKey, groupNamespace);
            String parent = parentGroupKey != null ? (" : " + parentGroupKey.ID) : "";
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
        id = Introspector.decapitalize(id);
        if(id != null) {
            if(id.matches("[^a-zA-Z].*")) {
                id = "v" + id;
            }
            id = Junidecode.unidecode(id).replaceAll("[^a-zA-Z0-9_]", "_");
        }
        int count = 0;
        while(usedObjectIds.contains(id + (count == 0 ? "" : count))) {
            count++;
        }
        id = id + (count == 0 ? "" : count);
        usedObjectIds.add(id);
        return id;
    }

    private String getObjectsScript(ElementKey elementKey, ElementNamespace namespace, ElementKey inGroupKey) {
        if(elementKey != null && !usedObjectsIds.contains(elementKey.ID)) {
            usedObjectsIds.add(elementKey.ID);
            String extID = getExtIDScript(elementKey, namespace);
            String inGroup = (inGroupKey == null || inGroupKey.ID == null ? "" : (" IN " + inGroupKey.ID));
            return "\nOBJECTS " + elementKey.ID + " = INTEGER" + extID + inGroup;
        } else return null;
    }

    private String getExtIDScript(ElementKey key, ElementNamespace namespace) {
        String namespaceScript = "";
        if (namespace != null) {
            if (!usedNamespacePrefixes.contains(namespace.prefix)) {
                usedNamespacePrefixes.add(namespace.prefix);
                namespaceScript = namespace.prefix + "=" + namespace.uri + ":";
            } else {
                namespaceScript = namespace.prefix + ":";
            }
        }
        return !key.ID.equals(key.extID) || namespace != null ? (" EXTID '" + namespaceScript + key.extID + "'") : "";
    }

    private String getPropertiesScript(ElementKey object, ElementKey inGroup, Set<ElementProperty> properties) {
        if(!properties.isEmpty()) {
            return "PROPERTIES" +
                    "(" + (object == null ? "" : object.ID) + ") " +
                    (inGroup == null ? "" : ("IN " + inGroup.ID + " ")) +
                    properties.stream().map(ElementProperty::getScript).collect(Collectors.joining(", ")) +
                    (object != null ? "\nFILTERS " + properties.iterator().next().key.ID + "(" + object.ID + ")" : "");
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

        ElementKey(String extID) {
            this(extID, generateObjectId(extID));
        }

        ElementKey(String extID, String ID) {
            this.extID = extID;
            this.ID = ID;
        }

        @Override
        public String toString() {
            return "ElementKey{" + "extID='" + extID + '\'' + ", ID='" + ID + '\'' + '}';
        }
    }

    private class CodeBlock {
        String formName;
        Set<String> groupDeclarationScripts;
        Set<String> propertyDeclarationScripts;
        Set<String> objectsScripts;
        String propertiesScript;
        String filtersScript;

        List<CodeBlock> children;

        CodeBlock(String formName, Set<String> groupDeclarationScripts, Set<String> propertyDeclarationScripts,
                  Set<String> objectsScripts, String propertiesScript, String filtersScript, List<CodeBlock> children) {
            this.formName = formName;
            this.groupDeclarationScripts = groupDeclarationScripts;
            this.propertyDeclarationScripts = propertyDeclarationScripts;
            this.objectsScripts = objectsScripts;
            this.propertiesScript = propertiesScript;
            this.filtersScript = filtersScript;
            this.children = children;
        }
    }

    private class Form {
        String formName;
        Set<String> groupDeclarationScripts;
        Set<String> propertyDeclarationScripts;
        List<String> formScripts;

        Form(String formName, Set<String> groupDeclarationScripts, Set<String> propertyDeclarationScripts, List<String> formScripts) {
            this.formName = formName;
            this.groupDeclarationScripts = groupDeclarationScripts;
            this.propertyDeclarationScripts = propertyDeclarationScripts;
            this.formScripts = formScripts;
        }
    }

    abstract class ParseNode {
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

    abstract class GroupParseNode extends ParseNode {
        ElementNamespace namespace;

        GroupParseNode(String key, List<ParseNode> children, ElementNamespace namespace) {
            super(key, children);
            this.namespace = namespace;
        }
    }

    class GroupObjectParseNode extends GroupParseNode {
        private boolean integrationKey; // key (key in JSON, tag in XML, fields in plain formats) or index (array in JSON, multiple object name tags in xml, order in plain formats)

        GroupObjectParseNode(String key, List<ParseNode> children, ElementNamespace namespace, boolean integrationKey) {
            super(key, children, namespace);
            this.integrationKey = integrationKey;
        }

        public boolean isIndex() {
            return !integrationKey;
        }
    }

    class PropertyGroupParseNode extends GroupParseNode {
        String formName;
        PropertyGroupParseNode(String key, List<ParseNode> children, ElementNamespace namespace, String formName) {
            super(key, children, namespace);
            this.formName = formName;
        }
    }

    class PropertyParseNode extends ParseNode {
        ElementNamespace namespace;
        boolean attr;
        PropertyParseNode(String key, ElementNamespace namespace, boolean attr) {
            super(key, new ArrayList<>());
            this.namespace = namespace;
            this.attr = attr;
        }
    }

    class ElementProperty {
        ElementKey key;
        ElementNamespace namespace;
        boolean attr;

        ElementProperty(ElementKey key, ElementNamespace namespace, boolean attr) {
            this.key = key;
            this.namespace = namespace;
            this.attr = attr;
        }

        public String getScript() {
            return key.ID + getExtIDScript(key, namespace) + (attr ? " ATTR" : "");
        }
    }

    class ElementNamespace {
        String prefix;
        String uri;

        ElementNamespace(String prefix, String uri) {
            this.prefix = prefix;
            this.uri = uri;
        }
    }

}