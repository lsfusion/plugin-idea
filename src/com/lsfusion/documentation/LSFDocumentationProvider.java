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

    static {
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
        String documentation;
        PsiElement docElement = element;

        while (true) {
            PsiElement parentElement = docElement.getParent();

            documentation = parentElement instanceof LSFDocumentation ? ((LSFDocumentation) parentElement).getDocumentation(docElement) : null;

            if (documentation != null || parentElement instanceof LSFFile)
                break;

            docElement = parentElement;
        }

        return documentation != null ? "https://docs.lsfusion.org/" +
                documentationLanguageMap.getOrDefault(getDocumentationLanguage(), "") + // default language is en
                documentationVersionMap.getOrDefault(getDocumentationVersion(), "") + // default version is current
                documentation : null;
    }

    @Override
    public @Nullable String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        String documentationURL = getDocumentationURL(element);
        return documentationURL != null ? readExternalDocumentation(documentationURL) : null;
    }

    private String readExternalDocumentation(String documentationURL) {
        try {
            Document document = Jsoup.connect(documentationURL).get();
            Elements article = document.select("article");
            article.select("button").remove(); //remove "copy" buttons under code-blocks
            article.select("a[class=hash-link]").remove(); //remove all "#" hash-links in headers
            article.select("img").remove(); //remove all images
            article.select("footer").remove(); //remove "Edit this page" link

            // wrap code-blocks
            Color backgroundColor = JBUI.CurrentTheme.BigPopup.searchFieldBackground();
            Color color = JBUI.CurrentTheme.BigPopup.selectedTabTextColor();
            String cssColor = "color:rgb(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")";
            String cssBackgroundColor = "background-color:rgb(" + backgroundColor.getRed() + "," + backgroundColor.getGreen() + "," + backgroundColor.getBlue() + ")";

            //for docosaurus version 2.0.0-beta.1
            article.select("div[class~=codeBlockContainer]").attr("style", cssBackgroundColor);
            Elements codeElements = article.select("span[class=token-line]");

            //for docosaurus version 2.0.0-beta.0
            article.select("div[class~=codeBlockLines]").attr("style", cssBackgroundColor);
            Elements oldCodeElements = article.select("div[class=token-line]");

            Elements styles = codeElements.size() == 0 ? oldCodeElements : codeElements;
            styles.attr("style", cssColor);

            for (Element style : styles) {
                Element div = new Element("code");
                div.html(style.html());
                style.html(div.outerHtml());
                style.appendElement("br");
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
                    Whitelist.relaxed()
                            .addAttributes("div", "class", "style")
                            .addAttributes("span", "style"));
        } catch (IOException e) {
            return null;
        }
    }

    //disable popup on mouse hover. Available only since 193.5233.102
    //To disable manually: (Idea 211) Go to Settings -> Editor -> Code Editing and uncheck 'Show quick documentation on mouse move'
//    @Override
//    public @Nullable String generateHoverDoc(@NotNull PsiElement element, @Nullable PsiElement originalElement) {
//        return null;
//    }

    @Override
    public @Nullable PsiElement getCustomDocumentationElement(@NotNull Editor editor, @NotNull PsiFile file, @Nullable PsiElement contextElement) {
        String elementText = contextElement != null ? contextElement.getText() : null;
        return (elementText != null && elementText.contains("\n")) || contextElement instanceof PsiComment
                ? null
                : contextElement; //documentation for all elements without line breaks and comments
    }
}
