package com.lsfusion.util;

import com.intellij.lang.properties.IProperty;
import com.intellij.lang.properties.psi.PropertiesFile;
import com.intellij.openapi.module.Module;
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
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.util.Processor;
import com.lsfusion.lang.psi.Result;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import com.lsfusion.lang.psi.indexes.ModuleIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        final Result<Boolean> hasFiles = new Result<Boolean>(false);
        FilenameIndex.processFilesByName(
                fileName, false, new Processor<PsiFileSystemItem>() {
                    @Override
                    public boolean process(PsiFileSystemItem file) {
                        if (!file.isDirectory() && file instanceof PsiFile) {
                            hasFiles.setResult(true);
                            return false;
                        }
                        return true;
                    }
                },
                scope,
                project,
                null
        );
        return hasFiles.getResult();
    }

    public static void findFilesWithShortName(String fileName, final List<PsiFile> files, Project project, GlobalSearchScope scope) {
        FilenameIndex.processFilesByName(
                fileName, false, new Processor<PsiFileSystemItem>() {
                    @Override
                    public boolean process(PsiFileSystemItem file) {
                        if (!file.isDirectory() && file instanceof PsiFile) {
                            files.add((PsiFile) file);
                        }
                        return true;
                    }
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
        final PsiManager psiManager = PsiManager.getInstance(module.getProject());

        final List<PsiFile> result = new ArrayList<PsiFile>();

        final OrderEnumerator orderEnumerator = ModuleRootManager.getInstance(module).orderEntries();

        proceedModuleRoots(module, path, result, psiManager);
        orderEnumerator.forEachModule(new Processor<Module>() {
            @Override
            public boolean process(Module module) {
                proceedModuleRoots(module, path, result, psiManager);
                return true;
            }
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
        List<PsiFile> filesByPath = findFilesByPath(element, "lsfusion.properties");
        for (PsiFile file : filesByPath) {
            if (file instanceof PropertiesFile) {
                PropertiesFile propFile = (PropertiesFile) file;
                IProperty property = propFile.findPropertyByKey("logics.topModule");
                if (property != null) {
                    return BaseUtils.nullTrim(property.getValue());
                }
            }
        }
        return "dumb";
    }

    public static String getDBNamingPolicy(PsiElement element) {
        List<PsiFile> filesByPath = findFilesByPath(element, "lsfusion.properties");
        for (PsiFile file : filesByPath) {
            if (file instanceof PropertiesFile) {
                PropertiesFile propFile = (PropertiesFile) file;
                IProperty property = propFile.findPropertyByKey("db.namingPolicy");
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
            Collection<LSFModuleDeclaration> modules = ModuleIndex.getInstance().get(moduleName, myElement.getProject(), projectScope);
            if (modules.isEmpty()) {
                return GlobalSearchScope.EMPTY_SCOPE;
            }

            GlobalSearchScope scope = GlobalSearchScope.EMPTY_SCOPE;
            List<VirtualFile> files = new ArrayList<VirtualFile>();

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
}
