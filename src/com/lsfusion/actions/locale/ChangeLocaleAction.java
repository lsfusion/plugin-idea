package com.lsfusion.actions.locale;

import com.intellij.lang.properties.psi.PropertiesFile;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Progressive;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.lang.LSFFileType;
import com.lsfusion.lang.LSFReferenceAnnotator;
import com.lsfusion.lang.LSFResourceBundleUtils;
import com.lsfusion.lang.psi.LSFLocalizedStringValueLiteral;
import com.lsfusion.util.BaseUtils;
import com.lsfusion.util.LSFFileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;

import static com.lsfusion.lang.LSFElementGenerator.createLocalizedStringLiteral;

public class ChangeLocaleAction extends AnAction {
    private Project project;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        project = getEventProject(e);
        ChangeLocaleDialog dialog = new ChangeLocaleDialog();
        dialog.show();
    }

    private class ChangeLocaleDialog extends DialogWrapper {
        Map<String, ResourceBundlesWithScope> resourceBundlesWithScopeMap;
        ComboBox<String> resourceBundlesBox;
        ComboBox<String> localesBox;
        JCheckBox idModeCheckBox;

        ChangeLocaleDialog() {
            super(project);
            init();
            setTitle("Change Locale");
        }

        @Nullable
        @Override
        public JComponent getPreferredFocusedComponent() {
            return localesBox;
        }

        @Nullable
        @Override
        protected JComponent createCenterPanel() {
            resourceBundlesWithScopeMap = new HashMap<>();

            for(Module module : LSFFileUtils.getModules(project)) {
                String lsfStrLiteralsLanguage = LSFResourceBundleUtils.getLsfStrLiteralsLanguage(module, true);
                if(lsfStrLiteralsLanguage != null) {
                    LSFResourceBundleUtils.ScopeData scopeData = LSFResourceBundleUtils.getScopeData(module);
                    for (Map.Entry<String, Map<String, PropertiesFile>> entry : scopeData.propertiesFiles.entrySet()) {
                        resourceBundlesWithScopeMap.put(entry.getKey(), new ResourceBundlesWithScope(module.getModuleScope(), entry.getValue(), lsfStrLiteralsLanguage));
                    }
                }
            }

            resourceBundlesBox = new ComboBox<>(resourceBundlesWithScopeMap.keySet().toArray(new String[]{}));
            resourceBundlesBox.setAlignmentX(Component.LEFT_ALIGNMENT);

            localesBox = new ComboBox<>();
            localesBox.setAlignmentX(Component.LEFT_ALIGNMENT);

            resourceBundlesBox.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    String item = (String) e.getItem();
                    ResourceBundlesWithScope selectedResourceBundle = resourceBundlesWithScopeMap.get(item);
                    localesBox.setModel(getComboBoxModel(selectedResourceBundle));
                    localesBox.setSelectedItem(selectedResourceBundle.lsfStrLiteralsLanguage);
                }
            });
            ResourceBundlesWithScope selectedResourceBundle = getSelectedResourceBundle();
            if(selectedResourceBundle != null) {
                localesBox.setModel(getComboBoxModel(selectedResourceBundle));
                localesBox.setSelectedItem(selectedResourceBundle.lsfStrLiteralsLanguage);
            }

            idModeCheckBox = new JCheckBox("Replace literals with IDs");
            idModeCheckBox.addItemListener(e -> localesBox.setEnabled(e.getStateChange() != ItemEvent.SELECTED));

            JPanel topPanel = new JPanel();
            topPanel.setLayout(new GridBagLayout());

            JPanel resourcePanel = new JPanel();
            resourcePanel.setLayout(new BoxLayout(resourcePanel, BoxLayout.Y_AXIS));
            JLabel resourceLabel = new JLabel(" Choose resource bundle");
            resourceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            resourcePanel.add(resourceLabel);

            resourcePanel.add(resourceBundlesBox);

            JPanel localePanel = new JPanel();
            localePanel.setLayout(new BoxLayout(localePanel, BoxLayout.Y_AXIS));
            JLabel localeLabel = new JLabel(" Choose locale");
            localeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            localePanel.add(localeLabel);
            localePanel.add(localesBox);

            topPanel.add(resourcePanel, getGridBagConstraints(1, 1));
            topPanel.add(localePanel, getGridBagConstraints(1, 2));
            topPanel.add(idModeCheckBox, getGridBagConstraints(2, 2));

            return topPanel;
        }

        private GridBagConstraints getGridBagConstraints(int row, int column) {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = column;
            gbc.gridy = row;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;

            gbc.anchor = column == 0 ? GridBagConstraints.WEST : GridBagConstraints.EAST;
            gbc.fill = column == 0 ? GridBagConstraints.BOTH : GridBagConstraints.HORIZONTAL;

            gbc.weightx = column == 0 ? 0.1 : 1.0;
            gbc.weighty = 1.0;
            return gbc;
        }

        @Override
        protected void doOKAction() {
            try {
                ResourceBundlesWithScope resourceBundle = getSelectedResourceBundle();

                String oldLocale = resourceBundle.lsfStrLiteralsLanguage;
                VirtualFile oldPropertiesFile = resourceBundle.propertiesFiles.get(oldLocale).getVirtualFile();
                Map<String, String> oldOrdinaryMap = LSFResourceBundleUtils.getOrdinaryMap(oldPropertiesFile.getPath());

                if (idModeCheckBox.isSelected()) {

                    Map<String, String> literalsMap = new HashMap<>();
                    for (Map.Entry<String, String> oldEntry : oldOrdinaryMap.entrySet()) {
                        literalsMap.put(oldEntry.getValue(), "'{" + oldEntry.getKey() + "}'");
                    }

                    changeLocale(literalsMap, resourceBundle.scope);

                } else {

                    String newLocale = localesBox.getItem();
                    if(oldLocale.equals(newLocale)) {
                        JOptionPane.showMessageDialog(getContentPane(), "Selected locale is equal to current locale", "Change Locale", JOptionPane.ERROR_MESSAGE);
                    } else {
                        Map<String, String> newOrdinaryMap = LSFResourceBundleUtils.getOrdinaryMap(resourceBundle.propertiesFiles.get(newLocale).getVirtualFile().getPath());

                        Map<String, String> literalsMap = new HashMap<>();
                        for (Map.Entry<String, String> oldEntry : oldOrdinaryMap.entrySet()) {
                            String key = oldEntry.getKey();
                            String oldValue = oldEntry.getValue();
                            String newValue = newOrdinaryMap.get(key);
                            if (newValue != null) {
                                if (literalsMap.containsKey(oldValue)) {
                                    literalsMap.put(oldValue, "'{" + key + "}'"); //put key if multiple values found
                                } else {
                                    literalsMap.put(oldValue, "'" + newValue + "'");
                                }
                            }
                        }

                        changeLocale(literalsMap, resourceBundle.scope);

                        for(PsiFile propertiesFile : LSFFileUtils.findFilesByPath(PsiManager.getInstance(project).findFile(oldPropertiesFile), "lsfusion.properties")) {
                            Path filePath = Paths.get(propertiesFile.getVirtualFile().getPath());
                            List<String> newLines = new ArrayList<>();
                            for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
                                if (line.matches("logics\\.lsfStrLiteralsLanguage(\\s)*=.*")) {
                                    newLines.add("logics.lsfStrLiteralsLanguage = " + newLocale);
                                } else {
                                    newLines.add(line);
                                }
                            }
                            Files.write(filePath, String.join("\n", newLines).getBytes(StandardCharsets.UTF_8));
                        }
                        LSFResourceBundleUtils.setLsfStrLiteralsLanguage(resourceBundle.scope, newLocale);
                        dispose();
                    }

                }

            } catch (Throwable t) {
                throw new RuntimeException("Change Locale failed", t);
            }
        }

        private DefaultComboBoxModel<String> getComboBoxModel(ResourceBundlesWithScope selectedResourceBundle) {
            String[] locales = selectedResourceBundle.propertiesFiles.keySet().toArray(new String[]{});
            Arrays.sort(locales, (s1, s2) -> s1.equals("default") ? 1 : s2.equals("default") ? -1 : s1.compareTo(s2));
            return new DefaultComboBoxModel<>(locales);
        }

        private ResourceBundlesWithScope getSelectedResourceBundle() {
            return resourceBundlesWithScopeMap.get(resourceBundlesBox.getItem());
        }
    }

    public void changeLocale(Map<String, String> literalsMap, GlobalSearchScope scope) {
        final Progressive run = indicator -> {
            indicator.setIndeterminate(false);
            ReprocessInlineProcessor inlineProcessor = new ReprocessInlineProcessor(indicator);
            ApplicationManager.getApplication().runReadAction(() -> {
                int i = 0;
                Collection<VirtualFile> files = FileTypeIndex.getFiles(LSFFileType.INSTANCE, scope);
                for(VirtualFile vfile : files) {
                    indicator.setText("Processing : " + vfile.getName());
                    PsiFile psiFile = PsiManager.getInstance(project).findFile(vfile);
                    Collection<LSFLocalizedStringValueLiteral> localizedStringLiterals = PsiTreeUtil.findChildrenOfType(psiFile, LSFLocalizedStringValueLiteral.class);

                    for (final LSFLocalizedStringValueLiteral localizedStringLiteral : localizedStringLiterals) {
                        Runnable inlineRun = () -> {
                            if(!localizedStringLiteral.needToBeLocalized() && !LSFReferenceAnnotator.isInMetaUsage(localizedStringLiteral)) {
                                String value = literalsMap.get(localizedStringLiteral.getValue());
                                if (value != null) {
                                    localizedStringLiteral.replace(createLocalizedStringLiteral(localizedStringLiteral.getProject(), value));
                                }
                            }
                        };
                        inlineProcessor.proceed(inlineRun);
                    }

                    indicator.setFraction(((double) i++) / files.size());
                    indicator.setText2("file " + i + "/" + files.size());
                }
            });

            inlineProcessor.flushPostponed();
        };

        ProgressManager.getInstance().run(new Task.Modal(project, "Changing locale", true) {
            public void run(final @NotNull ProgressIndicator indicator) {
                run.run(indicator);
            }
        });
    }

    private static class ReprocessInlineProcessor {
        private final ProgressIndicator indicator;

        public ReprocessInlineProcessor(ProgressIndicator indicator) {
            this.indicator = indicator;
        }

        private final List<Runnable> postponed = new ArrayList<>();

        public void proceed(Runnable inline) {
            postponed.add(inline);
        }

        private void runEDTWriteUndo(Runnable run) {
            Runnable flush = () -> CommandProcessor.getInstance().runUndoTransparentAction(() -> ApplicationManager.getApplication().runWriteAction(run));
            if(ApplicationManager.getApplication().isDispatchThread())
                flush.run();
            else
                ApplicationManager.getApplication().invokeAndWait(flush);
        }

        public void flushPostponed() {
            if (postponed.isEmpty()) return;

            int blockSize = 500;
            int blocks = (postponed.size() - 1) / blockSize + 1;
            for (int i = 0; i < blocks; i++) {
                indicator.setText2("changing locale : " + i * blockSize + "/" + postponed.size());
                final int fi = i;
                runEDTWriteUndo(() -> {
                    for (int j = fi * blockSize; j < BaseUtils.min((fi + 1) * blockSize, postponed.size()); j++) {
                        postponed.get(j).run();
                    }
                    FileDocumentManager.getInstance().saveAllDocuments();
                });
            }
            postponed.clear();
        }
    }

    private static class ResourceBundlesWithScope {
        GlobalSearchScope scope;
        Map<String, PropertiesFile> propertiesFiles;
        String lsfStrLiteralsLanguage;

        public ResourceBundlesWithScope(GlobalSearchScope scope, Map<String, PropertiesFile> propertiesFiles, String lsfStrLiteralsLanguage) {
            this.scope = scope;
            this.propertiesFiles = propertiesFiles;
            this.lsfStrLiteralsLanguage = lsfStrLiteralsLanguage;
        }
    }
}