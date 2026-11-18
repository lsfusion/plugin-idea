package com.lsfusion.actions.generate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.JDOMUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.JBScrollPane;
import com.lsfusion.lang.LSFLexerAdapter;
import com.lsfusion.lang.psi.LSFTypes;
import com.lsfusion.util.BaseUtils;
import net.gcardone.junidecode.Junidecode;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.util.List;
import java.util.*;

public class GenerateFormAction extends AnAction {

    @Override
    public void actionPerformed(final @NotNull AnActionEvent e) {
        new GenerateFormDialog(getEventProject(e)).show();
    }

    // XML namespaces are the only part of the generated script that depends on the platform version
    enum Platform {
        // the form's FORMEXTID declares every namespace of the document ('ns=uri:root;p=uri'), an EXTID names only a prefix
        V7("7.0"),
        // FORMEXTID takes one namespace only (the ';' list is 7.0), and an import resolves a prefix against the document,
        // where it may be declared deeper than the element read: an EXTID outside the root's namespace carries its uri
        V6("6.x");

        final String caption;

        Platform(String caption) {
            this.caption = caption;
        }

        @Override
        public String toString() {
            return caption;
        }
    }

    private static final String PLATFORM_KEY = "lsfusion.generateForm.platform";

    private static class GenerateFormDialog extends DialogWrapper {
        private final Project project;
        private JTextPane sourceTextPane;
        private JTextPane targetTextPane;
        private ComboBox<Platform> platformComboBox;

        GenerateFormDialog(Project project) {
            super(project);
            this.project = project;
            setTitle("Generate Form");
            setOKButtonText("Close");
            init();
        }

        @Override
        protected Action @NotNull [] createActions() {
            return new Action[]{getOKAction()};
        }

        private void generate(String text) {
            try {
                Platform platform = (Platform) platformComboBox.getSelectedItem();
                PropertiesComponent.getInstance().setValue(PLATFORM_KEY, platform.name());
                targetTextPane.setText(new ScriptGenerator(platform).generate(parse(text)));
            } catch (Exception e) {
                Messages.showErrorDialog(project, String.valueOf(e.getMessage()), "Generation Failed");
            }
        }

        private void generateFromFile() {
            VirtualFile file = FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFileDescriptor()
                    .withExtensionFilter("JSON / XML", "json", "xml"), project, null);
            if (file != null) {
                String text;
                try {
                    // reading a file is not allowed on the EDT
                    text = ProgressManager.getInstance().runProcessWithProgressSynchronously(
                            () -> VfsUtilCore.loadText(file), "Reading " + file.getName(), false, project);
                } catch (Exception e) {
                    Messages.showErrorDialog(project, String.valueOf(e.getMessage()), "Read File Failed");
                    return;
                }
                generate(text);
            }
        }

        @Override
        protected @Nullable JComponent createCenterPanel() {
            sourceTextPane = new JTextPane();
            sourceTextPane.setBackground(null);

            JButton generateFromTextButton = new JButton("Generate from text");
            generateFromTextButton.addActionListener(e -> generate(sourceTextPane.getText()));
            generateFromTextButton.setEnabled(false);
            sourceTextPane.getDocument().addDocumentListener(new DocumentAdapter() {
                @Override
                protected void textChanged(@NotNull DocumentEvent e) {
                    generateFromTextButton.setEnabled(!sourceTextPane.getText().isEmpty());
                }
            });

            JButton generateFromFileButton = new JButton("Generate from file");
            generateFromFileButton.addActionListener(e -> generateFromFile());

            platformComboBox = new ComboBox<>(Platform.values());
            platformComboBox.setSelectedItem(Platform.valueOf(PropertiesComponent.getInstance().getValue(PLATFORM_KEY, Platform.V7.name())));
            platformComboBox.setToolTipText("XML only: 6.x does not accept the FORMEXTID 'ns=uri:root;p=uri' list, so each EXTID names its namespace's uri");
            JLabel platformLabel = new JLabel("Platform:");

            targetTextPane = new JTextPane();
            targetTextPane.setBackground(null);
            targetTextPane.setEditable(false);

            JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            buttonsPanel.add(generateFromTextButton);
            buttonsPanel.add(generateFromFileButton);
            buttonsPanel.add(platformLabel);
            buttonsPanel.add(platformComboBox);

            JPanel bottomPanel = new JPanel(new BorderLayout());
            bottomPanel.add(buttonsPanel, BorderLayout.NORTH);
            bottomPanel.add(new JBScrollPane(targetTextPane), BorderLayout.CENTER);

            JPanel mainPanel = new JPanel(new GridLayout(2, 1));
            mainPanel.setPreferredSize(new Dimension(700, 800));
            mainPanel.add(new JBScrollPane(sourceTextPane));
            mainPanel.add(bottomPanel);
            return mainPanel;
        }
    }

    // parse tree

    record Namespace(String prefix, String uri) {
    }

    abstract static class Node {
        final String name; // a JSON key or an XML local name
        final Namespace namespace; // XML only

        Node(String name, Namespace namespace) {
            this.name = name;
            this.namespace = namespace;
        }
    }

    // a value: a JSON primitive, an XML attribute or the text of an XML element
    static class PropertyNode extends Node {
        final String type; // null when no value says what it is (JSON null)
        final boolean attr;
        final boolean text; // the text of an XML element that has attributes too, read by the EXTID 'value'

        PropertyNode(String name, Namespace namespace, String type, boolean attr, boolean text) {
            super(name, namespace);
            this.type = type;
            this.attr = attr;
            this.text = text;
        }
    }

    // a JSON object, an XML element with children or attributes
    static class GroupNode extends Node {
        final List<Node> children = new ArrayList<>();
        final List<Namespace> namespaces = new ArrayList<>(); // root only: every namespace the document declares or uses

        GroupNode(String name, Namespace namespace) {
            super(name, namespace);
        }
    }

    // a JSON array, a repeated XML element
    static class ObjectNode extends Node {
        final GroupNode element; // the rows merged; null when they are values
        final String valueType;

        ObjectNode(String name, Namespace namespace, GroupNode element, String valueType) {
            super(name, namespace);
            this.element = element;
            this.valueType = valueType;
        }

        boolean isValues() {
            return element == null || element.children.isEmpty();
        }
    }

    static String mergeType(String a, String b) {
        return a == null ? b : b == null || a.equals(b) ? a : "STRING";
    }

    // merges the rows of an array: a key may be missing in some of them, hold an object in one and a value in another
    static Node merge(Node a, Node b) {
        if (a == null)
            return b;
        if (a instanceof PropertyNode pa && b instanceof PropertyNode pb)
            return new PropertyNode(pa.name, pa.namespace, mergeType(pa.type, pb.type), pa.attr, pa.text);
        if (a instanceof GroupNode ga && b instanceof GroupNode gb) {
            GroupNode result = new GroupNode(ga.name, ga.namespace);
            result.children.addAll(ga.children);
            for (Node child : gb.children) {
                int index = indexOf(result.children, child);
                if (index < 0)
                    result.children.add(child);
                else
                    result.children.set(index, merge(result.children.get(index), child));
            }
            return result;
        }
        if (a instanceof ObjectNode oa && b instanceof ObjectNode ob)
            return new ObjectNode(oa.name, oa.namespace, (GroupNode) merge(oa.element, ob.element), mergeType(oa.valueType, ob.valueType));
        // a value loses to a structure, a single object to an array
        if (a instanceof ObjectNode || b instanceof PropertyNode)
            return a;
        return b;
    }

    private static int indexOf(List<Node> nodes, Node node) {
        for (int i = 0; i < nodes.size(); i++) {
            Node n = nodes.get(i);
            if (n.name.equals(node.name) && Objects.equals(n.namespace, node.namespace) &&
                    (n instanceof PropertyNode pn && pn.attr) == (node instanceof PropertyNode p && p.attr))
                return i;
        }
        return -1;
    }

    // the root is a group: the JSON top-level object, or the XML root element
    static GroupNode parse(String text) throws Exception {
        if (text.startsWith("\uFEFF"))
            text = text.substring(1);
        return text.stripLeading().startsWith("<") ? parseXML(text) : parseJSON(text);
    }

    private static GroupNode parseJSON(String text) {
        // Gson keeps the order of the keys, so the form lists them as the document does
        JsonElement root = JsonParser.parseString(text);
        if (!root.isJsonObject()) { // the platform reads a top-level array or value as the key "value"
            JsonObject wrapper = new JsonObject();
            wrapper.add("value", root);
            root = wrapper;
        }
        return (GroupNode) parseJSON(root, null);
    }

    private static Node parseJSON(JsonElement element, String key) {
        if (element.isJsonObject()) {
            GroupNode group = new GroupNode(key, null);
            for (Map.Entry<String, JsonElement> entry : ((JsonObject) element).entrySet())
                group.children.add(parseJSON(entry.getValue(), entry.getKey()));
            return group;
        }
        if (element.isJsonArray()) {
            GroupNode rows = null;
            String valueType = null;
            for (JsonElement row : (JsonArray) element) {
                if (row.isJsonObject())
                    rows = (GroupNode) merge(rows, parseJSON(row, key));
                else
                    valueType = mergeType(valueType, row.isJsonArray() ? "STRING" : getJSONValueType(row));
            }
            return new ObjectNode(key, null, rows, valueType);
        }
        return new PropertyNode(key, null, getJSONValueType(element), false, false);
    }

    private static String getJSONValueType(JsonElement value) {
        if (value.isJsonNull())
            return null;
        JsonPrimitive primitive = (JsonPrimitive) value;
        if (primitive.isNumber())
            return "NUMERIC";
        if (primitive.isBoolean())
            return "BOOLEAN";
        return getPropertyType(primitive.getAsString());
    }

    private static GroupNode parseXML(String text) throws Exception {
        Element rootElement = JDOMUtil.load(text);
        Node root = parseXML(rootElement);
        GroupNode rootGroup;
        if (root instanceof GroupNode group)
            rootGroup = group;
        else { // <a>text</a>: the root's text is read by the EXTID 'value'
            rootGroup = new GroupNode(root.name, root.namespace);
            rootGroup.children.add(new PropertyNode(root.name, null, ((PropertyNode) root).type, false, true));
        }
        Set<Namespace> namespaces = new LinkedHashSet<>();
        collectXMLNamespaces(rootElement, namespaces);
        rootGroup.namespaces.addAll(namespaces);
        return rootGroup;
    }

    private static Node parseXML(Element element) {
        Namespace namespace = getXMLNamespace(element.getNamespacePrefix(), element.getNamespaceURI());
        List<Element> children = element.getChildren();
        List<Attribute> attributes = element.getAttributes();
        String text = element.getTextTrim();
        if (children.isEmpty() && attributes.isEmpty())
            return new PropertyNode(element.getName(), namespace, getPropertyType(text), false, false);

        GroupNode group = new GroupNode(element.getName(), namespace);
        for (Attribute attribute : attributes)
            group.children.add(new PropertyNode(attribute.getName(), getXMLNamespace(attribute.getNamespacePrefix(), attribute.getNamespaceURI()), getPropertyType(attribute.getValue()), true, false));
        if (children.isEmpty() && !text.isEmpty())
            group.children.add(new PropertyNode(element.getName(), null, getPropertyType(text), false, true));

        // an element met more than once among its siblings is a row of an object, otherwise a group or a value
        Map<String, List<Element>> sameNameChildren = new LinkedHashMap<>();
        for (Element child : children)
            sameNameChildren.computeIfAbsent(child.getNamespaceURI() + ":" + child.getName(), k -> new ArrayList<>()).add(child);
        for (List<Element> rows : sameNameChildren.values()) {
            Element first = rows.get(0);
            if (rows.size() == 1) {
                group.children.add(parseXML(first));
            } else {
                GroupNode merged = null;
                String valueType = null;
                for (Element row : rows) {
                    Node rowNode = parseXML(row);
                    if (rowNode instanceof GroupNode rowGroup)
                        merged = (GroupNode) merge(merged, rowGroup);
                    else if (!row.getTextTrim().isEmpty()) // an empty row says nothing about the text of the others
                        valueType = mergeType(valueType, ((PropertyNode) rowNode).type);
                }
                Namespace rowNamespace = getXMLNamespace(first.getNamespacePrefix(), first.getNamespaceURI());
                if (merged != null && valueType != null) { // some rows have attributes, others only text
                    GroupNode textRow = new GroupNode(first.getName(), rowNamespace);
                    textRow.children.add(new PropertyNode(first.getName(), null, valueType, false, true));
                    merged = (GroupNode) merge(merged, textRow);
                }
                group.children.add(new ObjectNode(first.getName(), rowNamespace, merged, valueType));
            }
        }
        return group;
    }

    private static Namespace getXMLNamespace(String prefix, String uri) {
        return uri.isEmpty() ? null : new Namespace(prefix, uri);
    }

    private static void collectXMLNamespaces(Element element, Set<Namespace> namespaces) {
        Namespace namespace = getXMLNamespace(element.getNamespacePrefix(), element.getNamespaceURI());
        if (namespace != null)
            namespaces.add(namespace);
        for (org.jdom.Namespace declared : element.getAdditionalNamespaces()) {
            Namespace additional = getXMLNamespace(declared.getPrefix(), declared.getURI());
            if (additional != null)
                namespaces.add(additional);
        }
        for (Attribute attribute : element.getAttributes()) {
            Namespace attributeNamespace = getXMLNamespace(attribute.getNamespacePrefix(), attribute.getNamespaceURI());
            if (attributeNamespace != null)
                namespaces.add(attributeNamespace);
        }
        for (Element child : element.getChildren())
            collectXMLNamespaces(child, namespaces);
    }

    private static String getPropertyType(String value) {
        try {
            String dateType = DateConverter.smartParse(value);
            if (dateType != null)
                return dateType;
        } catch (java.time.DateTimeException ignored) {
        }
        return "STRING";
    }

    // one generation: the names given are unique within it
    static class ScriptGenerator {
        private final Platform platform;

        private final Set<String> usedIds = new HashSet<>();
        private final List<String> groupScripts = new ArrayList<>();
        private final List<String> propertyScripts = new ArrayList<>();
        private final List<String> formScripts = new ArrayList<>();

        private Namespace rootNamespace;
        private final Map<String, String> declaredNamespaces = new LinkedHashMap<>(); // prefix -> uri, declared once for the whole form

        ScriptGenerator(Platform platform) {
            this.platform = platform;
        }

        String generate(GroupNode root) {
            String formScript;
            if (root.name == null) { // JSON
                formScript = "FORM generated";
            } else {
                rootNamespace = root.namespace;
                if (platform == Platform.V7) {
                    Map<String, Set<String>> uris = new LinkedHashMap<>();
                    for (Namespace namespace : root.namespaces)
                        uris.computeIfAbsent(namespace.prefix(), k -> new LinkedHashSet<>()).add(namespace.uri());
                    uris.forEach((prefix, prefixUris) -> {
                        // a prefix bound to different uris, and the default namespace of an element below the root, are named where they are used
                        if (!prefix.isEmpty() && prefixUris.size() == 1 && (rootNamespace == null || !prefix.equals(rootNamespace.prefix())))
                            declaredNamespaces.put(prefix, prefixUris.iterator().next());
                    });
                }
                // the root declares its namespace, the elements below it name only its prefix
                StringBuilder formExtId = new StringBuilder(rootNamespace == null ? root.name :
                        rootNamespace.prefix() + "=" + rootNamespace.uri() + ":" + root.name);
                declaredNamespaces.forEach((prefix, uri) -> formExtId.append(';').append(prefix).append('=').append(uri));
                // without FORMEXTID 7.0 exports the root as <export>
                formScript = "FORM " + generateId(root.name, Collections.emptySet()) + " FORMEXTID " + literal(formExtId.toString());
            }

            generateGroup(root, null, null);

            StringBuilder script = new StringBuilder();
            if (!groupScripts.isEmpty())
                script.append(String.join("\n", groupScripts)).append("\n\n");
            if (!propertyScripts.isEmpty())
                script.append(String.join("\n", propertyScripts)).append("\n\n");
            script.append(formScript);
            for (String formPart : formScripts)
                script.append("\n").append(formPart);
            return script.append(";").toString();
        }

        // the form follows the document's order, which EXPORT keeps (an XML schema may require it)
        private void generateGroup(GroupNode group, String groupId, String objectId) {
            List<String> properties = new ArrayList<>();
            for (Node child : group.children) {
                if (child instanceof PropertyNode property) {
                    properties.add(generateProperty(property, objectId));
                    continue;
                }
                addProperties(properties, groupId, objectId);
                if (child instanceof GroupNode childGroup) {
                    if (hasContent(childGroup)) {
                        String childGroupId = generateId(childGroup.name);
                        groupScripts.add("GROUP " + childGroupId + getExtIDScript(childGroupId, childGroup.name, childGroup.namespace) + (groupId != null ? " : " + groupId : "") + ";");
                        generateGroup(childGroup, childGroupId, objectId);
                    }
                } else
                    generateObject((ObjectNode) child, groupId, objectId);
            }
            addProperties(properties, groupId, objectId);
        }

        private void addProperties(List<String> properties, String groupId, String objectId) {
            if (!properties.isEmpty()) {
                formScripts.add("PROPERTIES(" + (objectId != null ? objectId : "") + ") " + (groupId != null ? "IN " + groupId + " " : "") + String.join(", ", properties));
                properties.clear();
            }
        }

        private static boolean hasContent(GroupNode group) {
            for (Node child : group.children)
                if (!(child instanceof GroupNode childGroup) || hasContent(childGroup))
                    return true;
            return false;
        }

        private String generateProperty(PropertyNode property, String objectId) {
            String id = generateId(property.name);
            propertyScripts.add(id + " = DATA LOCAL " + (property.type != null ? property.type : "STRING") + "(" + (objectId != null ? "INTEGER" : "") + ");");
            return id + (property.text ? " EXTID 'value'" : getExtIDScript(id, property.name, property.namespace)) + (property.attr ? " ATTR" : "");
        }

        private void generateObject(ObjectNode object, String groupId, String parentObjectId) {
            String id = generateId(object.name);
            formScripts.add("\nOBJECTS " + id + " = INTEGER" + getExtIDScript(id, object.name, object.namespace) + (groupId != null ? " IN " + groupId : ""));

            // IMPORT sets the property of a filter for every row it adds, so each object needs one of its own:
            // the row numbers of different objects overlap, and EXPORT would find the rows of one object in another
            if (parentObjectId != null) {
                String parentProperty = generateId(parentObjectId + BaseUtils.capitalize(id));
                propertyScripts.add(parentProperty + " = DATA LOCAL INTEGER(INTEGER);");
                formScripts.add("FILTERS " + parentProperty + "(" + id + ") == " + parentObjectId);
            } else {
                String importedProperty = generateId("imported" + BaseUtils.capitalize(id));
                propertyScripts.add(importedProperty + " = DATA LOCAL BOOLEAN(INTEGER);");
                formScripts.add("FILTERS " + importedProperty + "(" + id + ")");
            }

            if (object.isValues()) {
                propertyScripts.add(id + " = DATA LOCAL " + (object.valueType != null ? object.valueType : "STRING") + "(INTEGER);");
                formScripts.add("PROPERTIES(" + id + ") " + id + " EXTID 'value'");
            } else
                generateGroup(object.element, null, id);
        }

        private String getExtIDScript(String id, String name, Namespace namespace) {
            String extID = getExtID(name, namespace);
            return extID.equals(id) ? "" : " EXTID " + literal(extID);
        }

        private String getExtID(String name, Namespace namespace) {
            if (namespace == null)
                return name;
            if (namespace.equals(rootNamespace) || namespace.uri().equals(declaredNamespaces.get(namespace.prefix())))
                return namespace.prefix().isEmpty() ? name : namespace.prefix() + ":" + name;
            return namespace.prefix() + "=" + namespace.uri() + ":" + name;
        }

        // properties, groups and objects share one set of names, the form has a namespace of its own
        private String generateId(String name) {
            String id = generateId(name, usedIds);
            usedIds.add(id);
            return id;
        }

        private static String generateId(String name, Set<String> usedIds) {
            String id = Junidecode.unidecode(name).replaceAll("[^a-zA-Z0-9_]", "_");
            id = id.equals(id.toUpperCase()) ? id.toLowerCase() : Character.toLowerCase(id.charAt(0)) + id.substring(1);
            if (!id.matches("[a-zA-Z].*"))
                id = "v" + id;
            String result = id;
            for (int i = 1; usedIds.contains(result) || !isId(result); i++)
                result = id + i;
            return result;
        }

        // keywords, type names and literals (TRUE, INTEGER, ...) are not ids
        private static boolean isId(String id) {
            LSFLexerAdapter lexer = new LSFLexerAdapter();
            lexer.start(id);
            return lexer.getTokenType() == LSFTypes.ID && lexer.getTokenEnd() == id.length();
        }

        private static String literal(String value) {
            return "'" + value.replace("\\", "\\\\").replace("'", "\\'") + "'";
        }
    }
}
