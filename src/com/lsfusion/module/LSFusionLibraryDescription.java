package com.lsfusion.module;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.LibraryKind;
import com.intellij.openapi.roots.libraries.NewLibraryConfiguration;
import com.intellij.openapi.roots.ui.configuration.libraries.CustomLibraryDescription;
import com.intellij.openapi.roots.ui.configuration.libraryEditor.LibraryEditor;
import com.intellij.openapi.roots.ui.configuration.projectRoot.LibrariesContainer;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;
import java.util.Collections;
import java.util.Set;
import java.util.regex.Pattern;

public class LSFusionLibraryDescription extends CustomLibraryDescription {

    private static final String LSFUSION_SERVER_LIBRARY_PATTERN = ".*lsfusion.*\\.jar";


    @NotNull
    @Override
    public LibrariesContainer.LibraryLevel getDefaultLevel() {
        return LibrariesContainer.LibraryLevel.GLOBAL;
    }

    @NotNull
    @Override
    public Set<? extends LibraryKind> getSuitableLibraryKinds() {
        return Collections.singleton(LSFusionLibraryPresentationProvider.LSFUSION_LIBRARY_KIND);
    }

    @Override
    public NewLibraryConfiguration createNewLibrary(@NotNull JComponent parentComponent, VirtualFile contextDirectory) {
        final FileChooserDescriptor descriptor = new FileChooserDescriptor(false, true, false, false, false, false) {
            @Override
            public boolean isFileSelectable(VirtualFile file) {
                return super.isFileSelectable(file) && isLSFusionServerDirectory(file);
            }
        };
        descriptor.setTitle("lsFusion");
        descriptor.setDescription("Choose a directory containing lsFusion distribution");

        final VirtualFile dir = FileChooser.chooseFile(descriptor, parentComponent, null, null);
        if (dir != null) {
            final String jarName = getLSFusionServerFileName(dir);
            if (jarName != null) {
                return new NewLibraryConfiguration(jarName) {
                    @Override
                    public void addRoots(@NotNull LibraryEditor libraryEditor) {
                        libraryEditor.addRoot(VfsUtil.getUrlForLibraryRoot(new File(jarName)), OrderRootType.CLASSES);
                        
                        File sourcesJar = new File(jarName.substring(0, jarName.length() - 4) + "-sources.jar");
                        if (sourcesJar.exists()) {
                            libraryEditor.addRoot(VfsUtil.getUrlForLibraryRoot(sourcesJar), OrderRootType.SOURCES);
                        }
                    }
                };
            }
        }
        return null;
    }

    public static boolean isLSFusionServerDirectory(VirtualFile file) {
        return getLSFusionServerFileName(file) != null;
    }

    public static String getLSFusionServerFileName(VirtualFile directory) {
        if (directory != null && directory.isDirectory()) {
            final String path = directory.getPath();
            final Pattern pattern = Pattern.compile(LSFUSION_SERVER_LIBRARY_PATTERN);
            File[] serverFiles = new File(path).listFiles((dir, name) -> pattern.matcher(name).matches() && !name.contains("-sources"));

            if (serverFiles != null && serverFiles.length > 0) {
                assert pattern.matcher(serverFiles[0].getName()).matches();
                return serverFiles[0].getPath();
            }
        }
        return null;
    }
}
