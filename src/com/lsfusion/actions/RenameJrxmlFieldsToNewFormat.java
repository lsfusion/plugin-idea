package com.lsfusion.actions;

import com.google.common.collect.Sets;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.notification.impl.NotificationsConfigurationImpl;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.XmlElementVisitor;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlText;
import com.intellij.util.Processor;
import com.intellij.util.Query;
import com.intellij.util.containers.ContainerUtil;
import com.lsfusion.lang.psi.LSFGlobalResolver;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import com.lsfusion.lang.psi.declarations.LSFObjectDeclaration;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import com.lsfusion.references.LSFToJrxmlLanguageInjector;
import com.lsfusion.util.LSFFileUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.lsfusion.references.FormDeclByReportNameResolver.resolveByFullFileName;
import static com.lsfusion.references.FormDeclByReportNameResolver.resolveByVirtualFile;
import static org.apache.commons.lang.StringUtils.removeEnd;

/**
 * prop(caption) -> prop.header
 * prop(footer)  -> prop.footer
 * obj           -> obj.object
 */
public class RenameJrxmlFieldsToNewFormat extends AnAction {

    private static final Pattern fieldExprPattern = Pattern.compile("(\\$F\\s*\\{\\s*)([\\w\\(\\)\\,]+)(\\s*\\})");

    private static final String oldHeaderPostfix = "(caption)";
    private static final String oldFooterPostfix = "(footer)";

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project project = e.getProject();
        ProgressManager.getInstance().run(
                new Task.Modal(project, "Renaming jrxml fields to new format", true) {
                    public void run(final @NotNull ProgressIndicator indicator) {
                        final boolean showBalloonsState = NotificationsConfigurationImpl.getNotificationsConfigurationImpl().SHOW_BALLOONS;
                        NotificationsConfigurationImpl.getNotificationsConfigurationImpl().SHOW_BALLOONS = false;

                        renameInAllFiles(indicator, project);

                        ApplicationManager.getApplication().invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                NotificationsConfigurationImpl.getNotificationsConfigurationImpl().SHOW_BALLOONS = showBalloonsState;
                            }
                        });
                    }
                });
    }

    private void renameInAllFiles(final ProgressIndicator indicator, final Project project) {
        final GlobalSearchScope projectScope = ProjectScope.getProjectScope(project);
        final PsiManager psiManager = PsiManager.getInstance(project);

        final Collection<VirtualFile> xmlFiles = ApplicationManager.getApplication().runReadAction(
                new Computable<Collection<VirtualFile>>() {
                    public Collection<VirtualFile> compute() {
                        return FileTypeIndex.getFiles(XmlFileType.INSTANCE, projectScope);
                    }
                }
        );

        final List<VirtualFile> jrxmlFiles = ContainerUtil.filter(xmlFiles, new Condition<VirtualFile>() {
            @Override
            public boolean value(VirtualFile virtualFile) {
                return "jrxml".equals(virtualFile.getExtension());
            }
        });

        ApplicationManager.getApplication().runReadAction(new Runnable() {
            @Override
            public void run() {
                int filesCount = jrxmlFiles.size();
                for (int i = 0; i < filesCount; i++) {
                    final VirtualFile file = jrxmlFiles.get(i);
                    String fileName = file.getName();
                    indicator.setText("Processing: " + fileName + "(" + i + "/" + filesCount + ")");
//                    Notifications.Bus.notify(new Notification("", "", "Processing: " + relativePath + "(" + i + "/" + filesCount + ")", NotificationType.INFORMATION));

                    renameInFile(file, project, projectScope, psiManager);

                    indicator.checkCanceled();
                }
            }
        });
    }

    Map<String, String> explicitLink = new HashMap<String, String>() {{
        put("LabelMothercare_printLabelTransactionA_l", "Label_printLabelTransaction");
        put("LabelMothercare_printLabelTransactionB_l", "Label_printLabelTransaction");
        put("LabelMothercare_printLabelTransactionC1_l", "Label_printLabelTransaction");
        put("LabelMothercare_printLabelTransactionC_l", "Label_printLabelTransaction");
        put("LabelMothercare_printLabelTransactionD_l", "Label_printLabelTransaction");
        put("LabelMothercare_printLabelTransactionE_l", "Label_printLabelTransaction");
        put("LabelSOT_printLabelTransactionA_l", "Label_printLabelTransaction");
        put("LabelDolce_printLabelTransaction_l", "Label_printLabelTransaction");
    }};

    Map<String, Set<String>> explicitObjects = new HashMap<String, Set<String>>() {{
        put("annexInvoiceForm", Sets.newHashSet("freight", "importer", "typeInvoice", "article", "composition", "country", "category"));
        put("annexInvoiceForm2", Sets.newHashSet("freight", "importer", "typeInvoice", "article", "composition", "country", "category"));
        put("createFreightBoxForm", Sets.newHashSet("creationFreightBox", "freightBox"));
        put("createPalletForm", Sets.newHashSet("creationPallet", "pallet"));
        put("createSkuForm", Sets.newHashSet("obj1", "obj7"));
        put("invoiceExportForm", Sets.newHashSet("freight", "importer", "typeInvoice", "freightBox", "sku"));
        put("invoiceFromForm", Sets.newHashSet("freight", "importer", "typeInvoice", "article", "composition", "country", "category"));
        put("invoiceFromForm2", Sets.newHashSet("freight", "importer", "typeInvoice", "article", "composition", "country", "category"));
        put("listFreightUnitFreightForm", Sets.newHashSet("freight", "importer", "brand", "freightUnit"));
        put("packingListBoxForm", Sets.newHashSet("box", "article", "brand", "freightUnit"));
        put("packingListForm", Sets.newHashSet("freight", "importer", "typeInvoice", "freightBox", "article"));
        put("packingListForm2", Sets.newHashSet("freight", "importer", "typeInvoice", "freightBox", "article"));
        put("printSkuForm", Sets.newHashSet("sku"));
        put("proformInvoiceForm", Sets.newHashSet("freight", "importer", "article", "composition", "country", "category"));
        put("proformInvoiceForm2", Sets.newHashSet("freight", "importer", "article", "composition", "country", "category"));
        put("sbivkaForm", Sets.newHashSet("freight", "importer", "customCategory6", "category"));
        put("sbivkaSupplierForm", Sets.newHashSet("freight", "importer", "supplier", "customCategory6", "category"));
    }};
    
    Set<String> skipFiles = Sets.newHashSet("Revaluation_revaluationDate");

    private void renameInFile(VirtualFile file, final Project project, GlobalSearchScope scope, final PsiManager psiManager) {
        final String fileName = file.getNameWithoutExtension();
        
        if (skipFiles.contains(fileName)) {
            return;
        }
        
        final String relativePath = LSFFileUtils.getRelativePath(file, project);

        int underscorePos = fileName.indexOf('_');
        String explicitFormName = underscorePos == -1 ? fileName : fileName.substring(0, underscorePos);

        final Set<String> objects;
        if (explicitObjects.containsKey(explicitFormName)) {
            System.out.println("Using explicit objects for: " + relativePath);
            objects = explicitObjects.get(explicitFormName);
        } else {
            objects = new HashSet<String>();

            final LSFFormDeclaration formDecl = explicitLink.containsKey(fileName)
                                                ? resolveByFullFileName(explicitLink.get(fileName), project, scope)
                                                : resolveByVirtualFile(file, project, scope);
            if (formDecl == null) {
                Notifications.Bus.notify(new Notification("", "", "Can't find form for: " + relativePath, NotificationType.ERROR));
                return;
            }

            Query<LSFFormExtend> formExtends = LSFGlobalResolver.findExtendElements(formDecl, LSFStubElementTypes.EXTENDFORM, project, scope);
            formExtends.forEach(new Processor<LSFFormExtend>() {
                @Override
                public boolean process(LSFFormExtend extend) {
                    for (LSFObjectDeclaration obj : extend.getObjectDecls()) {
                        objects.add(obj.getDeclName());
                    }
                    return true;
                }
            });
        }

        final PsiFile psiFile = psiManager.findFile(file);
        if (psiFile instanceof XmlFile) {
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    CommandProcessor.getInstance().runUndoTransparentAction(new Runnable() {
                        @Override
                        public void run() {
                            ApplicationManager.getApplication().runWriteAction(new Runnable() {
                                @Override
                                public void run() {
                                    XmlFile xmlFile = (XmlFile) psiFile;
                                    XmlTag rootTag = xmlFile.getRootTag();

                                    assert rootTag != null;

                                    rootTag.acceptChildren(new XmlElementVisitor() {
                                        @Override
                                        public void visitXmlTag(XmlTag tag) {
                                            tag.acceptChildren(this);
                                        }

                                        @Override
                                        public void visitXmlText(XmlText text) {
                                            String oldText = text.getValue();
                                            String newText = renameInText(oldText, objects);
                                            if (!oldText.equals(newText)) {
                                                text.setValue(newText);
                                            }
                                        }

                                        @Override
                                        public void visitXmlAttribute(XmlAttribute attribute) {
                                            if ("name".equals(attribute.getName())) {
                                                XmlTag tag = attribute.getParent();
                                                if ("field".equals(tag.getName())) {
                                                    String fieldName = attribute.getValue();
                                                    if (fieldName != null) {
                                                        String newFieldName = getNewFieldName(fieldName, objects);
                                                        if (!fieldName.equals(newFieldName)) {
                                                            attribute.setValue(newFieldName);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    private String renameInText(String text, Set<String> objects) {
        Matcher m = fieldExprPattern.matcher(text);

        StringBuffer result = new StringBuffer();
        while (m.find()) {
            String oldName = m.group(2);
            String newName = getNewFieldName(oldName, objects);
            m.appendReplacement(result, "$1" + newName + "$3");
        }
        m.appendTail(result);
        return result.toString();
    }

    @NotNull
    private String getNewFieldName(@NotNull String fieldName, Set<String> objects) {
        if (fieldName.endsWith(oldHeaderPostfix)) {
            return removeEnd(fieldName, oldHeaderPostfix) + LSFToJrxmlLanguageInjector.headerPostfix;
        }

        if (fieldName.endsWith(oldFooterPostfix)) {
            return removeEnd(fieldName, oldFooterPostfix) + LSFToJrxmlLanguageInjector.footerPostfix;
        }

        if (fieldName.contains("(")) {
            return fieldName;
        }

        if (objects.contains(fieldName)) {
            return fieldName + LSFToJrxmlLanguageInjector.objectPostfix;
        }

        return fieldName;
    }
}
