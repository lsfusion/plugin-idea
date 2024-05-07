package com.lsfusion.util;

import com.intellij.lang.properties.IProperty;
import com.intellij.lang.properties.psi.PropertiesFile;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderEnumerator;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.lsfusion.lang.psi.LSFGlobalResolver;
import com.lsfusion.lang.psi.Result;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static com.lsfusion.util.BaseUtils.isRedundantString;

public class LSFFileUtils {
    public static boolean hasFilesWithShortNameInProject(PsiElement scopeElement, final String fileName) {
        Project project = scopeElement.getProject();
        GlobalSearchScope scope = ProjectScope.getAllScope(project);
        return hasFilesWithShortName(fileName, project, scope);
    }

    public static boolean hasFilesWithShortNameInModule(PsiElement scopeElement, final String fileName) {
        Module module = ModuleUtil.findModuleForPsiElement(scopeElement);
        return hasFilesWithShortNameInModule(module, fileName);
    }

    public static boolean hasFilesWithShortNameInModule(Module module, final String fileName) {
        if (module != null) {
            GlobalSearchScope scope = GlobalSearchScope.moduleWithDependentsScope(module);
            return hasFilesWithShortName(fileName, module.getProject(), scope);
            
        }
        return false;
    }

    private static boolean hasFilesWithShortName(String fileName, Project project, GlobalSearchScope scope) {
        final Result<Boolean> hasFiles = new Result<>(false);
        FilenameIndex.processFilesByName(
                fileName, false, file -> {
                    if (!file.isDirectory() && file instanceof PsiFile) {
                        hasFiles.setResult(true);
                        return false;
                    }
                    return true;
                },
                scope,
                project,
                null
        );
        return hasFiles.getResult();
    }

    public static void findFilesWithShortName(String fileName, final List<PsiFile> files, Project project, GlobalSearchScope scope) {
        FilenameIndex.processFilesByName(
                fileName, false, file -> {
                    if (!file.isDirectory() && file instanceof PsiFile) {
                        files.add((PsiFile) file);
                    }
                    return true;
                },
                scope,
                project,
                null
        );
    }

    public static List<PsiFile> findFilesByPath(PsiElement scopeElement, final String path) {
        Module module = ModuleUtil.findModuleForPsiElement(scopeElement);
        return findFilesByPath(module, path);
    }

    public static List<PsiFile> findFilesByPath(Module module, final String path) {
        final List<PsiFile> result = new ArrayList<>();

        if (module != null) {
            final PsiManager psiManager = PsiManager.getInstance(module.getProject());

            final OrderEnumerator orderEnumerator = ModuleRootManager.getInstance(module).orderEntries();

            proceedModuleRoots(module, path, result, psiManager);
            orderEnumerator.forEachModule(module1 -> {
                proceedModuleRoots(module1, path, result, psiManager);
                return true;
            });

            if (result.isEmpty()) {
                //almost orderEnumerator.getAllLibrariesAndSdkClassesRoots(), but without sdk
                VirtualFile[] classRoots = orderEnumerator.withoutModuleSourceEntries().recursively().withoutSdk().exportedOnly().classes().usingCache().getRoots();
                for (VirtualFile classRoot : classRoots) {
                    VirtualFile file = classRoot.findFileByRelativePath(path);
                    if (file != null) {
                        result.add(psiManager.findFile(file));
                    }
                }
            }
        }

        return result;
    }

    private static void proceedModuleRoots(Module module, String path, List<PsiFile> result, PsiManager psiManager) {
        VirtualFile[] sourceRoots = ModuleRootManager.getInstance(module).getSourceRoots();
        if (sourceRoots.length > 0) {
            for (VirtualFile sourceRoot : sourceRoots) {
                VirtualFile file = sourceRoot.findFileByRelativePath(path);
                if (file != null) {
                    result.add(psiManager.findFile(file));
                }
            }
        }
    }

    @NotNull
    public static String getFileRelativePath(PsiFile file) {
        String relativePath = getRelativePath(file.getVirtualFile(), file.getProject());
        return relativePath == null ? file.getName() : relativePath;
    }

    // c/p from com.intellij.ide.util.gotoByName.GotoFileCellRenderer.getRelativePath()
    @Nullable
    public static String getRelativePath(final VirtualFile virtualFile, final Project project) {
        String url = virtualFile.getPresentableUrl();
        if (project == null) {
            return url;
        }
        VirtualFile root = ProjectFileIndex.SERVICE.getInstance(project).getContentRootForFile(virtualFile);
        if (root != null) {
            return root.getName() + File.separatorChar + VfsUtilCore.getRelativePath(virtualFile, root, File.separatorChar);
        }

        final VirtualFile baseDir = project.getBaseDir();
        if (baseDir != null) {
            //noinspection ConstantConditions
            final String projectHomeUrl = baseDir.getPresentableUrl();
            if (url.startsWith(projectHomeUrl)) {
                final String cont = url.substring(projectHomeUrl.length());
                if (cont.isEmpty()) {
                    return null;
                }
                url = "..." + cont;
            }
        }
        return url;
    }

    public static boolean isFileCommited(Project project, VirtualFile file) {
        return ProjectLevelVcsManager.getInstance(project).getAllActiveVcss().length > 0
               && ChangeListManager.getInstance(project).getChange(file) == null;
    }

    public static GlobalSearchScope getModuleWithDependenciesScope(@NotNull PsiElement psi) {
        Module module = ModuleUtil.findModuleForPsiElement(psi);
        return module == null
               ? GlobalSearchScope.allScope(psi.getProject())
               : GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module);
    }

    public static GlobalSearchScope getModuleWithDependantsScope(@NotNull PsiElement psi) {
        Module module = ModuleUtil.findModuleForPsiElement(psi);
        return module == null
               ? GlobalSearchScope.allScope(psi.getProject())
               : GlobalSearchScope.moduleWithDependentsScope(module);
    }

    public static String getTopModule(PsiElement element) {
        String value = getPropertyValue(element, "logics.topModule");
        return value == null ? "dumb" : value;
    }

    public static List<String> getPossibleTopModules(PsiElement element) {
        List<PsiFile> propertyFiles = findFilesByPath(element, "lsfusion.properties");
        String possibleTopModulesString = getPropertyValue(propertyFiles, "logics.possibleTopModules");
        if (possibleTopModulesString != null && !possibleTopModulesString.trim().isEmpty()) {
            return Arrays.stream(possibleTopModulesString.split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());
        }

        String topModuleName = getPropertyValue(propertyFiles, "logics.topModule");
        return topModuleName == null ? Collections.emptyList() : Collections.singletonList(topModuleName);
    }
     
    public static String getDBNamingPolicy(PsiElement element) {
        return getPropertyValue(element, "db.namingPolicy");
    }

    public static int getDBMaxIdSize(PsiElement element) {
        String value = getPropertyValue(element, "db.maxIdLength");
        if (!isRedundantString(value)) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException ignored) {
            }
        }
        return 63;
    }

    public static String getLsfStrLiteralsLanguage(List<PsiFile> files) {
        return getPropertyValue(files, "logics.lsfStrLiteralsLanguage");
    }
    
    private static String getPropertyValue(PsiElement element, String propertyName) {
        List<PsiFile> files = findFilesByPath(element, "lsfusion.properties");
        return getPropertyValue(files, propertyName);
    }

    private static String getPropertyValue(List<PsiFile> files, String propertyName) {
        for (PsiFile file : files) {
            if (file instanceof PropertiesFile) {
                IProperty property = ((PropertiesFile) file).findPropertyByKey(propertyName);
                if (property != null) {
                    return BaseUtils.nullTrim(property.getValue());
                }
            }
        }
        return null;
    }    
    
    public static GlobalSearchScope getElementRequireScope(PsiElement myElement, String moduleName, boolean searchInRequiredModules) {
        GlobalSearchScope projectScope = getModuleWithDependenciesScope(myElement);

        if (moduleName != null) {
            Collection<LSFModuleDeclaration> modules = LSFGlobalResolver.findModules(moduleName, myElement.getProject(), projectScope);
            if (modules.isEmpty()) {
                return GlobalSearchScope.EMPTY_SCOPE;
            }

            GlobalSearchScope scope = GlobalSearchScope.EMPTY_SCOPE;
            List<VirtualFile> files = new ArrayList<>();

            for (LSFModuleDeclaration lsfModule : modules) {
                if (searchInRequiredModules) {
                    scope = scope.uniteWith(lsfModule.getLSFFile().getRequireScope());
                } else {
                    files.add(lsfModule.getLSFFile().getVirtualFile());
                }
            }

            return searchInRequiredModules
                   ? scope
                   : GlobalSearchScope.filesScope(myElement.getProject(), files);
        }

        return projectScope;
    }

    public static Module[] getModules(Project project) {
        return ModuleManager.getInstance(project).getModules();
    }

    public static GlobalSearchScope getScope(List<String> modulesToInclude, Project project) {
        GlobalSearchScope modulesScope = null;
        if (modulesToInclude != null && !modulesToInclude.isEmpty()) {
            ModuleManager moduleManager = ModuleManager.getInstance(project);
            for (String moduleToInclude : modulesToInclude) {
                Module logics = moduleManager.findModuleByName(moduleToInclude);
                if (logics != null) {
                    GlobalSearchScope moduleScope = logics.getModuleWithDependenciesScope();
                    modulesScope = modulesScope == null ? moduleScope : modulesScope.uniteWith(moduleScope);
                }
            }
        } else
            modulesScope = GlobalSearchScope.allScope(project);
        return modulesScope;
    }
}
