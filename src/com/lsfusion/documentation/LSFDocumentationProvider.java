package com.lsfusion.documentation;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ui.JBUI;
import com.lsfusion.lang.psi.LSFFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class LSFDocumentationProvider extends AbstractDocumentationProvider {

    public static final Map<String, String> documentation = new HashMap<>();
    public static final Map<String, String> documentationVersionMap = new HashMap<>();
    public static final Map<String, String> documentationLanguageMap = new HashMap<>();
    private static final String documentationVersionPropertyKey = "documentationVersion";
    private static final String documentationLanguagePropertyKey = "documentationLanguage";

    static  {
        documentationVersionMap.put("Version 4", "");
        documentationVersionMap.put("Master", "next/");

        documentationLanguageMap.put("en", "");
        documentationLanguageMap.put("ru", "ru/");
    }

    public static void setDocumentationVersion(String version) {
        PropertiesComponent.getInstance().setValue(documentationVersionPropertyKey, version);
    }

    public static String getDocumentationVersion() {
        String version = PropertiesComponent.getInstance().getValue(documentationVersionPropertyKey);
        return version != null ? version : "Version 4";
    }

    public static void setDocumentationLanguage(String language) {
        PropertiesComponent.getInstance().setValue(documentationLanguagePropertyKey, language);
    }

    public static String getDocumentationLanguage() {
        String language = PropertiesComponent.getInstance().getValue(documentationLanguagePropertyKey);
        return language != null ? language : "en";
    }

    private String getDocumentationURL(PsiElement element) {
        PsiElement parentElement = element;

        boolean elementContainsDocumentation = false;
        while (!elementContainsDocumentation && !(parentElement instanceof LSFFile)) { // search documentation only in current file elements
            elementContainsDocumentation = documentation.containsKey(parentElement.getClass().getSimpleName());

            parentElement = !elementContainsDocumentation ? parentElement.getParent() : parentElement;
        }

        return "https://docs.lsfusion.org/" +
                documentationLanguageMap.getOrDefault(getDocumentationLanguage(), "") + // default language is en
                documentationVersionMap.getOrDefault(getDocumentationVersion(), "") + // default version is current
                documentation.get(parentElement.getClass().getSimpleName()) + "/";
    }

    @Override
    public @Nullable String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        return readExternalDocumentation(getDocumentationURL(element));
    }

    private String readExternalDocumentation(String documentationURL) {
        try {
            Document document = Jsoup.connect(documentationURL).get();
            Elements article = document.select("article");
            article.select("button").remove(); //remove "copy" buttons under code-blocks
            article.select("a[class=hash-link]").remove(); //remove all "#" hash-links in headers
            article.select("img").remove(); //remove all images

            // wrap code-blocks
            Color backgroundColor = JBUI.CurrentTheme.EditorTabs.underlinedTabBackground();
            Color color = JBUI.CurrentTheme.EditorTabs.underlinedTabForeground();
            String cssColor = "color:rgb(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")";
            String cssBackgroundColor = "background-color:rgb(" + backgroundColor.getRed() + "," + backgroundColor.getGreen() + "," + backgroundColor.getBlue() + ")";
            article.select("div[class~=codeBlockLines]").attr("style", cssBackgroundColor);
            Elements styles = article.select("div[class=token-line]").attr("style", cssColor);
            for (Element style : styles) {
                Element div = new Element("code");
                div.html(style.html());
                style.html(div.outerHtml());
            }

            //wrap headers with built in class
            Elements headers = article.select("h1, h2, h3, h4, h5, h6");
            for (Element header : headers) {
                Element div = new Element("div");
                div.attr("class", "definition");
                div.html(header.outerHtml());
                header.html(div.outerHtml());
            }

            return Jsoup.clean(article.html(), "https://docs.lsfusion.org/",
                    Whitelist.relaxed().addAttributes("div", "class", "style"));
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public @Nullable PsiElement getCustomDocumentationElement(@NotNull Editor editor, @NotNull PsiFile file, @Nullable PsiElement contextElement, int targetOffset) {
        String elementText = contextElement != null ? contextElement.getText() : null;
        return (elementText != null && elementText.contains("\n")) || contextElement instanceof PsiComment
                ? null
                : contextElement; //documentation for all elements without line breaks and comments
    }
}
