package com.lsfusion.module;

import com.intellij.framework.library.LibraryVersionProperties;
import com.intellij.openapi.roots.libraries.JarVersionDetectionUtil;
import com.intellij.openapi.roots.libraries.LibraryKind;
import com.intellij.openapi.roots.libraries.LibraryPresentationProvider;
import com.intellij.openapi.roots.libraries.LibraryUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.lsfusion.LSFIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

import static com.lsfusion.module.LSFusionModuleBuilder.BOOTSTRAP_CLASS_NAME;

public class LSFusionLibraryPresentationProvider extends LibraryPresentationProvider<LibraryVersionProperties> {

    public static final LibraryKind LSFUSION_LIBRARY_KIND = LibraryKind.create("lsFusion-server");

    public LSFusionLibraryPresentationProvider() {
        super(LSFUSION_LIBRARY_KIND);
    }

    @Override
    public String getDescription(@NotNull LibraryVersionProperties properties) {
        final String version = properties.getVersionString();
        return "lsFusion library" + (version == null ? "" : " of version " + version);
    }

    @Override
    public LibraryVersionProperties detect(@NotNull List<VirtualFile> classesRoots) {
        if (LibraryUtil.isClassAvailableInLibrary(classesRoots, BOOTSTRAP_CLASS_NAME)) {
            return new LibraryVersionProperties(JarVersionDetectionUtil.detectJarVersion(BOOTSTRAP_CLASS_NAME, classesRoots));
        }
        return null;
    }

    @NotNull
    public Icon getIcon() {
        return LSFIcons.LIBRARY;
    }
}
