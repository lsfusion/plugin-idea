package com.lsfusion.dependencies.module;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.HighlightInfoType;
import com.intellij.codeInsight.daemon.impl.UpdateHighlightersUtil;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.tools.SimpleActionGroup;
import com.lsfusion.dependencies.DependenciesView;
import com.lsfusion.dependencies.GraphNode;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.lang.psi.cache.ModuleDependentsCache;
import com.lsfusion.lang.psi.declarations.LSFDeclaration;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import com.lsfusion.lang.psi.references.LSFModuleReference;
import com.lsfusion.lang.psi.references.LSFReference;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.BitSet;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class ModuleDependenciesView extends DependenciesView {
    private Editor currentEditor;
    private boolean redundantModulesWarningsEnabled;
    
    public ModuleDependenciesView(Project project, ToolWindowEx toolWindow) {
        super("Module dependencies", project, toolWindow, true);
    }

    @Override
    public boolean showPathToElement() {
        return true;
    }

    @Override
    public String getDependentTitle() {
        return "Requiring";
    }

    @Override
    public String getDependencyTitle() {
        return "Required";
    }

    @Override
    public int getAverageNodeWidth() {
        return 100;
    }

    @Override
    protected void addThirdToolbarActions(@NotNull SimpleActionGroup actions) {
        actions.add(new ToggleAction("Highlight redundant modules") {
            @Override
            public boolean isSelected(@NotNull AnActionEvent e) {
                return redundantModulesWarningsEnabled;
            }

            @Override
            public void setSelected(@NotNull AnActionEvent e, boolean state) {
                redundantModulesWarningsEnabled = state;
                if (state) {
                    highlightRedundantModules();
                } else if (currentEditor != null) {
                    clearRedundantModuleWarnings(currentEditor);
                }
            }

            @Override
            public void update(@NotNull AnActionEvent e) {
                super.update(e);
                e.getPresentation().setEnabled(currentElement instanceof LSFModuleDeclaration);
            }

            @Override
            public @NotNull ActionUpdateThread getActionUpdateThread() {
                return ActionUpdateThread.BGT;
            }

            @SuppressWarnings("removal")
            @Override
            public boolean displayTextInToolbar() {
                return true;
            }
        });
    }

    @Override
    public void getTargetEditorPsiElement(Editor editor, Consumer<PsiElement> elementConsumer, boolean skipSameEditor) {
        VirtualFile editorFile = FileDocumentManager.getInstance().getFile(editor.getDocument());
        boolean sameEditorFile = editor.equals(currentEditor);
        // if editor doesn't change, skip redraw as module doesn't change either
        // otherwise background process progress bar flashes in the toolbar
        if (!skipSameEditor || !sameEditorFile) {
            super.getTargetEditorPsiElement(editor, elementConsumer, skipSameEditor);
        }
        setCurrentEditor(editor, editorFile);
    }

    @Override
    public void getSelectedElement(Consumer<PsiElement> elementConsumer, boolean forceUpdate) {
        getTargetEditorPsiElement(targetElement -> {
            if (targetElement != null) {
                SelectedModuleData selectedModuleData = DumbService.getInstance(project).runReadActionInSmartMode(() -> {
                    PsiFile containingFile = targetElement.getContainingFile();
                    LSFModuleDeclaration currentModuleDeclaration = currentElement instanceof LSFModuleDeclaration ? (LSFModuleDeclaration) currentElement : null;
                    if (containingFile instanceof LSFFile) {
                        LSFModuleDeclaration moduleDeclaration = ((LSFFile) containingFile).getModuleDeclaration();
                        boolean moduleChanged = currentModuleDeclaration != null &&
                                (moduleDeclaration == null || !getVirtualFile(currentModuleDeclaration).equals(getVirtualFile(moduleDeclaration)));
                        return new SelectedModuleData(moduleDeclaration, moduleChanged);
                    }
                    return new SelectedModuleData(null, currentModuleDeclaration != null);
                });
                if (selectedModuleData.moduleChanged) {
                    disableRedundantModuleWarnings(currentEditor);
                }
                elementConsumer.accept(selectedModuleData.moduleDeclaration);
            } else if (currentElement instanceof LSFModuleDeclaration) {
                disableRedundantModuleWarnings(currentEditor);
            }
            elementConsumer.accept(null);
        }, !forceUpdate);
    }

    @Override
    public void getPathTarget(Consumer<String> pathConsumer) {
        getTargetEditorPsiElement(targetElement -> {
            String module = DumbService.getInstance(project).runReadActionInSmartMode(() -> {
                if (targetElement != null && targetElement.getContainingFile() instanceof LSFFile) {
                    LSFReference<?> ref = PsiTreeUtil.getParentOfType(targetElement, LSFReference.class);
                    if (ref != null) {
                        LSFDeclaration decl = ref.resolveDecl();
                        if (decl != null) {
                            return decl.getLSFFile().getModuleDeclaration().getName();
                        }
                    }
                }
                return null;
            });
            pathConsumer.accept(module);
        }, false);
    }

    @Override
    public void createDependencyNode(PsiElement element, Set<PsiElement> proceeded) {
        if(element != null) {
            LSFModuleDeclaration module = (LSFModuleDeclaration) element;

            if(onlyLeafs) {
                List<LSFModuleDeclaration> leafModules = new ArrayList<>();
                findDependencyLeafModules(module, leafModules, new HashSet<>());
                for(LSFModuleDeclaration leafModule : leafModules) {
                    addModelEdge(module, leafModule, true);
                }
            } else {
                for (LSFModuleDeclaration moduleDeclaration : module.getRequireModules()) {
                    if (moduleDeclaration != null && moduleDeclaration != module) {
                        String sourceDeclName = module.getDeclName();
                        String targetDeclName = moduleDeclaration.getDeclName();
                        if (allEdges || (!dataModel.containsNode(targetDeclName) || !dataModel.containsNode(sourceDeclName))) {
                            addModelEdge(module, moduleDeclaration, true);

                            if (proceeded.add(moduleDeclaration)) {
                                createDependencyNode(moduleDeclaration, proceeded);
                            }
                        }
                    }
                }
            }
        }
    }

    private void findDependencyLeafModules(LSFModuleDeclaration module, List<LSFModuleDeclaration> leafModules, Set<LSFModuleDeclaration> seenModules) {
        seenModules.add(module);
        List<LSFModuleDeclaration> requireModules = module.getRequireModules();
        if (seenModules.containsAll(requireModules)) {
            leafModules.add(module);
        } else {
            for (LSFModuleDeclaration requiredModule : requireModules) {
                findDependencyLeafModules(requiredModule, leafModules, seenModules);
            }
        }
    }

    @Override
    public void createDependentNode(GlobalSearchScope scope, PsiElement element, Set<PsiElement> proceeded) {
        if(element != null) {
            LSFModuleDeclaration module = (LSFModuleDeclaration) element;

            if(onlyLeafs) {
                List<LSFModuleDeclaration> leafModules = new ArrayList<>();
                findDependentLeafModules(module, leafModules, new HashSet<>());
                for (LSFModuleDeclaration leafModule : leafModules) {
                    if (scope == null || scope.accept(leafModule.getLSFFile().getVirtualFile())) {
                        addModelEdge(leafModule, module, false);
                    }
                }
            } else {
                Set<LSFModuleDeclaration> refModules = ModuleDependentsCache.getInstance(project).resolveWithCaching(module);
                if (refModules != null) {
                    for (LSFModuleDeclaration decl : refModules) {
                        assert decl != null;
                        ApplicationManager.getApplication().runReadAction(() -> ProgressManager.getInstance().runProcess(() -> {
                            if (scope == null || scope.accept(decl.getLSFFile().getVirtualFile())) {
                                String sourceDeclName = decl.getDeclName();
                                String targetDeclName = module.getDeclName();
                                if (allEdges || !dataModel.containsNode(targetDeclName) || !dataModel.containsNode(sourceDeclName)) {
                                    addModelEdge(decl, module, false);
                                }

                                if (proceeded.add(decl)) {
                                    createDependentNode(scope, decl, proceeded);
                                }
                            }
                        }, null));
                    }
                }
            }
        }
    }

    private void findDependentLeafModules(LSFModuleDeclaration module, List<LSFModuleDeclaration> leafModules, Set<LSFModuleDeclaration> seenModules) {
        seenModules.add(module);
        Set<LSFModuleDeclaration> refModules = ModuleDependentsCache.getInstance(project).resolveWithCaching(module);
        if(refModules != null) {
            if (seenModules.containsAll(refModules)) {
                leafModules.add(module);
            } else {
                for (LSFModuleDeclaration refModule : refModules) {
                    findDependentLeafModules(refModule, leafModules, seenModules);
                }
            }
        }
    }
    
    private void addModelEdge(LSFModuleDeclaration sourceModule, LSFModuleDeclaration targetModule, boolean required) {
        GraphNode sourceNode = dataModel.getNode(sourceModule.getDeclName());
        if (sourceNode == null) {
            sourceNode = new ModuleGraphNode(sourceModule, required);
        }
        GraphNode targetNode = dataModel.getNode(targetModule.getDeclName());
        if (targetNode == null) {
            targetNode = new ModuleGraphNode(targetModule, required);
        }

        dataModel.createEdge(sourceNode, targetNode, required);
    }

    private void highlightRedundantModules() {
        if (redundantModulesWarningsEnabled && currentElement instanceof LSFModuleDeclaration moduleDeclaration) {
            Editor targetEditor = currentEditor != null && !currentEditor.isDisposed() ? currentEditor : FileEditorManager.getInstance(project).getSelectedTextEditor();
            if (targetEditor != null) {
                setCurrentEditor(targetEditor, FileDocumentManager.getInstance().getFile(targetEditor.getDocument()));
                new Task.Backgroundable(project, "Highlighting redundant modules", true) {
                    private List<HighlightInfo> highlightInfos = Collections.emptyList();
                    @Override
                    public void run(@NotNull com.intellij.openapi.progress.ProgressIndicator indicator) {
                        highlightInfos = DumbService.getInstance(project).runReadActionInSmartMode(() -> calculateRedundantModules(moduleDeclaration));
                    }
                    @Override
                    public void onSuccess() {
                        if (redundantModulesWarningsEnabled && currentEditor == targetEditor) {
                            applyRedundantModuleWarnings(targetEditor, highlightInfos);
                        }
                    }
                }.queue();
            }
        }
    }

    private void applyRedundantModuleWarnings(@NotNull Editor editor, @NotNull List<HighlightInfo> highlightInfos) {
        clearRedundantModuleWarnings(editor);
        if (!editor.isDisposed()) {
            setHighlightersToEditor(editor, highlightInfos);
        }
    }

    private void clearRedundantModuleWarnings(@NotNull Editor editor) {
        if (!editor.isDisposed()) {
            setHighlightersToEditor(editor, Collections.emptyList());
        }
    }

    private void disableRedundantModuleWarnings(Editor editor) {
        if (redundantModulesWarningsEnabled) {
            redundantModulesWarningsEnabled = false;
            ApplicationManager.getApplication().invokeLater(() -> {
                if (editor != null) {
                    clearRedundantModuleWarnings(editor);
                }
                refreshThirdToolbarActions();
            });
        }
    }

    private void setCurrentEditor(Editor editor, VirtualFile editorFile) {
        if (currentEditor != editor) {
            disableRedundantModuleWarnings(currentEditor);
        }
        currentEditor = editor;
    }

    private void setHighlightersToEditor(Editor editor, List<HighlightInfo> highlightInfos) {
        Document document = editor.getDocument();
        UpdateHighlightersUtil.setHighlightersToEditor(project, document, 0, document.getTextLength(),
                highlightInfos, editor.getColorsScheme(), 0x4C534652);
    }

    @NotNull
    private List<HighlightInfo> calculateRedundantModules(@NotNull LSFModuleDeclaration moduleDeclaration) {
        List<RedundantModuleEntry> moduleEntries = new ArrayList<>();
        Map<Object, Integer> listedModuleIndexes = new HashMap<>();

        for (LSFModuleReference requireRef : moduleDeclaration.getRequireRefs()) {
            LSFModuleDeclaration requiredModule = requireRef.resolveDecl();
            if (requiredModule != null) {
                VirtualFile virtualFile = getVirtualFile(requiredModule);
                listedModuleIndexes.computeIfAbsent(virtualFile, k -> listedModuleIndexes.size());
                moduleEntries.add(new RedundantModuleEntry(requiredModule, virtualFile, requireRef.getTextRange()));
            }
        }
        Map<Object, BitSet> reachableListedModules = collectReachableListedModules(listedModuleIndexes, buildExplicitReverseDependencies(moduleEntries));
        String[] requiringModuleNames = new String[listedModuleIndexes.size()];

        for (RedundantModuleEntry sourceEntry : moduleEntries) {
            BitSet reachableModules = reachableListedModules.get(sourceEntry.virtualFile);
            if (reachableModules != null) {
                for (int targetIndex = reachableModules.nextSetBit(0); targetIndex >= 0; targetIndex = reachableModules.nextSetBit(targetIndex + 1)) {
                    if (targetIndex != listedModuleIndexes.get(sourceEntry.virtualFile) && requiringModuleNames[targetIndex] == null) {
                        requiringModuleNames[targetIndex] = sourceEntry.getGlobalName();
                    }
                }
            }
        }

        List<HighlightInfo> highlightInfos = new ArrayList<>();
        for (RedundantModuleEntry moduleEntry : moduleEntries) {
            String requiringModuleName = requiringModuleNames[listedModuleIndexes.get(moduleEntry.virtualFile)];
            if (requiringModuleName != null) {
                HighlightInfo highlightInfo = HighlightInfo.newHighlightInfo(HighlightInfoType.WARNING).range(moduleEntry.textRange)
                        .descriptionAndTooltip(String.format("Redundant REQUIRE: module '%s' is already required by module '%s'",
                                moduleEntry.getGlobalName(), requiringModuleName))
                        .create();
                if (highlightInfo != null) {
                    highlightInfos.add(highlightInfo);
                }
            }
        }

        return highlightInfos;
    }

    @NotNull
    private Map<Object, Set<Object>> buildExplicitReverseDependencies(@NotNull List<RedundantModuleEntry> moduleEntries) {
        Map<Object, Set<Object>> reverseDependencies = new HashMap<>();
        Map<Object, List<LSFModuleDeclaration>> directDependenciesCache = new HashMap<>();
        Deque<LSFModuleDeclaration> queue = new ArrayDeque<>();
        Set<Object> visitedModules = new HashSet<>();

        for (RedundantModuleEntry moduleEntry : moduleEntries) {
            reverseDependencies.computeIfAbsent(moduleEntry.virtualFile, key -> new LinkedHashSet<>());
            if (visitedModules.add(moduleEntry.virtualFile)) {
                queue.addLast(moduleEntry.moduleDeclaration);
            }
        }

        while (!queue.isEmpty()) {
            LSFModuleDeclaration currentModule = queue.removeFirst();
            VirtualFile virtualFile = getVirtualFile(currentModule);

            reverseDependencies.computeIfAbsent(virtualFile, key -> new LinkedHashSet<>());
            for (LSFModuleDeclaration dependencyModule : getDirectExplicitDependencies(currentModule, directDependenciesCache)) {
                VirtualFile dependencyKey = getVirtualFile(dependencyModule);
                reverseDependencies.computeIfAbsent(dependencyKey, key -> new LinkedHashSet<>()).add(virtualFile);
                if (visitedModules.add(dependencyKey)) {
                    queue.addLast(dependencyModule);
                }
            }
        }

        return reverseDependencies;
    }

    @NotNull
    private Map<Object, BitSet> collectReachableListedModules(@NotNull Map<Object, Integer> listedModuleIndexes,
                                                              @NotNull Map<Object, Set<Object>> reverseDependencies) {
        Map<Object, BitSet> reachableListedModules = new HashMap<>();
        Deque<Object> queue = new ArrayDeque<>();
        int listedModuleCount = listedModuleIndexes.size();

        for (Map.Entry<Object, Integer> listedModuleEntry : listedModuleIndexes.entrySet()) {
            BitSet moduleBitSet = new BitSet(listedModuleCount);
            moduleBitSet.set(listedModuleEntry.getValue());
            reachableListedModules.put(listedModuleEntry.getKey(), moduleBitSet);
            queue.addLast(listedModuleEntry.getKey());
        }

        while (!queue.isEmpty()) {
            Object virtualFile = queue.removeFirst();
            BitSet reachableModules = reachableListedModules.get(virtualFile);
            if (reachableModules == null || reachableModules.isEmpty()) {
                continue;
            }

            for (Object dependentKey : reverseDependencies.getOrDefault(virtualFile, Collections.emptySet())) {
                BitSet dependentReachableModules = reachableListedModules.computeIfAbsent(dependentKey, key -> new BitSet(listedModuleCount));
                int oldCardinality = dependentReachableModules.cardinality();
                dependentReachableModules.or(reachableModules);
                if (dependentReachableModules.cardinality() != oldCardinality) {
                    queue.addLast(dependentKey);
                }
            }
        }

        return reachableListedModules;
    }

    @NotNull
    private List<LSFModuleDeclaration> getDirectExplicitDependencies(@NotNull LSFModuleDeclaration moduleDeclaration,
                                                                     @NotNull Map<Object, List<LSFModuleDeclaration>> directDependenciesCache) {
        VirtualFile moduleKey = getVirtualFile(moduleDeclaration);
        List<LSFModuleDeclaration> cachedDependencies = directDependenciesCache.get(moduleKey);
        if (cachedDependencies == null) {
            Map<VirtualFile, LSFModuleDeclaration> directDependencies = new LinkedHashMap<>();
            for (LSFModuleReference requireRef : moduleDeclaration.getRequireRefs()) {
                LSFModuleDeclaration dependencyModule = requireRef.resolveDecl();
                if (dependencyModule != null) {
                    directDependencies.putIfAbsent(getVirtualFile(dependencyModule), dependencyModule);
                }
            }

            cachedDependencies = new ArrayList<>(directDependencies.values());
            directDependenciesCache.put(moduleKey, cachedDependencies);
        }

        return cachedDependencies;
    }

    @NotNull
    private VirtualFile getVirtualFile(@NotNull LSFModuleDeclaration moduleDeclaration) {
        return moduleDeclaration.getLSFFile().getVirtualFile();
    }

    private record RedundantModuleEntry(LSFModuleDeclaration moduleDeclaration, VirtualFile virtualFile, TextRange textRange) {
        public String getGlobalName() {
            return moduleDeclaration.getGlobalName();
        }
    }

    private record SelectedModuleData(LSFModuleDeclaration moduleDeclaration, boolean moduleChanged) {
    }

    @Override
    public void dispose() {
        if (currentEditor != null) {
            clearRedundantModuleWarnings(currentEditor);
        }
        super.dispose();
    }
}
