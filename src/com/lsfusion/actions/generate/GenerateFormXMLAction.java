package com.lsfusion.actions.generate;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenerateFormXMLAction extends GenerateFormAction {

    @Override
    String getExtension() {
        return "xml";
    }

    @Override
    protected Object getRootElement(String file) throws JDOMException, IOException {
        return file != null ? new SAXBuilder().build(new ByteArrayInputStream(file.getBytes(getCharset(file)))).getRootElement(): null;
    }

    private Charset getCharset(String file) {
        try {
            Pattern p = Pattern.compile("<\\?xml version=\".*\" encoding=\"(.*)\".*");
            Matcher m = p.matcher(file.substring(0, Math.max(file.indexOf("\n"), file.length())));
            return m.matches() ? Charset.forName(m.group(1)) : StandardCharsets.UTF_8;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse charset", e);
        }
    }

    @Override
    protected List<ParseNode> generateHierarchy(Object element, String key) {
        return generateHierarchyXML((Element) element, key);
    }

    private List<ParseNode> generateHierarchyXML(Element element, String key) {
        List<ParseNode> children = new ArrayList<>();

        ElementNamespace namespace = getElementNamespace(element);

        boolean noChildren = element.getChildren().isEmpty();
        boolean noAttributes = element.getAttributes().isEmpty();
        boolean isPropertyParseNode = noChildren && noAttributes;

        if (isPropertyParseNode) {
            return Collections.singletonList(new PropertyParseNode(key, namespace, false));
        } else {

            Map<String, List<Element>> childrenMap = getChildrenMap(element);

            for (Map.Entry<String, List<Element>> childEntry : childrenMap.entrySet()) {
                String childKey = childEntry.getKey();
                List<Element> childrenList = childEntry.getValue();

                boolean isGroupObjectParseNode = childrenList.size() > 1;

                if (isGroupObjectParseNode) {

                    List<ParseNode> localChildren = new ArrayList<>();
                    for (Element c : childrenList) {
                        localChildren.addAll(generateHierarchyXML(c, childKey));
                    }

                    ParseNode mergedChild = deepMerge(localChildren);
                    boolean integrationKey = mergedChild.children.size() > 0;

                    List<ParseNode> nChildren = new ArrayList<>();
                    nChildren.add(mergedChild);

                    //get namespace of first child, all children should be with equal namespace
                    ElementNamespace childNamespace = getElementNamespace(childEntry.getValue().get(0));
                    children.add(new GroupObjectParseNode(childKey, nChildren, childNamespace, integrationKey));

                } else { //propertyGroupParseNode, childrenList.size() == 1

                    for (Element child : childrenList) {
                        children.addAll(generateHierarchyXML(child, childKey));
                    }
                }
            }

            for (Attribute attribute : element.getAttributes()) {
                children.add(new PropertyParseNode(attribute.getName(), namespace, true));
            }

            //declared in this node namespaces
            for(Namespace ns : element.getNamespacesIntroduced()) {
                children.add(new NamespaceParseNode(new ElementNamespace(ns.getPrefix(), ns.getURI())));
            }

            List<ParseNode> result = new ArrayList<>();
            if(noChildren) { //assert !noAttributes
                result.add(new PropertyParseNode(key, namespace, false));
            }
            result.add(new PropertyGroupParseNode(key, children, namespace, key == null ? element.getName() : null));
            return result;
        }
    }

    private Map<String, List<Element>> getChildrenMap(Element element) {
        Map<String, List<Element>> childrenMap = new LinkedHashMap<>();
        for(Element child : element.getChildren()) {
            List<Element> childrenList = childrenMap.get(child.getName());
            if(childrenList == null)
                childrenList = new ArrayList<>();
            childrenList.add(child);
            childrenMap.put(child.getName(), childrenList);
        }
        return childrenMap;
    }

    private ElementNamespace getElementNamespace(Element element) {
        String prefix = element.getNamespacePrefix();
        String uri = element.getNamespaceURI();
        return /*!prefix.isEmpty() ? */new ElementNamespace(prefix, uri)/* : null*/;
    }
}
