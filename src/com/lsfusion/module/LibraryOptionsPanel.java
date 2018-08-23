package com.lsfusion.module;

import com.intellij.facet.impl.ui.libraries.LibraryCompositionSettings;
import com.intellij.framework.library.FrameworkLibraryVersionFilter;
import com.intellij.ide.util.frameworkSupport.OldCustomLibraryDescription;
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
import com.intellij.openapi.util.NotNullComputable;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.SortedComboBoxModel;
import com.intellij.util.PlatformIcons;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * based on com.intellij.facet.impl.ui.libraries.LibraryOptionsPanel
 */
public class LibraryOptionsPanel {
    private JComboBox myExistingLibraryComboBox;
    private JButton myCreateButton;
    private JButton myDownloadButton;
    private JPopupMenu myPopupMenu;
    private JButton myPopupButton;
    private JPanel mySimplePanel;

    private String downloadUrl = "https://download.lsfusion.org";;
    private String subDirPattern = "(\\d+(\\.)?)*/";
    private String jarPattern = "lsfusion-server-(\\d+(\\.)?)*\\.jar";

    private LibraryCompositionSettings mySettings;
    private final LibrariesContainer myLibrariesContainer;
    private SortedComboBoxModel<LibraryEditor> myLibraryComboBoxModel;

    public LibraryOptionsPanel(@NotNull final CustomLibraryDescription libraryDescription,
                               @NotNull final String baseDirectoryPath,
                               @NotNull final FrameworkLibraryVersionFilter versionFilter,
                               @NotNull final LibrariesContainer librariesContainer) {
        myLibrariesContainer = librariesContainer;

        mySettings = new LibraryCompositionSettings(libraryDescription, new NotNullComputable<String>() {
            @NotNull
            @Override
            public String compute() {
                return baseDirectoryPath;
            }
        }, versionFilter, new ArrayList<>());

        showSettingsPanel();
    }

    private void showSettingsPanel() {
        List<Library> libraries = calculateSuitableLibraries();

        myLibraryComboBoxModel = new SortedComboBoxModel<>(new Comparator<LibraryEditor>() {
            @Override
            public int compare(LibraryEditor o1, LibraryEditor o2) {
                final String name1 = o1.getName();
                final String name2 = o2.getName();
                return -StringUtil.notNullize(name1).compareToIgnoreCase(StringUtil.notNullize(name2));
            }
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
            protected void customizeCellRenderer(JList list, Object value, int index, boolean selected, boolean hasFocus) {
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
        myDownloadButton.addActionListener(e -> doDownload(null));
        myPopupMenu = new JPopupMenu();
        myPopupButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                showPopupMenu(e);
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

    private void doDownload(String lsfusionServerJar) {
        try {
            File file = lsfusionServerJar != null ? downloadFile(lsfusionServerJar) : getLatestLsfusionServerJar();
            if (file != null) {
                final NewLibraryConfiguration libraryConfiguration = new NewLibraryConfiguration(file.getAbsolutePath()) {
                    @Override
                    public void addRoots(@NotNull LibraryEditor libraryEditor) {
                        libraryEditor.addRoot(VfsUtil.getUrlForLibraryRoot(file), OrderRootType.CLASSES);
                    }
                };

                String defaultLibraryName = libraryConfiguration.getDefaultLibraryName();
                if (myLibraryComboBoxModel.get(0) == null) {
                    myLibraryComboBoxModel.remove(0);
                }
                Iterator<LibraryEditor> i = myLibraryComboBoxModel.iterator();
                while (i.hasNext()) {
                    LibraryEditor library = i.next();
                    if(library.getName().equals(defaultLibraryName)) {
                        i.remove();
                    }
                }

                final NewLibraryEditor libraryEditor = new NewLibraryEditor(libraryConfiguration.getLibraryType(), libraryConfiguration.getProperties());
                libraryEditor.setName(libraryConfiguration.getDefaultLibraryName());
                libraryConfiguration.addRoots(libraryEditor);
                myLibraryComboBoxModel.add(libraryEditor);
                myLibraryComboBoxModel.setSelectedItem(libraryEditor);
            }
        } catch (Exception ignored) {
        }
    }

    private void showPopupMenu(MouseEvent mouseEvent) {
        try {
            myPopupMenu.removeAll();
            List<String> lsfusionServerJarList = getLsfusionServerJarList();
            for(String lsfusionServerJar : lsfusionServerJarList) {
                JMenuItem menuItem = new JMenuItem(lsfusionServerJar);
                menuItem.addActionListener(e -> doDownload(lsfusionServerJar));
                myPopupMenu.add(menuItem);
            }
            myPopupMenu.show(mouseEvent.getComponent(), 0, mouseEvent.getComponent().getHeight());
        } catch (IOException ignored) {
        }
    }

    @Nullable
    private VirtualFile getBaseDirectory() {
        String path = mySettings.getBaseDirectoryPath();
        VirtualFile dir = LocalFileSystem.getInstance().findFileByPath(path);
        if (dir == null) {
            path = path.substring(0, path.lastIndexOf('/'));
            dir = LocalFileSystem.getInstance().findFileByPath(path);
        }
        return dir;
    }

    private File getLatestLsfusionServerJar() throws IOException, ExecutionException, InterruptedException {
        String latestLsfusionServerJar = null;
        List<String> directoryList = parseURL(downloadUrl, subDirPattern);
        for (int i = directoryList.size() - 1; i >= 0; i--) {
            String subDirectory = downloadUrl + "/" + directoryList.get(i);
            List<String> files = parseURL(subDirectory, jarPattern);
            if (!files.isEmpty()) {
                latestLsfusionServerJar = subDirectory + files.get(files.size() - 1);
                break;
            }
        }
        return downloadFile(latestLsfusionServerJar);
    }

    private List<String> getLsfusionServerJarList() throws IOException {
        List<String> lsfusionServerJarList = new ArrayList<>();
        List<String> directoryList = parseURL(downloadUrl, subDirPattern);
        for (int i = directoryList.size() - 1; i >= 0; i--) {
            String subDirectory = downloadUrl + "/" + directoryList.get(i);
            List<String> files = parseURL(subDirectory, jarPattern);
            if (!files.isEmpty()) {
                lsfusionServerJarList.add(subDirectory + files.get(files.size() - 1));
            }
        }
        return lsfusionServerJarList;
    }

    private List<String> parseURL(String url, String pattern) throws IOException {
        List<String> result = new ArrayList<>();
        Connection connection = Jsoup.connect(url);
        connection.timeout(10000);
        Document doc = connection.get();
        for (Element item : doc.getElementsByTag("a")) {
            String href = item.attr("href");
            if (href != null && href.matches(pattern)) {
                result.add(href);
            }
        }
        return result;
    }

    private File downloadFile(String url) throws ExecutionException, InterruptedException {

        final JDialog dialog = new ProgressDialog(url);

        SwingWorker<File, Void> mySwingWorker = new SwingWorker<File, Void>() {
            @Override
            protected File doInBackground() throws Exception {
                VirtualFile baseDirectory = getBaseDirectory();
                if (baseDirectory != null) {
                    String userAgent = "Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1667.0 Safari/537.36";
                    File file = new File(baseDirectory.getPath() + "/" + FilenameUtils.getName(url));
                    if (url != null) {
                        HttpGet httpGet = new HttpGet(url);
                        httpGet.addHeader("User-Agent", userAgent);
                        HttpEntity fileEntity = HttpClients.createDefault().execute(httpGet).getEntity();
                        if (fileEntity != null) {
                            FileUtils.copyInputStreamToFile(fileEntity.getContent(), file);
                            return file;
                        }
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                dialog.dispose();
            }
        };

        mySwingWorker.execute();

        dialog.setVisible(true);

        return mySwingWorker.get();
    }

    @Nullable
    public LibraryCompositionSettings apply() {
        if (mySettings == null) {
            return null;
        }

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
        return mySettings;
    }

    public JComponent getSimplePanel() {
        return mySimplePanel;
    }

    private class ProgressDialog extends JDialog {

        ProgressDialog(String url) {
            super((Frame) null, "Please wait", true);
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);
            setAlwaysOnTop(true);

            JPanel messagePanel = new JPanel();
            messagePanel.add(new JLabel("<html><center>Downloading file " + url + "...</center></html>"));

            JProgressBar progressBar = new JProgressBar();
            progressBar.setIndeterminate(true);
            JPanel progressPanel = new JPanel();
            progressPanel.add(progressBar);

            Container contentPane = getContentPane();
            contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
            contentPane.add(messagePanel);
            contentPane.add(progressPanel);

            pack();
            setMinimumSize(new Dimension(300, 100));
            setResizable(false);

            setFocusableWindowState(false);
        }
    }
}