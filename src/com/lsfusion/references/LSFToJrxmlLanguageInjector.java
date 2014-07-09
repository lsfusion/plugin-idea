package com.lsfusion.references;

import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.project.Project;
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
import com.intellij.util.Processor;
import com.intellij.util.Query;
import com.lsfusion.lang.LSFLanguage;
import com.lsfusion.lang.psi.LSFGlobalResolver;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import com.lsfusion.lang.psi.stubs.types.indexes.FormIndex;
import com.lsfusion.util.LSFPsiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class LSFToJrxmlLanguageInjector implements LanguageInjector {
    @Override
    public void getLanguagesToInject(@NotNull PsiLanguageInjectionHost element, @NotNull InjectedLanguagePlaces injectionPlacesRegistrar) {
        if (element instanceof XmlAttributeValue) {
            PsiFile containingFile = element.getContainingFile();
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

                        Pair<String, String> formNameAndRequires =
                                new FormDeclResolver(containingFile).resolveFormFullNameAndRequires(virtualFile);

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

    private class FormDeclResolver {
        final PsiFile file;
        final Project project;
        final GlobalSearchScope scope;

        private FormDeclResolver(PsiFile file) {
            this.file = file;
            project = file.getProject();
            scope = LSFPsiUtils.getModuleScope(file);
        }

        private Pair<String, String> resolveFormFullNameAndRequires(VirtualFile virtualFile) {
            String fileName = virtualFile.getNameWithoutExtension();

            LSFFormDeclaration formDeclaration = resolveFull(fileName);

            if (formDeclaration != null) {
                Query<LSFFormExtend> formExtends = LSFGlobalResolver.findExtendElements(formDeclaration, LSFStubElementTypes.EXTENDFORM, project, scope);
                final Set<String> requiredModules = new HashSet<String>();
                formExtends.forEach(new Processor<LSFFormExtend>() {
                    @Override
                    public boolean process(LSFFormExtend extend) {
                        requiredModules.add(extend.getLSFFile().getModuleDeclaration().getGlobalName());
                        return true;
                    }
                });
                String requires = "";
                for (String moduleName : requiredModules) {
                    if (requires.length() > 0) {
                        requires += ",";
                    }
                    requires += moduleName;
                }
                return new Pair<String, String>(formDeclaration.getNamespaceName() + "." + formDeclaration.getGlobalName(), requires);
            }

            return null;
        }

        private LSFFormDeclaration resolveFull(String fileName) {
            //[tableid_].*
            LSFFormDeclaration formDeclaration = resolveWithMaybeTablePrefix(fileName);
            if (formDeclaration != null) {
                return formDeclaration;
            }
            //xls_.*
            return resolveWithXLSPrefix(fileName);
        }

        private LSFFormDeclaration resolveWithXLSPrefix(String fileName) {
            if (fileName.startsWith("xls_")) {
                return resolveWithMaybeTablePrefix(fileName.substring(4));
            }
            return null;
        }

        private LSFFormDeclaration resolveWithMaybeTablePrefix(String fileName) {
            LSFFormDeclaration formDecl = resolveWithMaybePostfix(fileName);
            if (formDecl != null) {
                return formDecl;
            }

            if (fileName.startsWith("table")) {
                int underscoreInd = fileName.indexOf('_', 6);
                while (underscoreInd != -1) {
                    formDecl = resolveWithMaybePostfix(fileName.substring(underscoreInd));
                    if (formDecl != null) {
                        return formDecl;
                    }
                    underscoreInd = fileName.indexOf('_', underscoreInd + 1);
                }
            }

            return null;
        }

        private LSFFormDeclaration resolveWithMaybePostfix(String fileName) {
            LSFFormDeclaration formDecl = resolve(fileName);
            if (formDecl != null) {
                return formDecl;
            }

            int underscoreInd = fileName.lastIndexOf('_');
            while (underscoreInd != -1) {
                formDecl = resolve(fileName.substring(0, underscoreInd));
                if (formDecl != null) {
                    return formDecl;
                }
                underscoreInd = fileName.lastIndexOf('_', underscoreInd - 1);
            }

            return null;
        }

        private LSFFormDeclaration resolve(String fileName) {
            int underscoreInd = fileName.indexOf("_");
            if (underscoreInd == -1) {
                return null;
            }

            String namespace = fileName.substring(0, underscoreInd);
            String formName = fileName.substring(underscoreInd + 1);

            Collection<LSFFormDeclaration> decls = FormIndex.getInstance().get(formName, project, scope);
            for (LSFFormDeclaration decl : decls) {
                if (namespace.equals(decl.getNamespaceName())) {
                    return decl;
                }

            }
            return null;
        }
    }
}
