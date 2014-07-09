package com.lsfusion.references;

import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.InjectedLanguagePlaces;
import com.intellij.psi.LanguageInjector;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.lsfusion.lang.LSFLanguage;
import com.lsfusion.util.LSFPsiUtils;
import org.jetbrains.annotations.NotNull;

import static com.lsfusion.references.FormDeclByReportNameResolver.resolveFormFullNameAndRequires;

public class LSFToJrxmlLanguageInjector implements LanguageInjector {
    @Override
    public void getLanguagesToInject(@NotNull PsiLanguageInjectionHost element, @NotNull InjectedLanguagePlaces injectionPlacesRegistrar) {
        if (element instanceof XmlAttributeValue) {
            final PsiFile containingFile = element.getContainingFile();
            if (containingFile.getFileType() != XmlFileType.INSTANCE) {
                return;
            }

            VirtualFile virtualFile = containingFile.getVirtualFile();
            if (virtualFile != null && "jrxml".equals(virtualFile.getExtension())) {
                XmlAttributeValue attrVaue = (XmlAttributeValue) element;

                if (attrVaue.getParent() instanceof XmlAttribute) {
                    XmlAttribute attr = (XmlAttribute) attrVaue.getParent();
                    XmlTag tag = attr.getParent();

                    if (tag.getName().equals("field") && attr.getName().equals("name")) {

                        GlobalSearchScope scope = LSFPsiUtils.getModuleScope(containingFile);

                        Pair<String, String> formNameAndRequires = resolveFormFullNameAndRequires(virtualFile, containingFile.getProject(), scope);

                        if (formNameAndRequires == null) {
                            return;
                        }

                        String formName = formNameAndRequires.first;
                        String requresList = formNameAndRequires.second;

                        injectionPlacesRegistrar.addPlace(
                                LSFLanguage.INSTANCE,
                                new TextRange(1, element.getTextLength() - 1),
                                "MODULE x; REQUIRE " + requresList + "; EXTERNAL PROPERTYDRAW " + formName + " ",
                                ";"
                        );
                    }
                }
            }
        }
    }

}
