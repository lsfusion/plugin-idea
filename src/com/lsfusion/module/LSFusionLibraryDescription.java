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
import java.io.FilenameFilter;
import java.util.Collections;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LSFusionLibraryDescription extends CustomLibraryDescription {

    private static final String LSFUSION_SERVER_LIBRARY_PATTERN = "lsfusion-server-(.*)\\.jar";


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
        descriptor.setTitle("lsFusion Platform");
        descriptor.setDescription("Choose a directory containing lsFusion Platform distribution");

        final VirtualFile dir = FileChooser.chooseFile(descriptor, parentComponent, null, null);
        if (dir == null) {
            return null;
        }

        final String[] jarNameAndVersion = getLSFusionServerFileAndVersion(dir);
        if (jarNameAndVersion == null) {
            return null;
        }

        final String jarName = jarNameAndVersion[0];
        final String version = jarNameAndVersion[1];

        return new NewLibraryConfiguration("lsFusion-server" + "-" + version) {
            @Override
            public void addRoots(@NotNull LibraryEditor libraryEditor) {
                libraryEditor.addRoot(VfsUtil.getUrlForLibraryRoot(new File(jarName)), OrderRootType.CLASSES);
            }
        };
    }

    public static boolean isLSFusionServerDirectory(VirtualFile file) {
        return getLSFusionServerFileAndVersion(file) != null;
    }

    public static String[] getLSFusionServerFileAndVersion(VirtualFile directory) {
        if (directory != null && directory.isDirectory()) {
            final String path = directory.getPath();
            final Pattern pattern = Pattern.compile(LSFUSION_SERVER_LIBRARY_PATTERN);
            File[] serverFiles = new File(path).listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return pattern.matcher(name).matches();
                }
            });

            if (serverFiles != null && serverFiles.length > 0) {
                File jarFile = serverFiles[0];
                String jarName = jarFile.getName();
                Matcher matcher = pattern.matcher(jarName);
                if (matcher.matches() && matcher.groupCount() == 1) {
                    return new String[]{ jarFile.getPath(), matcher.group(1) };
                }
            }
        }
        return null;
    }
}
