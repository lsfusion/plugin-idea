package com.lsfusion.dependencies;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.jcef.JBCefBrowser;
import com.lsfusion.design.ui.FlexAlignment;
import com.lsfusion.design.ui.FlexConstraints;
import com.lsfusion.design.ui.FlexPanel;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFExplicitInterfacePropStatement;
import com.lsfusion.lang.psi.impl.LSFClassStatementImpl;
import groovyjarjarantlr4.v4.misc.OrderedHashMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

public class ClassDiagramView extends DiagramView {
    private final Project project;
    private final JBCefBrowser browser;

    public ClassDiagramView(Project project) {
        this.project = project;
        this.browser = new JBCefBrowser();

        FlexPanel mainPanel = new FlexPanel(true);

        FlexPanel buttonsPanel = new FlexPanel(false);

        JButton updateButton = new JButton("Update Diagram");
        updateButton.addActionListener(e -> updateDiagram());
        buttonsPanel.add(updateButton);

        JButton zoomInButton = new JButton("Zoom In");
        zoomInButton.addActionListener(e -> zoom(1.2));
        buttonsPanel.add(zoomInButton);

        JButton zoomOutButton = new JButton("Zoom Out");
        zoomOutButton.addActionListener(e -> zoom(0.8));
        buttonsPanel.add(zoomOutButton);

        mainPanel.add(buttonsPanel, new FlexConstraints(FlexAlignment.STRETCH, 0));
        mainPanel.add(browser.getComponent(), new FlexConstraints(FlexAlignment.STRETCH, 1));

        updateDiagram();

        add(mainPanel, BorderLayout.CENTER);
    }

    @Override
    public void redraw() {
        updateDiagram();
    }

    private void updateDiagram() {
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor != null) {
            PsiFile file = PsiManager.getInstance(project).findFile(editor.getVirtualFile());
            if (file instanceof LSFFile) {
                ApplicationManager.getApplication().runReadAction(() -> {
                    Map<String, List<PropertyOrAction>> classPropOrActionMap = new OrderedHashMap<>();
                    Map<String, List<String>> classExtendsMap = new OrderedHashMap<>();
                    for (PsiElement child : ((LSFFile) file).getStatements()) {
                        if (child instanceof LSFClassStatementImpl) {
                            String className = ((LSFClassStatementImpl) child).getClassName();
                            if(!className.contains("#") && !classPropOrActionMap.containsKey(className)) {
                                classPropOrActionMap.put(className, new ArrayList<>());
                                List<String> classExtends = ((LSFClassStatementImpl) child).getExtends().stream().map(c -> c.name).collect(Collectors.toList());
                                if(!classExtends.isEmpty()) {
                                    classExtendsMap.put(className, classExtends);
                                }
                            }
                        } else if (child instanceof LSFExplicitInterfacePropStatement property) {
                            if (!property.isInMetaDecl()) {
                                List<LSFClassSet> params = property.getDeclaration().resolveParamClasses();
                                String cls = (params.isEmpty() ? "GlobalFields" : String.valueOf(params.get(0)));

                                String signature = params.size() > 1 ?
                                        "(" + params.subList(1, params.size()).stream().map(c -> c != null ? String.valueOf(c) : "?").collect(joining(", ")) + ")"
                                        : "";

                                Set<String> valueClasses = property.getExplicitValues();
                                String value = valueClasses != null && !valueClasses.isEmpty() ? (" " + StringUtils.join(valueClasses, ",")) : property.getDeclaration().getValuePresentableText();

                                PropertyOrAction propertyOrAction = new PropertyOrAction(property.getName(), signature, value, property.isAction());
                                classPropOrActionMap.computeIfAbsent(cls, k -> new ArrayList<>()).add(propertyOrAction);
                            }
                        }
                    }

                    StringBuilder mermaid = new StringBuilder("classDiagram\n");
                    for(Map.Entry<String, List<PropertyOrAction>> entry : classPropOrActionMap.entrySet()) {
                        String properties = Strings.join(entry.getValue().stream().filter(p -> !p.isAction).toList(), '\n');
                        String actions = Strings.join(entry.getValue().stream().filter(p -> p.isAction).toList(), '\n');
                        String fields = (properties.isEmpty() && actions.isEmpty() ? "" : ("{\n" + properties + actions + "\n}"));
                        String mermaidEntry = "class " + entry.getKey() + fields + "\n";
                        mermaid.append(mermaidEntry);
                    }
                    for(Map.Entry<String, List<String>> entry : classExtendsMap.entrySet()) {
                        for(String extend : entry.getValue()) {
                            mermaid.append(entry.getKey()).append(" --> ").append(extend).append("\n");
                        }
                    }

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
                            "      height: 90vh;\n" +
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
                            "      <div class=\"mermaid\">\n" + mermaid + "</div>\n" +
                            "    </div>\n" +
                            "  </div>\n" +
                            "</body>\n" +
                            "</html>";

                    browser.loadHTML(html);
                });
            }
        }
    }

    double zoomLevel = 1;
    private void zoom(double factor) {
        zoomLevel = zoomLevel * factor;
        browser.getCefBrowser().executeJavaScript(
                "document.getElementById(\"diagram-scale-wrapper\").style.transform = `scale(" + zoomLevel + ")`;",
                browser.getCefBrowser().getURL(), 0
        );
    }

    @Override
    public void dispose() {
        //ignore
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
            return "+ " + name + signature + value;
        }
    }
}

