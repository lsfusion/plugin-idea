package com.lsfusion.dependencies;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.*;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.jcef.JBCefBrowser;
import com.lsfusion.LSFIcons;
import com.lsfusion.design.ui.FlexPanel;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.*;
import com.lsfusion.lang.psi.impl.LSFClassStatementImpl;
import groovyjarjarantlr4.v4.misc.OrderedHashMap;
import groovyjarjarantlr4.v4.runtime.misc.OrderedHashSet;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

public class UMLDiagramView {
    private final Project project;
    private final JBCefBrowser browser;
    private final JPanel mainPanel;

    JBCheckBox requiredCheckBox;

    public UMLDiagramView(Project project) {
        this.project = project;
        this.browser = new JBCefBrowser();

        mainPanel = new JPanel(new BorderLayout());

        FlexPanel toolboxPanel = new FlexPanel(false);

        JButton updateButton = new JButton(LSFIcons.Design.REFRESH);
        updateButton.addActionListener(e -> updateDiagram());
        toolboxPanel.add(updateButton);

        JButton zoomInButton = new JButton(LSFIcons.DEPENDENCY_ZOOM_IN);
        zoomInButton.addActionListener(e -> zoom(1));
        toolboxPanel.add(zoomInButton);

        JButton resetZoomButton = new JButton(LSFIcons.DEPENDENCY_ACTUAL_ZOOM);
        resetZoomButton.addActionListener(e -> zoom(0));
        toolboxPanel.add(resetZoomButton);

        JButton zoomOutButton = new JButton(LSFIcons.DEPENDENCY_ZOOM_OUT);
        zoomOutButton.addActionListener(e -> zoom(-1));
        toolboxPanel.add(zoomOutButton);

        requiredCheckBox = new JBCheckBox("Include Required");
        requiredCheckBox.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        toolboxPanel.add(requiredCheckBox);

        JButton exportButton = new JButton(LSFIcons.GRAPH_EXPORT);
        exportButton.addActionListener(e -> export());
        toolboxPanel.add(exportButton);

        mainPanel.add(toolboxPanel, BorderLayout.NORTH);
        mainPanel.add(browser.getComponent(), BorderLayout.CENTER);

        updateDiagram();
    }

    public void redraw() {
        updateDiagram();
    }

    public JPanel getComponent() {
        return mainPanel;
    }

    private void updateDiagram() {
        zoomLevel = 1;
        String html = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "  <meta charset=\"UTF-8\">\n" +
                "  <script type=\"module\">\n" +
                "    import mermaid from 'https://cdn.jsdelivr.net/npm/mermaid@10/dist/mermaid.esm.min.mjs';\n" +
                "    mermaid.initialize({ startOnLoad: true });\n" +
                "  </script>\n" +
                "  <style>\n" +
                "    #diagram-container {\n" +
                "      overflow: auto;\n" +
                "      height: 95vh;\n" +
                "    }\n" +
                "\n" +
                "    #diagram-scale-wrapper {\n" +
                "      transform-origin: top left;\n" +
                "    }\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "  <div id=\"diagram-container\">\n" +
                "    <div id=\"diagram-scale-wrapper\">\n" +
                "      <div class=\"mermaid\">\n" + generateUML() + "</div>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "</body>\n" +
                "</html>";
        browser.loadHTML(html);
    }

    private void export() {
        new ExportUMLDialog(generateUML()).show();
    }

    private String generateUML() {
        AtomicReference<String> result = new AtomicReference<>();
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor != null) {
            PsiFile file = PsiManager.getInstance(project).findFile(editor.getVirtualFile());
            if (file instanceof LSFFile) {
                ApplicationManager.getApplication().runReadAction(() -> {
                    Map<String, List<PropertyOrAction>> classPropOrActionMap = new OrderedHashMap<>();
                    Map<String, List<String>> classExtendsMap = new OrderedHashMap<>();

                    for (LSFFile moduleFile : getModuleFiles((LSFFile) file)) {
                        for (PsiElement child : (moduleFile).getStatements()) {
                            if (child instanceof LSFClassStatementImpl cls) {
                                if (!cls.isInMetaDecl()) {
                                    String className = cls.getClassName();
                                    if (!classPropOrActionMap.containsKey(className)) {
                                        classPropOrActionMap.put(className, new ArrayList<>());
                                        List<String> classExtends = cls.getExtends().stream().map(c -> c.name).collect(Collectors.toList());
                                        if (!classExtends.isEmpty()) {
                                            classExtendsMap.put(className, classExtends);
                                        }
                                    }
                                }
                            } else if (child instanceof LSFExplicitInterfaceActionOrPropStatement property) {
                                if (!property.isInMetaDecl()) {
                                    List<LSFClassSet> params = property.getDeclaration().resolveParamClasses();
                                    if (params != null && !params.isEmpty()) {
                                        String signature = params.size() > 1 ?
                                                "(" + params.subList(1, params.size()).stream().map(c -> c != null ? String.valueOf(c) : "?").collect(joining(", ")) + ")"
                                                : "";

                                        String value = null;
                                        if (property instanceof LSFExplicitInterfacePropStatement) {
                                            Set<String> valueClasses = ((LSFExplicitInterfacePropStatement) property).getExplicitValues();
                                            if (!valueClasses.isEmpty())
                                                value = String.join(",", valueClasses);
                                        } else {
                                            value = "";
                                        }
                                        if (value != null) {
                                            PropertyOrAction propertyOrAction = new PropertyOrAction(property.getName(), signature, value, property.isAction());
                                            classPropOrActionMap.computeIfAbsent(String.valueOf(params.get(0)), k -> new ArrayList<>()).add(propertyOrAction);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    StringBuilder mermaid = new StringBuilder("classDiagram\n");
                    for (Map.Entry<String, List<PropertyOrAction>> entry : classPropOrActionMap.entrySet()) {
                        String properties = StringUtils.join(entry.getValue().stream().filter(p -> !p.isAction).toList(), '\n');
                        String actions = StringUtils.join(entry.getValue().stream().filter(p -> p.isAction).toList(), '\n');
                        String fields = (properties.isEmpty() && actions.isEmpty() ? "" : ("{\n" + properties + actions + "\n}"));
                        String classEntry = "class " + getClassName(entry.getKey()) + fields + "\n";
                        mermaid.append(classEntry);
                    }
                    for (Map.Entry<String, List<String>> entry : classExtendsMap.entrySet()) {
                        for (String extend : entry.getValue()) {
                            String extendClassEntry = getClassName(entry.getKey()) + " --> " + getClassName(extend) + "\n";
                            mermaid.append(extendClassEntry);
                        }
                    }
                    result.set(mermaid.toString());
                });
            }
        }
        return result.get();
    }

    double zoomLevel = 1;

    private void zoom(int iterations) {
        switch (iterations) {
            case 1:
                zoomLevel = zoomLevel * 1.2;
                break;
            case 0:
                zoomLevel = 1;
                break;
            case -1:
                zoomLevel = zoomLevel * 0.8;
                break;
        }
        browser.getCefBrowser().executeJavaScript(
                "document.getElementById(\"diagram-scale-wrapper\").style.transform = `scale(" + zoomLevel + ")`;",
                browser.getCefBrowser().getURL(), 0
        );
    }

    private OrderedHashSet<LSFFile> getModuleFiles(LSFFile file) {
        OrderedHashSet<LSFFile> files = new OrderedHashSet<>();
        files.add(file);
        boolean required = requiredCheckBox.isSelected();
        if (required) {
            LSFModuleDeclaration moduleDeclaration = file.getModuleDeclaration();
            String moduleDeclarationName = moduleDeclaration.getName();
            if (moduleDeclarationName != null) {
                for (LSFModuleDeclaration module : file.getModuleDeclaration().getRequireModules()) {
                    if (!moduleDeclarationName.equals(module.getName())) {
                        files.addAll(getModuleFiles(module.getLSFFile()));
                    }
                }
            }
        }
        return files;
    }

    private String getClassName(String className) {
        //forbidden symbols: []
        return className.matches(".*[\\[\\]].*") ? ("`" + className + "`") : className;
    }

    private static class PropertyOrAction {
        private final String name;
        private final String signature;
        private final String value;
        private final boolean isAction;

        public PropertyOrAction(String name, String signature, String value, boolean isAction) {
            this.name = name;
            this.signature = signature;
            this.value = value;
            this.isAction = isAction;
        }

        public String toString() {
            return "+ " + name + signature + (value.isEmpty() ? "" : (" : " + value));
        }
    }

    public class ExportUMLDialog extends DialogWrapper {
        private final String text;

        public ExportUMLDialog(String text) {
            super(project);
            this.text = text;
            init();
            setTitle("UML Diagram");
        }

        @Override
        protected @Nullable JComponent createCenterPanel() {
            JTextPane sourceTextPane = new JTextPane();
            sourceTextPane.setText(text);
            sourceTextPane.setBackground(null);
            sourceTextPane.setEditable(false);
            return new JBScrollPane(sourceTextPane);
        }
    }
}

