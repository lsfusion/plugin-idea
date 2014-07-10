package com.lsfusion.references;

import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.XmlElementVisitor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.*;
import com.lsfusion.lang.LSFLanguage;
import com.lsfusion.util.LSFPsiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.lsfusion.references.FormDeclByReportNameResolver.resolveFormFullNameAndRequires;

public class LSFToJrxmlLanguageInjector implements MultiHostInjector {
    public static final String headerPostfix = ".header";
    public static final String footerPostfix = ".footer";
    public static final String objectPostfix = ".object";
    public static final Pattern fieldExprPattern = Pattern.compile("(\\$F\\s*\\{\\s*)([\\w\\(\\)\\,\\.]+)(\\s*\\})");

    @Override
    public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement context) {
        assert context instanceof XmlDocument;

        XmlDocument xmlDocument = (XmlDocument) context;
        XmlTag rootTag = xmlDocument.getRootTag();
        
        if (rootTag == null) {
            return;
        }

        VirtualFile virtualFile = xmlDocument.getContainingFile().getVirtualFile();
        if (virtualFile == null || !"jrxml".equals(virtualFile.getExtension())) {
            return;
        }

        GlobalSearchScope scope = LSFPsiUtils.getModuleScope(xmlDocument);
        Pair<String, String> formNameAndRequires = resolveFormFullNameAndRequires(virtualFile, xmlDocument.getProject(), scope);
        if (formNameAndRequires == null) {
            return;
        }

        String formName = formNameAndRequires.first;
        String requresList = formNameAndRequires.second;

        final List<Injection> injections = new ArrayList<Injection>();
        
        rootTag.acceptChildren(new XmlElementVisitor() {
            @Override
            public void visitXmlTag(XmlTag tag) {
                tag.acceptChildren(this);
            }

            @Override
            public void visitXmlAttribute(XmlAttribute attribute) {
                if ("name".equals(attribute.getName())) {
                    XmlTag tag = attribute.getParent();
                    if ("field".equals(tag.getName())) {
                        String fieldName = attribute.getValue();
                        if (fieldName != null) {
                            XmlAttributeValue attrValue = attribute.getValueElement();
                            if (attrValue == null) {
                                return;
                            }

                            resolveAndAddInjection(attrValue, fieldName, 1, attrValue.getTextLength() - 1);
                        }
                    }
                }
            }

            @Override
            public void visitXmlText(XmlText xmlText) {
                String value = xmlText.getText();
                Matcher m = fieldExprPattern.matcher(value);
                while (m.find()) {
                    resolveAndAddInjection(xmlText, m.group(2), m.start(2), m.end(2));
                }
            }

            private void resolveAndAddInjection(PsiElement host, String refText, int startOffset, int endOffset) {
                assert host instanceof PsiLanguageInjectionHost;
                
                boolean isObjRef = refText.endsWith(objectPostfix);
                if (isObjRef) {
                    endOffset -= objectPostfix.length();
                } else if (refText.endsWith(headerPostfix)) {
                    endOffset -= headerPostfix.length();
                } else if (refText.endsWith(footerPostfix)) {
                    endOffset -= footerPostfix.length();
                }
                
                injections.add(new Injection(isObjRef, (PsiLanguageInjectionHost) host, new TextRange(startOffset, endOffset)));
            }
        });
        
        if (injections.size() != 0) {
            registrar.startInjecting(LSFLanguage.INSTANCE);
            
            boolean first = true;
            for (Injection injection : injections) {
                String prefix = "\nEXTERNAL " + (injection.isObjRef ? "OBJECT " : "PROPERTYDRAW ") + formName + " ";
                if (first) {
                    prefix = "MODULE x; REQUIRE " + requresList + "; " + prefix;
                    first = false;
                }
                
                registrar.addPlace(prefix, "; ", injection.host, injection.rangeInsideHost);
            }
            
            registrar.doneInjecting();
        }
    }

    @NotNull
    @Override
    public List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
        return Collections.singletonList(XmlDocument.class);
    }

    private static class Injection {
        public final boolean isObjRef;
        public final PsiLanguageInjectionHost host;
        public final TextRange rangeInsideHost;

        private Injection(boolean isObjRef, PsiLanguageInjectionHost host, TextRange rangeInsideHost) {
            this.isObjRef = isObjRef;
            this.host = host;
            this.rangeInsideHost = rangeInsideHost;
        }
    }
}
