package com.lsfusion.module;

import com.intellij.facet.impl.ui.libraries.LibraryCompositionSettings;
import com.intellij.framework.library.FrameworkLibraryVersion;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * based on com.intellij.facet.impl.ui.libraries.LibraryOptionsPanel
 */
public class LibraryOptionsPanel {
    private JComboBox myExistingLibraryComboBox;
    private JButton myCreateButton;
    private JButton myDownloadButton;
    private JPanel mySimplePanel;

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
        }, versionFilter, new ArrayList<FrameworkLibraryVersion>());

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
        myDownloadButton.addActionListener(e -> doDownload());
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

    private void doDownload() {
        try {
            String url = "https://lsfusion.ru/download/fsl-server-latest.jar";
            VirtualFile baseDirectory = getBaseDirectory();
            if (baseDirectory != null) {
                File file = new File(baseDirectory.getPath() + "/fsl-server-latest.jar");
                FileUtils.copyURLToFile(new URL(url), file);

                final NewLibraryConfiguration libraryConfiguration = new NewLibraryConfiguration(file.getAbsolutePath()) {
                    @Override
                    public void addRoots(@NotNull LibraryEditor libraryEditor) {
                        libraryEditor.addRoot(VfsUtil.getUrlForLibraryRoot(file), OrderRootType.CLASSES);
                    }
                };
                final NewLibraryEditor libraryEditor = new NewLibraryEditor(libraryConfiguration.getLibraryType(), libraryConfiguration.getProperties());
                libraryEditor.setName(myLibrariesContainer.suggestUniqueLibraryName(libraryConfiguration.getDefaultLibraryName()));
                libraryConfiguration.addRoots(libraryEditor);
                if (myLibraryComboBoxModel.get(0) == null) {
                    myLibraryComboBoxModel.remove(0);
                }
                myLibraryComboBoxModel.add(libraryEditor);
                myLibraryComboBoxModel.setSelectedItem(libraryEditor);
            }
        } catch (Exception ignored) {
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
}