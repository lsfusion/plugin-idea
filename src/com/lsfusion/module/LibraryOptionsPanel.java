package com.lsfusion.module;

import com.intellij.facet.impl.ui.libraries.LibraryCompositionSettings;
import com.intellij.framework.library.FrameworkLibraryVersionFilter;
import com.intellij.ide.util.frameworkSupport.OldCustomLibraryDescription;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Progressive;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.impl.libraries.LibraryEx;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.NewLibraryConfiguration;
import com.intellij.openapi.roots.ui.OrderEntryAppearanceService;
import com.intellij.openapi.roots.ui.configuration.libraries.CustomLibraryDescription;
import com.intellij.openapi.roots.ui.configuration.libraries.LibraryPresentationManager;
import com.intellij.openapi.roots.ui.configuration.libraryEditor.ExistingLibraryEditor;
import com.intellij.openapi.roots.ui.configuration.libraryEditor.LibraryEditor;
import com.intellij.openapi.roots.ui.configuration.libraryEditor.NewLibraryEditor;
import com.intellij.openapi.roots.ui.configuration.projectRoot.LibrariesContainer;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.SortedComboBoxModel;
import com.intellij.ui.components.JBOptionButton;
import com.intellij.util.PlatformIcons;
import com.intellij.util.text.VersionComparatorUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import javax.swing.*;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;

/**
 * based on com.intellij.facet.impl.ui.libraries.LibraryOptionsPanel
 */
public class LibraryOptionsPanel {
    // calling URL without trailing slash follows redirect to the page with CDN id and root folder name 
    // which causes 404 error as this page is not reachable from the outside
    private final String DOWNLOAD_URL = "https://download.lsfusion.org/java/";
    private final String SERVER_PATTERN = "lsfusion-server-(\\d+(\\.)?)*(-beta\\d+|-SNAPSHOT)?\\.jar";

    private final String SERVER_JAR_KEY = "serverJar";
    private final String SOURCES_JAR_KEY = "sourcesJar";
    
    private final JComboBox myExistingLibraryComboBox = new JComboBox();
    private final JButton myCreateButton = new JButton("Create...");
    private final JBOptionButton myDownloadButton = new JBOptionButton(new AbstractAction("Download") {
        @Override
        public void actionPerformed(ActionEvent e) {
            downloadSelected(myLatestRelease);
        }
    }, null);
    private final JPanel mySimplePanel = new JPanel(new GridBagLayout());
    // the release the Download button offers, null until the version list is loaded
    private Map<String, String> myLatestRelease;

    private LibraryCompositionSettings mySettings;
    private final LibrariesContainer myLibrariesContainer;
    private SortedComboBoxModel<LibraryEditor> myLibraryComboBoxModel;

    public LibraryOptionsPanel(@NotNull final CustomLibraryDescription libraryDescription,
                               @NotNull final String baseDirectoryPath,
                               @NotNull final FrameworkLibraryVersionFilter versionFilter,
                               @NotNull final LibrariesContainer librariesContainer) {
        myLibrariesContainer = librariesContainer;

        mySettings = new LibraryCompositionSettings(libraryDescription, () -> baseDirectoryPath, versionFilter, new ArrayList<>());

        showSettingsPanel();
    }

    private void showSettingsPanel() {
        myCreateButton.setMnemonic('C');
        myDownloadButton.setMnemonic('D');
        // a split button inside the page opens its drop-down with Down while it has focus; Alt+Shift+Enter, named by
        // JBOptionButton.getDefaultTooltip(), works only for the option buttons of DialogWrapper's own button bar
        myDownloadButton.setOptionTooltipText("Download another version (" + KeymapUtil.getKeystrokeText(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0)) + ")");
        // the options are versions, not distinct actions, so they are not separated from each other
        myDownloadButton.setAddSeparator(false);

        GridBagConstraints comboConstraints = new GridBagConstraints();
        comboConstraints.fill = GridBagConstraints.HORIZONTAL;
        comboConstraints.weightx = 1;
        mySimplePanel.add(myExistingLibraryComboBox, comboConstraints);
        mySimplePanel.add(myCreateButton);
        mySimplePanel.add(myDownloadButton);

        List<Library> libraries = calculateSuitableLibraries();

        myLibraryComboBoxModel = new SortedComboBoxModel<>((o1, o2) -> {
            final String name1 = o1.getName();
            final String name2 = o2.getName();
            return -StringUtil.notNullize(name1).compareToIgnoreCase(StringUtil.notNullize(name2));
        });

        for (Library library : libraries) {
            ExistingLibraryEditor libraryEditor = myLibrariesContainer.getLibraryEditor(library);
            if (libraryEditor == null) {
                libraryEditor = mySettings.getOrCreateEditor(library);
            }
            myLibraryComboBoxModel.add(libraryEditor);
        }
        myExistingLibraryComboBox.setModel(myLibraryComboBoxModel);
        if (libraries.isEmpty()) {
            myLibraryComboBoxModel.add(null);
        }
        myExistingLibraryComboBox.setSelectedIndex(0);
        myExistingLibraryComboBox.setRenderer(new ColoredListCellRenderer() {
            @Override
            protected void customizeCellRenderer(@NotNull JList list, Object value, int index, boolean selected, boolean hasFocus) {
                if (value == null) {
                    append("[No library selected]");
                } else if (value instanceof ExistingLibraryEditor) {
                    final Library library = ((ExistingLibraryEditor) value).getLibrary();
                    final boolean invalid = !((LibraryEx) library).getInvalidRootUrls(OrderRootType.CLASSES).isEmpty();
                    OrderEntryAppearanceService.getInstance().forLibrary(getProject(), library, invalid).customize(this);
                } else if (value instanceof NewLibraryEditor) {
                    setIcon(PlatformIcons.LIBRARY_ICON);
                    final String name = ((NewLibraryEditor) value).getName();
                    append(name != null ? name : "<unnamed>");
                }
            }
        });

        myCreateButton.addActionListener(e -> doCreate());

        // JBOptionButton shows its drop-down arrow only when it already has options, so the versions are read
        // from the download page in advance rather than on a click
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            try {
                List<Map<String, String>> serverJars = getLsfusionServerJars();
                ApplicationManager.getApplication().invokeLater(() -> {
                    myLatestRelease = findLatestRelease(serverJars);
                    List<Action> options = new ArrayList<>();
                    for (Map<String, String> serverJar : serverJars) {
                        if (serverJar == myLatestRelease) {
                            String version = getVersion(serverJar);
                            myDownloadButton.getAction().putValue(Action.NAME, "Download " + version);
                            // without a tooltip of its own the main part would show the drop-down arrow's tooltip
                            myDownloadButton.getAction().putValue(Action.SHORT_DESCRIPTION, "Download lsFusion server " + version
                                    + (serverJar.containsKey(SOURCES_JAR_KEY) ? " with sources" : ""));
                        } else {
                            options.add(new AbstractAction(getVersion(serverJar)) {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    downloadSelected(serverJar);
                                }
                            });
                        }
                    }
                    myDownloadButton.setOptions(options.toArray(new Action[0]));
                }, ModalityState.any());
            } catch (IOException ignored) {
            }
        });
    }

    private List<Library> calculateSuitableLibraries() {
        final CustomLibraryDescription description = mySettings.getLibraryDescription();
        List<Library> suitableLibraries = new ArrayList<>();
        for (Library library : myLibrariesContainer.getAllLibraries()) {
            if (description instanceof OldCustomLibraryDescription &&
                ((OldCustomLibraryDescription) description).isSuitableLibrary(library, myLibrariesContainer)
                || LibraryPresentationManager.getInstance().isLibraryOfKind(library, myLibrariesContainer, description.getSuitableLibraryKinds())) {
                suitableLibraries.add(library);
            }
        }
        return suitableLibraries;
    }

    private Project getProject() {
        Project project = myLibrariesContainer.getProject();
        if (project == null) {
            project = ProjectManager.getInstance().getDefaultProject();
        }
        return project;
    }

    private void doCreate() {
        final NewLibraryConfiguration libraryConfiguration = mySettings.getLibraryDescription().createNewLibrary(myCreateButton, getBaseDirectory());
        if (libraryConfiguration != null) {
            final NewLibraryEditor libraryEditor = new NewLibraryEditor(libraryConfiguration.getLibraryType(), libraryConfiguration.getProperties());
            libraryEditor.setName(myLibrariesContainer.suggestUniqueLibraryName(libraryConfiguration.getDefaultLibraryName()));
            libraryConfiguration.addRoots(libraryEditor);
            if (myLibraryComboBoxModel.get(0) == null) {
                myLibraryComboBoxModel.remove(0);
            }
            myLibraryComboBoxModel.add(libraryEditor);
            myLibraryComboBoxModel.setSelectedItem(libraryEditor);
        }
    }

    private void downloadSelected(Map<String, String> lsfusionServerUrls) { // server with sources
        try {
            Map<String, File> files = lsfusionServerUrls != null ? downloadFiles(lsfusionServerUrls) : getLatestLsfusionServerJar();
            if (files != null) {
                File serverJar = files.get(SERVER_JAR_KEY);
                if (serverJar != null) {
                    final NewLibraryConfiguration libraryConfiguration = new NewLibraryConfiguration(serverJar.getAbsolutePath()) {
                        @Override
                        public void addRoots(@NotNull LibraryEditor libraryEditor) {
                            libraryEditor.addRoot(VfsUtil.getUrlForLibraryRoot(serverJar), OrderRootType.CLASSES);

                            File sourcesJar = files.get(SOURCES_JAR_KEY);
                            if (sourcesJar != null && sourcesJar.exists()) {
                                libraryEditor.addRoot(VfsUtil.getUrlForLibraryRoot(sourcesJar), OrderRootType.SOURCES);
                            }
                        }
                    };

                    String defaultLibraryName = libraryConfiguration.getDefaultLibraryName();
                    if (myLibraryComboBoxModel.get(0) == null) {
                        myLibraryComboBoxModel.remove(0);
                    }
                    Iterator<LibraryEditor> i = myLibraryComboBoxModel.iterator();
                    while (i.hasNext()) {
                        LibraryEditor library = i.next();
                        if (library.getName().equals(defaultLibraryName)) {
                            i.remove();
                        }
                    }

                    final NewLibraryEditor libraryEditor = new NewLibraryEditor(libraryConfiguration.getLibraryType(), libraryConfiguration.getProperties());
                    libraryEditor.setName(libraryConfiguration.getDefaultLibraryName());
                    libraryConfiguration.addRoots(libraryEditor);
                    myLibraryComboBoxModel.add(libraryEditor);
                    myLibraryComboBoxModel.setSelectedItem(libraryEditor);
                }
            }
        } catch (Exception e) {
            Messages.showErrorDialog(getProject(), getStackTrace(e), "Saving lsFusion server failed");
        }
    }

    @Nullable
    private VirtualFile getBaseDirectory() {
        String path = mySettings.getBaseDirectoryPath();
        LocalFileSystem fileSystem = LocalFileSystem.getInstance();
        VirtualFile dir = fileSystem.findFileByPath(path);
        if (dir == null) {
            path = path.substring(0, path.lastIndexOf('/'));
            dir = fileSystem.findFileByPath(path);
        }
        return dir;
    }

    private Map<String, File> getLatestLsfusionServerJar() throws IOException {
        Map<String, String> latestRelease = findLatestRelease(getLsfusionServerJars());
        return latestRelease != null ? downloadFiles(latestRelease) : null;
    }

    // newest first; for the same version number VersionComparatorUtil puts the release before its betas
    // and the betas before the SNAPSHOT
    private List<Map<String, String>> getLsfusionServerJars() throws IOException {
        List<String> hrefs = new ArrayList<>();
        for (Element item : Jsoup.connect(DOWNLOAD_URL).timeout(10000).get().getElementsByTag("a")) {
            hrefs.add(item.attr("href"));
        }

        List<Map<String, String>> serverJarUrlsList = new ArrayList<>();
        for (String href : hrefs) {
            String releaseHref = href.replaceFirst("-beta\\d+", "");
            // betas are offered only until their version is released
            if (href.matches(SERVER_PATTERN) && (releaseHref.equals(href) || !hrefs.contains(releaseHref))) {
                Map<String, String> fileUrls = new HashMap<>();
                fileUrls.put(SERVER_JAR_KEY, DOWNLOAD_URL + href);

                String sourcesHref = href.replace(".jar", "-sources.jar");
                if (hrefs.contains(sourcesHref)) {
                    fileUrls.put(SOURCES_JAR_KEY, DOWNLOAD_URL + sourcesHref);
                }
                serverJarUrlsList.add(fileUrls);
            }
        }
        serverJarUrlsList.sort((jar1, jar2) -> VersionComparatorUtil.compare(getVersion(jar2), getVersion(jar1)));
        return serverJarUrlsList;
    }

    // a release is a version without a -beta or -SNAPSHOT suffix
    @Nullable
    private Map<String, String> findLatestRelease(List<Map<String, String>> serverJars) {
        return serverJars.stream().filter(serverJar -> !getVersion(serverJar).contains("-")).findFirst().orElse(null);
    }

    private String getVersion(Map<String, String> serverJar) {
        String url = serverJar.get(SERVER_JAR_KEY);
        return url.substring((DOWNLOAD_URL + "lsfusion-server-").length(), url.length() - ".jar".length());
    }
    
    private Map<String, File> downloadFiles(Map<String, String> serverJarUrls) {
        Map<String, File> resultFiles = new HashMap<>();

        final FileChooserDescriptor dirChooser = FileChooserDescriptorFactory.createSingleFolderDescriptor()
                .withTitle("Select Folder to Download lsFusion " + getVersion(serverJarUrls));
        FileChooser.chooseFiles(dirChooser, getProject(), null, paths -> {
            if (!paths.isEmpty()) {
                String targetPath = paths.get(0).getPath();
                resultFiles.putAll(downloadToDir(serverJarUrls, targetPath));
            }
        });

        return resultFiles;
    }
    
    private Map<String, File> downloadToDir(Map<String, String> urls, String targetPath) {
        final Map<String, File> resultFiles = new HashMap<>();
        final Progressive run = indicator -> {
            for (Map.Entry<String, String> urlEntry : urls.entrySet()) {
                String url = urlEntry.getValue();
                
                if (url != null) {
                    indicator.setText(url);
                    try {
                        Pattern p = Pattern.compile(".*/lsfusion-server-(\\d+)[\\d.]*(-beta\\d+|-SNAPSHOT)?(-sources)?\\.jar");
                        Matcher m = p.matcher(url);
                        if(m.matches()) {
                            String fileName = "lsfusion-" + m.group(1) + (m.group(2) != null ? m.group(2) : "") + (m.group(3) != null ? m.group(3) : "") + ".jar";
                            File file = new File(targetPath + "/" + fileName);
                            HttpGet httpGet = new HttpGet(url);
                            httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1667.0 Safari/537.36");
                            HttpEntity fileEntity = HttpClients.createDefault().execute(httpGet).getEntity();
                            if (fileEntity != null) {
                                Files.copy(fileEntity.getContent(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                resultFiles.put(urlEntry.getKey(), file);
                            }
                        }
                    } catch (Exception e) {
                        ApplicationManager.getApplication().invokeAndWait(() -> Messages.showErrorDialog(getProject(), getStackTrace(e), "Download lsFusion server failed"));
                    }
                }
            }
        };
        
        ProgressManager.getInstance().run(new Task.Modal(getProject(), "Downloading file", false) {
            public void run(final @NotNull ProgressIndicator indicator) {
                run.run(indicator);
            }
        });
        
        return resultFiles;
    }

    @Nullable
    public LibraryCompositionSettings apply() {
        if (mySettings != null) {
            final Object item = myExistingLibraryComboBox.getSelectedItem();
            if (item instanceof ExistingLibraryEditor) {
                mySettings.setSelectedExistingLibrary(((ExistingLibraryEditor) item).getLibrary());
            } else {
                mySettings.setSelectedExistingLibrary(null);
            }

            if (item instanceof NewLibraryEditor) {
                mySettings.setNewLibraryEditor((NewLibraryEditor) item);
            } else {
                mySettings.setNewLibraryEditor(null);
            }
        }
        return mySettings;
    }

    public JComponent getSimplePanel() {
        return mySimplePanel;
    }
}