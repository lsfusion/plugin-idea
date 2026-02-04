package com.lsfusion.mcp;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Conditions;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.MergeQuery;
import com.intellij.util.Processor;
import com.intellij.util.Query;
import com.lsfusion.lang.classes.CustomClassSet;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.classes.LSFValueClass;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.*;
import com.lsfusion.lang.psi.extend.LSFExtend;
import com.lsfusion.lang.psi.indexes.LSFStringStubIndex;
import com.lsfusion.lang.psi.references.LSFGlobalReference;
import com.lsfusion.lang.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.lang.psi.stubs.types.GlobalDeclStubElementType;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import com.lsfusion.refactoring.CanonicalNameUtils;
import com.lsfusion.refactoring.PropertyCanonicalNameUtils;
import com.lsfusion.util.BaseUtils;
import com.lsfusion.util.LSFFileUtils;
import com.lsfusion.util.LSFPsiUtils;
import com.lsfusion.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

/**
 * MCPSearchService: findElements for MCP server consumption.
 * This is a best-effort initial implementation that relies on available indices and resolvers in the plugin.
 * It supports filters: modules, names, elementTypes, excludedElements, and maxSymbols (approximate early-stop).
*/
public class MCPSearchUtils {
    private static final Logger LOG = Logger.getInstance(MCPSearchUtils.class);

    private static final List<LSFMCPDeclaration.ElementType> priorityTypes = BaseUtils.toList(
        LSFMCPDeclaration.ElementType.MODULE,
        LSFMCPDeclaration.ElementType.CLASS,
        LSFMCPDeclaration.ElementType.PROPERTY,
        LSFMCPDeclaration.ElementType.ACTION,
        LSFMCPDeclaration.ElementType.FORM);

    private static int getPriority(LSFMCPDeclaration.ElementType type) {
        if (type == null) return 5;
        switch (type) {
            case MODULE:
            case CLASS:
                return 1;
            case PROPERTY:
                return 2;
            case ACTION:
                return 3;
            case FORM:
                return 4;
            default:
                return 5;
        }
    }

    public static final int DEFAULT_MAX_SYMBOLS = 200_000; // fail-safe
    public static final int DEFAULT_MIN_SYMBOLS = 1_000; // fail-safe
    public static final int DEFAULT_TIMEOUT_SECS = 10; // default 10 seconds

    private static final class SelectedStatement {
        private final @NotNull LSFMCPDeclaration decl;
        private final @NotNull List<LSFMCPStatement> withExtends;
        private final @NotNull JSONObject json;
        private final int jsonSize;

        private SelectedStatement(@NotNull LSFMCPDeclaration decl, GlobalSearchScope searchScope) {
            this.decl = decl;
            this.withExtends = getElementWithExtends(decl, searchScope);
            this.json = getJsonFromStatement(this, 1);
            this.jsonSize = this.json.toString().length();
        }
    }

    /**
     * Main entry: executes search based on provided JSON query.
     * Expected structure (simplified):
     * {
     *   modules: "ModuleA, ModuleB", // optional, CSV; filter results by these modules
     *   scope: "project" | "project and libraries" | "modules", // optional; SearchScope concept from IDEA
     *     // omitted scope or "project and libraries" -> allScope (includes libraries)
     *     // scope=project -> projectOnly scope (excludes libraries)
     *     // otherwise, scope can be a CSV list of IDEA module names (e.g. "my-idea-module1,my-idea-module2")
     *   requiredModules: true, // optional (default: true); if true, include REQUIRE-d modules for modules in `modules`
     *   query: "cust,Order", // optional, CSV; filters by element name and code (word or regex)
     *   elementTypes: "class,property,action", // optional, CSV
     *   classes: "MyNS.MyClass, MyOtherNS.OtherClass", // optional, CSV canonical names
     *   relatedElements: "property:MyNS.myProp[MyNS.MyClass], MyModule(10:5)", // optional, CSV
     *   relatedDirection: "both" | "uses" | "used", // optional, applies to ALL relatedElements (default: both)
     *   moreFilters: [ { ... }, { ... } ], // optional: additional filter objects; results are merged (OR)
     *   maxSymbols: 100000 // approximate cap for total result code length
     * }
     */
    public static JSONObject findElements(@NotNull Project project, @NotNull JSONObject query) {
        // Parse everything
        int maxSymbols = Math.max(1, query.optInt("maxSymbols", DEFAULT_MAX_SYMBOLS));
        int minSymbols = Math.max(0, query.optInt("minSymbols", DEFAULT_MIN_SYMBOLS));

        // Timeout parsing simplified: only timeoutSeconds is supported; default to 10s
        int sec = query.optInt("timeoutSeconds", DEFAULT_TIMEOUT_SECS);
        final long timeoutMillis = (long) Math.max(1, sec) * 1000L;

        // Allocate 90% of timeout for the search stage and 10% for post-processing (expand / minSymbols fill).
        // Do NOT sprinkle timeout checks through the search loops: keep the search logic as-is and only bound it
        // by executor termination time.
        final long searchBudgetMillis = Math.max(1L, (timeoutMillis * 9L) / 10L);
        final long startMillis = System.currentTimeMillis();
        final long deadlineMillis = startMillis + timeoutMillis;

        SearchState state = new SearchState(maxSymbols);
        List<String> errors = Collections.synchronizedList(new ArrayList<>());
        AtomicBoolean timeoutHit = new AtomicBoolean(false);

        GlobalSearchScope searchScope = searchElements(project, query, state, searchBudgetMillis, timeoutHit, errors);

        List<SelectedStatement> bestStatements = new ArrayList<>();
        JSONArray best = sortAndTruncate(state.statementsByPriority, maxSymbols, bestStatements);

        int bestTotal = best.toString().length();

        // 1) First, try to expand shortCode for the selected set under maxSymbols.
        if (bestTotal < maxSymbols) {
            try {
                JSONArray expanded = tryExpandShortCodeUntilBudget(bestStatements, maxSymbols, deadlineMillis);
                if (expanded != null) {
                    best = expanded;
                    bestTotal = expanded.toString().length();
                }
            } catch (Throwable t) {
                handleError(t, "Error expanding short code", errors);
            }
        }

        final AtomicBoolean nonMatchingStatementsAdded = new AtomicBoolean(false);
        // 2) If still below minSymbols, append neighboring declarations (in-file PSI order) with full code.
        if (!timeoutHit.get() && minSymbols > 0 && bestTotal < minSymbols && bestTotal < maxSymbols) {
            try {
                // Neighbor fill ignores the original filter predicate by design; report this to the caller.
                fillNeighborsUntilMinSymbols(bestStatements, best, bestTotal, searchScope, minSymbols, maxSymbols, deadlineMillis, nonMatchingStatementsAdded);
            } catch (Throwable t) {
                handleError(t, "Error filling neighbors", errors);
            }
        }

        String meta = getMetaReason(timeoutHit.get(), best.toString().length() >= maxSymbols, nonMatchingStatementsAdded.get());
        return assembleResult(best, meta, errors);
    }

    private static @NonNull GlobalSearchScope searchElements(@NonNull Project project, @NonNull JSONObject query, SearchState state, long searchBudgetMillis, AtomicBoolean timeoutHit, List<String> errors) {
        // Build iterations: run all applicable types simultaneously with in-iteration filtering
        Set<LSFMCPDeclaration> seen = Collections.newSetFromMap(new ConcurrentHashMap<>());
        ConcurrentMap<RelatedKey, RelatedState> relatedCache = new ConcurrentHashMap<>();

        // Executor and dynamic scheduling support
        ExecutorService exec = createPriorityExecutor(Math.min(6, Math.max(1, Runtime.getRuntime().availableProcessors() - 1)));
        Runnable awaitAll = createAwaitAll(exec, searchBudgetMillis, timeoutHit, state.stopRequested);
        // helper to submit tasks with accounting
        TaskSubmitter submit = createSubmit(exec, state, errors);

        GlobalSearchScope searchScope = run(project, query, seen, relatedCache, state, submit);

        JSONArray moreFilters = query.optJSONArray("moreFilters");
        if (moreFilters != null && !moreFilters.isEmpty())
            for (int i = 0; i < moreFilters.length(); i++)
                searchScope = searchScope.union(run(project, moreFilters.optJSONObject(i), seen, relatedCache, state, submit));

        // Wait for all tasks to complete (but keep a reserve for post-processing)
        awaitAll.run();

        return searchScope;
    }

    private static JSONArray sortAndTruncate(List<SelectedStatement>[] state, int maxSymbols, List<SelectedStatement> bestStatements) {
        int currentTotalSymbols = 0;
        JSONArray best = new JSONArray();

        for (int p = 1; p <= 5; p++) {
            for (SelectedStatement selSt : state[p]) {
                currentTotalSymbols += selSt.jsonSize;
                if (currentTotalSymbols > maxSymbols)
                    return best;

                best.put(selSt.json);
                bestStatements.add(selSt);
            }
        }
        return best;
    }

    private static String getMetaReason(boolean timeoutHit, boolean maxSymbolsHit, boolean nonMatchingAdded) {
        // Single-line meta reason (exactly one string).
        // Priority: timeout > maxSymbols > nonMatching elements added.
        if (timeoutHit) {
            return "too long - timeout hit";
        } else if (maxSymbolsHit) {
            return "too large - max symbols hit";
        } else if (nonMatchingAdded) {
            return "too small - non matching elements added";
        }
        return null;
    }

    private static JSONObject assembleResult(JSONArray items, String meta, List<String> errors) {
        JSONObject resultObject = new JSONObject()
                .put("items", items);
        if (meta != null)
            resultObject.put("meta", meta);
        if (!errors.isEmpty()) {
            resultObject.put("errors", new JSONArray(new LinkedHashSet<>(errors)));
        }
        return resultObject;
    }

    private static Processor<LSFMCPDeclaration> createSearchProcessor(SearchState state, Set<LSFMCPDeclaration> seen, List<NameFilter> queryFilters, Set<LSFMCPDeclaration.ElementType> elementTypes, Set<LSFClassDeclaration> classDecls, Map<LSFMCPDeclaration, Direction> related, GlobalSearchScope searchScope, ConcurrentMap<RelatedKey, RelatedState> relatedCache) {
        return st -> {
            if (state.stopRequested.get()) {
                return false;
            }
            int p = getPriority(st.getMCPType());
            if (p > state.stopPriorityThreshold.get()) {
                return true;
            }
            assert getStatement(st) == st; // is not extend
            if (!seen.add(st)) return true;

            // already processed

            if (matchesAllFilters(st, queryFilters, elementTypes, classDecls, related, searchScope, relatedCache)) {
                SelectedStatement selSt = new SelectedStatement(st, searchScope);

                synchronized (state.statementsByPriority[p]) {
                    state.statementsByPriority[p].add(selSt);
                }
                state.symbolsByPriority[p].addAndGet(selSt.jsonSize);
                state.updateThreshold();
            }
            return true; // continue search
        };
    }

    private static Runnable createAwaitAll(ExecutorService exec, long searchBudgetMillis, AtomicBoolean timeoutHit, AtomicBoolean stopRequested) {
        return () -> {
            try {
                exec.shutdown();
                try {
                    // Reserve time for post-processing by waiting only for the search budget.
                    boolean terminated = exec.awaitTermination(searchBudgetMillis, TimeUnit.MILLISECONDS);
                    if (!terminated) {
                        timeoutHit.set(true);
                    }
                } catch (InterruptedException ie) {
                    // Requested: treat interruption of awaitTermination as a timeout hit.
                    timeoutHit.set(true);
                    Thread.currentThread().interrupt();
                }
            } finally {
                stopRequested.set(true);
                exec.shutdownNow();
            }
        };
    }

    private static TaskSubmitter createSubmit(ExecutorService exec, SearchState state, List<String> errors) {
        return (priority, r) -> {
            state.pendingTasksByPriority[priority].incrementAndGet();
            exec.execute(new PriorityTask(priority, () -> {
                try {
                    r.run();
                } catch (com.intellij.openapi.progress.ProcessCanceledException e) {
                    throw e; // Standard platform cancellation, must be rethrown or handled by the platform
                } catch (Throwable t) {
                    handleError(t, "Error executing MCP search task", errors);
                } finally {
                    state.pendingTasksByPriority[priority].decrementAndGet();
                    state.checkAllPBetterDone();
                }
            }));
        };
    }

    private static boolean submitTypeTasks(Project project, GlobalSearchScope searchScope, Set<LSFMCPDeclaration.ElementType> elementTypes, Processor<LSFMCPDeclaration> processor, TaskSubmitter submit) {
        boolean typesFullyStreamable = !elementTypes.isEmpty();
        for (LSFMCPDeclaration.ElementType et : elementTypes.isEmpty() ? priorityTypes : (Collection<LSFMCPDeclaration.ElementType>) elementTypes) {
            GlobalDeclStubElementType<?, ?> stubType = et.stubType;
            if (stubType != null) { // only index-backed types
                LSFStringStubIndex<? extends LSFGlobalDeclaration> index = stubType.getGlobalIndex();
                submit.submit(getPriority(et), () -> ReadAction.run(() -> {
                    for (String key : index.getAllKeys(project)) {
                        Collection<? extends LSFGlobalDeclaration> items = LSFGlobalResolver.getItemsFromIndex(index, key, project, searchScope, LSFLocalSearchScope.GLOBAL);
                        for (LSFGlobalDeclaration<?, ?> it : items) {
                            if (!processStatement(it, processor))
                                return; // stop this iteration
                        }
                    }
                }));
            } else {
                typesFullyStreamable = false;
            }
        }
        return typesFullyStreamable;
    }

    private static boolean submitClassTasks(GlobalSearchScope searchScope, Set<LSFClassDeclaration> classDecls, Processor<LSFMCPDeclaration> processor, TaskSubmitter submit, boolean onlyPropertiesClassesActions) {
        Project project = searchScope.getProject();
        for (LSFClassDeclaration targetClass : classDecls) {
            submit.submit(1, () -> ReadAction.run(() -> {
                for (LSFValueClass vc : CustomClassSet.getClassParentsRecursively(targetClass)) {
                    if (vc instanceof LSFClassDeclaration cls && !processStatement(cls, processor)) return; // early stop
                }
                // actually declarations
                for (PsiElement item : LSFPsiUtils.getPropertiesApplicableToClass(targetClass, project, searchScope, LSFLocalSearchScope.GLOBAL, true, true)) {
                    if (!processStatement(item, processor)) return; // early stop for this task
                }
                for (PsiElement item : LSFPsiUtils.getActionsApplicableToClass(targetClass, project, searchScope, LSFLocalSearchScope.GLOBAL, true, true)) {
                    if (!processStatement(item, processor)) return; // early stop for this task
                }
                if (!onlyPropertiesClassesActions) {
                    for (LSFMCPDeclaration item : nextUsed(getStatement(targetClass), searchScope))
                        if (!processor.process(item)) return;
                }
            }));
        }
        return !classDecls.isEmpty();
    }

    private static boolean submitRelatedTasks(Map<LSFMCPDeclaration, Direction> related, GlobalSearchScope searchScope, Processor<LSFMCPDeclaration> processor, TaskSubmitter submit) {
        // Global visited marks (single set for all directions)
        final Set<LSFMCPDeclaration> visitedRelated = Collections.newSetFromMap(new ConcurrentHashMap<>());
        for (Map.Entry<LSFMCPDeclaration, Direction> unit : related.entrySet()) {
            submit.submit(1, () -> ReadAction.run(() -> streamRelated(unit.getKey(), unit.getValue(), searchScope, processor, visitedRelated)));
        }
        return !related.isEmpty();
    }

    private static void submitFileTasks(GlobalSearchScope searchScope, Processor<LSFMCPDeclaration> processor, TaskSubmitter submit) {
        for (LSFFile lsfFile : ReadAction.compute(() -> LSFFileUtils.getLsfFiles(searchScope))) {
            submit.submit(5, () -> ReadAction.run(() -> {
                for (LSFMCPDeclaration st : LSFMCPDeclaration.getMCPDeclarations(lsfFile)) {
                    if (!processor.process(st)) break;
                }
            }));
        }
    }

    private static @NotNull GlobalSearchScope run(@NotNull Project project,
                                                 @NotNull JSONObject query,
                                                 @NotNull Set<LSFMCPDeclaration> seen,
                                                 @NotNull ConcurrentMap<RelatedKey, RelatedState> relatedCache,
                                                 @NotNull SearchState state,
                                                 @NotNull TaskSubmitter submit) {
        GlobalSearchScope searchScope = ReadAction.compute(() -> buildSearchScope(project, query.optString("modules"), query.optString("scope"), query.optBoolean("requiredModules", true)));
        List<NameFilter> queryFilters = parseMatchersCsv(query.optString("query"));
        Set<LSFMCPDeclaration.ElementType> elementTypes = parseElementTypes(query.optString("elementTypes"));

        Set<LSFClassDeclaration> classDecls = ReadAction.compute(() -> parseClasses(project, searchScope, query.optString("classes")));
        Map<LSFMCPDeclaration, Direction> related = ReadAction.compute(() -> parseRelated(project, searchScope, query.optString("relatedElements"), query.optString("relatedDirection")));

        // Shared processor that applies all filters and returns false to stop the current iteration
        final Processor<LSFMCPDeclaration> processor = createSearchProcessor(state, seen, queryFilters, elementTypes, classDecls, related, searchScope, relatedCache);

        // Element-type iterations (only for index-backed types and only if elementTypes filter provided)
        boolean typesFullyStreamable = submitTypeTasks(project, searchScope, elementTypes, processor, submit);

        // Classes-based iterations
        boolean classesFullyStreamable = submitClassTasks(searchScope, classDecls, processor, submit, isOnlyPropertiesClassesActions(elementTypes));

        // Related elements iterations: dynamically schedule traversal tasks
        boolean relatedFullyStreamable = submitRelatedTasks(related, searchScope, processor, submit);

        // Per-file iterations (fallback) — run only if no block is fully streamable
        if (!classesFullyStreamable && !relatedFullyStreamable && !typesFullyStreamable) {
            submitFileTasks(searchScope, processor, submit);
        }

        return searchScope;
    }

    private static boolean isOnlyPropertiesClassesActions(Set<LSFMCPDeclaration.ElementType> elementTypes) {
        boolean onlyPropertiesClassesActions = !elementTypes.isEmpty();
        for (LSFMCPDeclaration.ElementType et : elementTypes) {
            onlyPropertiesClassesActions &= et.equals(LSFMCPDeclaration.ElementType.PROPERTY) || et.equals(LSFMCPDeclaration.ElementType.CLASS) || et.equals(LSFMCPDeclaration.ElementType.ACTION);
        }
        return onlyPropertiesClassesActions;
    }

    private static boolean isTimedOut(long deadlineMillis) {
        return System.currentTimeMillis() > deadlineMillis;
    }

    private static final int FULL_TEXT_SHORT_CODE_FACTOR = 1_000_000_000;

    private static void fillNeighborsUntilMinSymbols(@NotNull List<SelectedStatement> selected,
                                                     @NotNull JSONArray selectedJson,
                                                     int selectedLen,
                                                     @NotNull GlobalSearchScope searchScope,
                                                     int minSymbols,
                                                     int maxSymbols,
                                                     long deadlineMillis,
                                                     @NotNull AtomicBoolean nonMatchingStatementsAdded) {
        // Best-effort: keep already selected JSON (typically expanded shortCode) and append neighboring declarations
        // in the same file (in-file PSI order) until reaching minSymbols (maxSymbols is a hard cap).
        // Neighbor code is generated with a large factor to effectively include full text.
        ReadAction.run(() -> {
            // Build per-file ordered declarations and declaration→index maps.
            Map<LSFFile, List<LSFMCPDeclaration>> fileDecls = new HashMap<>();
            Map<LSFFile, Map<LSFMCPDeclaration, Integer>> fileDeclToIndex = new HashMap<>();

            Set<LSFMCPDeclaration> added = new HashSet<>();
            Set<LSFFile> files = new LinkedHashSet<>();
            for (SelectedStatement st : selected) {
                added.add(st.decl);
                files.add((LSFFile) st.decl.getContainingFile());
            }

            for (LSFFile file : files) {
                if (isTimedOut(deadlineMillis)) return;
                List<LSFMCPDeclaration> decls = LSFMCPDeclaration.getMCPDeclarationsInFileOrder(file);
                fileDecls.put(file, decls);

                Map<LSFMCPDeclaration, Integer> declToIndex = new IdentityHashMap<>(decls.size() * 2);
                for (int i = 0; i < decls.size(); i++) {
                    declToIndex.put(decls.get(i), i);
                }
                fileDeclToIndex.put(file, declToIndex);
            }

            // Queue-based neighbor expansion: enqueue idx-1/idx+1 for each included declaration,
            // then iteratively add missing neighbors and enqueue their neighbors too.
            final class IndexRef {
                final LSFFile file;
                final int idx;

                IndexRef(LSFFile file, int idx) {
                    this.file = file;
                    this.idx = idx;
                }
            }

            ArrayDeque<IndexRef> queue = new ArrayDeque<>();
            for (SelectedStatement st : selected) {
                LSFFile file = (LSFFile) st.decl.getContainingFile();

                Map<LSFMCPDeclaration, Integer> declToIndex = fileDeclToIndex.get(file);
                if (declToIndex == null) continue;
                Integer idx = declToIndex.get(st.decl);
                if (idx == null) continue;

                queue.addLast(new IndexRef(file, idx - 1));
                queue.addLast(new IndexRef(file, idx + 1));
            }

            int totalLen = selectedLen;
            while (!queue.isEmpty()) {
                if (isTimedOut(deadlineMillis)) return;
                IndexRef ref = queue.removeFirst();
                List<LSFMCPDeclaration> decls = fileDecls.get(ref.file);
                if (ref.idx < 0 || ref.idx >= decls.size()) continue;

                LSFMCPDeclaration cand = decls.get(ref.idx);

                if (!added.add(cand)) {
                    continue;
                }

                JSONObject json = getJsonFromStatement(cand, searchScope);
                totalLen += json.toString().length();
                if (totalLen > maxSymbols)
                    return;
                selectedJson.put(json);
                nonMatchingStatementsAdded.set(true);
                if (totalLen > minSymbols)
                    return;

                queue.addLast(new IndexRef(ref.file, ref.idx - 1));
                queue.addLast(new IndexRef(ref.file, ref.idx + 1));
            }

            for (LSFFile file : LSFFileUtils.getLsfFiles(searchScope)) {
                if (fileDecls.containsKey(file)) {
                    continue;
                }

                for (LSFMCPDeclaration cand : LSFMCPDeclaration.getMCPDeclarationsInFileOrder(file)) {
                    if (isTimedOut(deadlineMillis)) return;
                    JSONObject json = getJsonFromStatement(cand, searchScope);
                    totalLen += json.toString().length();
                    if (totalLen > maxSymbols)
                        return;
                    selectedJson.put(json);
                    nonMatchingStatementsAdded.set(true);
                    if(totalLen > minSymbols)
                        return;
                }
            }
        });
    }

    private static @Nullable JSONArray tryExpandShortCodeUntilBudget(@NotNull List<SelectedStatement> selected,
                                                                    int maxSymbols,
                                                                    long deadlineMillis) {
        // Rebuild the same set of statements with progressively larger "keepPrefixLength" values
        // (x10 each iteration) while staying within the maxSymbols budget.
        return ReadAction.compute(() -> {
            JSONArray best = null;
            int bestLen = -1;

            int factor = 1;
            while (true) {
                JSONArray cur = new JSONArray();
                int totalLen = 0;
                for (SelectedStatement st : selected) {
                    if (isTimedOut(deadlineMillis)) {
                        return best;
                    }
                    JSONObject json = getJsonFromStatement(st, factor);
                    cur.put(json);
                    totalLen += json.toString().length();
                    if (totalLen > maxSymbols) {
                        break;
                    }
                }

                if (totalLen > maxSymbols) {
                    break;
                }
                if (totalLen == bestLen) {
                    break; // no further expansion changes the output size
                }

                best = cur;
                bestLen = totalLen;

                if (factor >= 1_000_000_000) {
                    break;
                }
                factor = factor * 10;
            }

            return best;
        });
    }

    // region Parsing & filters

    // Models for filters (direct PSI declarations)
    private enum Direction { USES, USED, BOTH }

    private static boolean isWordSearch(@NotNull String s) {
        // Treat as a "word" ONLY when it matches lsFusion ID tokenization and what
        // PsiSearchHelper.processElementsWithWord expects.
        // lsFusion ID (see LSF.flex): FIRST_ID_LETTER=[a-zA-Z], NEXT_ID_LETTER=[a-zA-Z_0-9]
        // Everything else is treated as regex.
        if (s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (i == 0) {
                if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))) return false;
            } else {
                boolean ok = (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '_';
                if (!ok) return false;
            }
        }
        return true;
    }

    private static @NotNull List<NameFilter> parseMatchersCsv(@Nullable String csv) {
        List<NameFilter> res = new ArrayList<>();
        for (String token : parseCsv(csv)) {
            if (token.isEmpty()) continue;

            if (isWordSearch(token)) {
                res.add(new NameFilter(token, null));
            } else {
                Pattern pattern = null;
                try {
                    pattern = Pattern.compile(token);
                } catch (Exception ignore) {
                    // ignore invalid regex
                }
                if (pattern != null) {
                    res.add(new NameFilter(null, pattern));
                }
            }
        }
        return res;
    }

    private static @NotNull List<String> parseCsv(@Nullable String csv) {
        if (csv == null) return Collections.emptyList();
        String s = csv.trim();
        if (s.isEmpty()) return Collections.emptyList();

        List<String> res = new ArrayList<>();
        for (String part : s.split(",", -1)) {
            String v = part.trim();
            if (!v.isEmpty()) res.add(v);
        }
        return res;
    }

    // region Parse + generate

    private static Set<LSFClassDeclaration> parseClasses(Project project, GlobalSearchScope scope, @Nullable String classesCsv) {
        if (classesCsv.isEmpty()) return Collections.emptySet();

        Set<LSFClassDeclaration> res = new HashSet<>();
        for (String name : parseCsv(classesCsv)) {
            res.addAll(resolveClassesByName(project, scope, name));
        }
        return res;
    }

    private static Map<LSFMCPDeclaration, Direction> parseRelated(Project project,
                                                                  GlobalSearchScope scope,
                                                                  @Nullable String relatedElementsCsv,
                                                                  @Nullable String relatedDirection) {
        if (relatedElementsCsv.isEmpty()) return Collections.emptyMap();

        Direction dir = Direction.BOTH;
        if ("uses".equals(relatedDirection)) {
            dir = Direction.USES;
        } else if ("used".equals(relatedDirection)) {
            dir = Direction.USED;
        }

        Map<LSFMCPDeclaration, Direction> units = new HashMap<>();
        for (String token : parseCsv(relatedElementsCsv)) {
            for (LSFMCPDeclaration stmt : getStatementsFromJson(project, scope, token)) {
                units.put(stmt, dir);
            }
        }
        return units;
    }

    private static Set<LSFMCPDeclaration> getStatementsFromName(Project project, GlobalSearchScope scope, LSFMCPDeclaration.ElementType type, String canonicalName) {
        Set<LSFMCPDeclaration> decls = new HashSet<>();
        for (LSFGlobalDeclaration<?, ?> d : getElementsFromName(project, scope, type, canonicalName))
            decls.add(getStatement(d));
        return decls;
    }

    private static @Nullable Collection<? extends LSFGlobalDeclaration> getElementsFromName(Project project, GlobalSearchScope scope, LSFMCPDeclaration.ElementType type, String name) {
        if (type == null) return Collections.emptyList();
        if (name == null || name.isEmpty()) return Collections.emptyList();

        Condition condition = Conditions.alwaysTrue();

        if (type == LSFMCPDeclaration.ElementType.PROPERTY || type == LSFMCPDeclaration.ElementType.ACTION) {
            int bracketPos = name.indexOf(PropertyCanonicalNameUtils.signatureLBracket);
            if (bracketPos >= 0) {
                int r = name.lastIndexOf(PropertyCanonicalNameUtils.signatureRBracket);
                if (r < bracketPos) return Collections.emptyList();

                List<LSFClassSet> signature = new ArrayList<>();
                for (String part : name.substring(bracketPos + 1, r).split(",", -1)) {
                    String canonicalOrData = part.trim();
                    LSFClassSet result = LSFPsiImplUtil.resolveDataClass(canonicalOrData, true);
                    if (result == null) {
                        Collection<LSFClassDeclaration> decls = resolveClassesByName(project, scope, canonicalOrData);
                        if (!decls.isEmpty())
                            result = new CustomClassSet(decls.iterator().next());
                    }
                    signature.add(result);
                }
                name = name.substring(0, bracketPos);
                condition = decl -> signature.equals(((LSFActionOrGlobalPropDeclaration<?, ?>) decl).resolveParamClasses());
            }
        }

        GlobalDeclStubElementType stubType = type.stubType;
        if (stubType == null) return Collections.emptyList();

        Collection<? extends LSFGlobalDeclaration> elements;
        if(stubType instanceof FullNameStubElementType) {
            String namespace = null;
            if (CanonicalNameUtils.isCorrect(name)) {
                namespace = CanonicalNameUtils.getNamespace(name);
                name = CanonicalNameUtils.getName(name);
            }

            elements = LSFGlobalResolver.findElements(name, namespace, project, scope, (FullNameStubElementType<?, ?>) stubType, condition);
        } else
            elements = LSFGlobalResolver.findModules(name, project, scope);
        return elements;
    }

    /**
     * Single entry point: resolve an element (LSFFullNameDeclaration/LSFMCPStatement) from a related-element token.
     * Token contract:
     *  - Named elements: `type:name`, where `name` is canonicalName (best-effort).
     *  - Unnamed elements: `location` where location is "<module>(<line>:<symbolInLine>)", e.g. "MyModule(10:5)".
     */
    private static Collection<LSFMCPDeclaration> getStatementsFromJson(Project project, GlobalSearchScope scope, @Nullable String token) {
        String s = token.trim();
        if (s.isEmpty()) return Collections.emptySet();

        // 1) location-based resolution (for unnamed elements)
        // Try to parse/resolve as location first; if it doesn't work, fallback to `type:name`.
        LSFMCPDeclaration byLocation = getStatementByLocation(project, scope, s);
        if (byLocation != null) return Collections.singletonList(byLocation);

        // 2) name-based resolution using resolver + condition when possible (for named types)
        int colon = s.indexOf(':');
        if (colon <= 0 || colon >= s.length() - 1) return Collections.emptyList();

        String typeStr = s.substring(0, colon).trim();
        String name = s.substring(colon + 1).trim();
        if (typeStr.isEmpty() || name.isEmpty()) return Collections.emptyList();

        LSFMCPDeclaration.ElementType type = LSFMCPDeclaration.ElementType.fromString(typeStr);
        Set<LSFMCPDeclaration> byName = getStatementsFromName(project, scope, type, name);
        if (!byName.isEmpty()) return byName;

        return Collections.emptyList();
    }

    // Expected format: <module>(<line>:<symbolInLine>) e.g. MyModule(10:5)
    // line and symbolInLine are 1-based
    private static LSFMCPDeclaration getStatementByLocation(Project project, GlobalSearchScope scope, String location) {
        if (location == null) return null;
        String s = location.trim();
        if (s.isEmpty()) return null;

        int lParen = s.lastIndexOf('(');
        int rParen = s.endsWith(")") ? s.length() - 1 : -1;
        if (lParen <= 0 || rParen <= lParen) return null;

        String module = s.substring(0, lParen);

        String inside = s.substring(lParen + 1, rParen);
        int colon = inside.indexOf(':');
        if (colon <= 0 || colon >= inside.length() - 1) return null;

        int line0;
        int col0;
        try {
            int line1 = Integer.parseInt(inside.substring(0, colon));
            int col1 = Integer.parseInt(inside.substring(colon + 1));
            if (line1 <= 0 || col1 <= 0) return null;
            line0 = line1 - 1;
            col0 = col1 - 1;
        } catch (NumberFormatException ignore) {
            return null;
        }

        // Find the module file, then the position in the document by the given line+column
        Collection<LSFModuleDeclaration> decls = LSFGlobalResolver.findModules(module, project, scope);
        for (LSFModuleDeclaration md : decls) {
            LSFFile file = md.getLSFFile();
            if (file == null) continue;
            Document doc = file.getViewProvider().getDocument();
            if (doc == null) continue;
            int line = line0;
            if (line >= doc.getLineCount() || line < 0) continue;

            int lineStart = doc.getLineStartOffset(line);
            int lineEnd = doc.getLineEndOffset(line);

            int offset = lineStart + Math.max(0, col0);
            if (offset >= lineEnd) offset = lineStart;

            // Find the first non-whitespace element within the line
            PsiElement el = file.findElementAt(offset);
            while (offset < lineEnd && (el == null || el instanceof com.intellij.psi.PsiWhiteSpace)) {
                offset++;
                el = file.findElementAt(offset);
            }

            if (el == null) continue;

            LSFMCPDeclaration st = getStatement(el);
            if (st == null) continue;

            return st;
        }
        return null;
    }

    private static String getLocationByStatement(LSFMCPStatement stmt) {
        if (stmt == null) return null;

        Pair<LSFFile, TextRange> location = LSFMCPStatement.getLocation(stmt);
        LSFFile file = location.first;
        if (file == null) return null;

        LSFModuleDeclaration md = file.getModuleDeclaration();
        if (md == null) return null;

        Document doc = file.getViewProvider().getDocument();
        if (doc == null) return null;

        int startOffset = location.second.getStartOffset();
        int line0 = doc.getLineNumber(startOffset);
        int lineStart = doc.getLineStartOffset(line0);
        int col0 = Math.max(0, startOffset - lineStart);
        return md.getDeclName() + "(" + (line0 + 1) + ":" + (col0 + 1) + ")";
    }

    private static Collection<LSFClassDeclaration> resolveClassesByName(Project project, GlobalSearchScope scope, String canonicalName) {
        return (Collection<LSFClassDeclaration>) getElementsFromName(project, scope, LSFMCPDeclaration.ElementType.CLASS, canonicalName);
    }


    private static void handleError(Throwable t, String logMessage, List<String> errors) {
        LOG.error(logMessage, t);
        errors.add(t.getMessage() != null ? t.getMessage() : t.toString());
    }

    private static @NotNull JSONObject getJsonFromStatement(LSFMCPDeclaration stmt,
                                                            GlobalSearchScope searchScope) {
        return getJsonFromStatement(new SelectedStatement(stmt, searchScope), MCPSearchUtils.FULL_TEXT_SHORT_CODE_FACTOR);
    }

    private static @NotNull JSONObject getJsonFromStatement(SelectedStatement selSt,
                                                            int shortCodeFactor) {
        JSONObject o = new JSONObject();
        LSFMCPDeclaration.ElementType t = selSt.decl.getMCPType();
        if (t != null)
            o.put("type", t.apiName);

        Collection<LSFGlobalDeclaration<?, ?>> nameDeclarations = LSFMCPDeclaration.getNameDeclarations(selSt.decl);
        Iterator<LSFGlobalDeclaration<?, ?>> nameDeclarationIterator = nameDeclarations.iterator();
        if (nameDeclarationIterator.hasNext()) {
            o.put("name", nameDeclarationIterator.next().getCanonicalName());

            if (nameDeclarationIterator.hasNext()) {
                JSONArray extendArray = new JSONArray();
                while (nameDeclarationIterator.hasNext())
                    extendArray.put(nameDeclarationIterator.next().getCanonicalName());
                o.put("moreNames", extendArray);
            }
        }

        fillJsonCodeFromStatement(selSt.withExtends.get(0), t, shortCodeFactor, o);
        if (selSt.withExtends.size() > 1) {
            JSONArray extendArray = new JSONArray();
            for (int i = 1; i < selSt.withExtends.size(); i++) {
                JSONObject co = new JSONObject();
                fillJsonCodeFromStatement(selSt.withExtends.get(i), t, shortCodeFactor, co);
                extendArray.put(co);
            }
            o.put("extends", extendArray);
        }
        return o;
    }

    private static void fillJsonCodeFromStatement(LSFMCPStatement stmt, LSFMCPDeclaration.ElementType t, int shortCodeFactor, JSONObject co) {
        // location and code
        String location = getLocationByStatement(stmt);
        if (location != null)
            co.put("location", location);
        co.put("code", getShortCode(t, stmt, shortCodeFactor));

        JSONArray metacodeStack = LSFMCPStatement.getMetacodeStack(stmt);
        if (metacodeStack != null)
            co.put("metacodeStack", metacodeStack);
    }

    private static String getShortCode(LSFMCPDeclaration.ElementType t, @NotNull LSFMCPStatement stmt, int shortCodeFactor) {
        return LSFPsiImplUtil.getTextWithCutRules(stmt, scaleShortCodeRules(t.shortCodeRules, shortCodeFactor));
    }

    private static @NotNull LSFPsiImplUtil.TextCutRules scaleShortCodeRules(@NotNull LSFPsiImplUtil.TextCutRules base, int factor) {
        if(factor == 1) return base;

        LSFPsiImplUtil.TextCutRules.CutRule[] scaled = new LSFPsiImplUtil.TextCutRules.CutRule[base.cutRules.length];
        for (int i = 0; i < base.cutRules.length; i++) {
            LSFPsiImplUtil.TextCutRules.CutRule r = base.cutRules[i];
            int keepPrefixLength = r.keepPrefixLength;

            // NOTE: we intentionally use saturation here to avoid integer overflow.
            // Without it, e.g. 120 * 1_000_000_000 overflows int and may turn into a negative prefix,
            // which effectively becomes "cut the whole element".
            long basePrefix = (keepPrefixLength == 0 ? 1L : (long) keepPrefixLength);
            long scaledPrefix = basePrefix * (long) factor;
            int safePrefix = scaledPrefix >= FULL_TEXT_SHORT_CODE_FACTOR ? FULL_TEXT_SHORT_CODE_FACTOR : (int) scaledPrefix;

            scaled[i] = new LSFPsiImplUtil.TextCutRules.CutRule(r.ruleClass, safePrefix);
        }
        return new LSFPsiImplUtil.TextCutRules(scaled);
    }

    private static Set<LSFMCPDeclaration.ElementType> parseElementTypes(@Nullable String elementTypesCsv) {
        if (elementTypesCsv.isEmpty()) return Collections.emptySet();
        Set<LSFMCPDeclaration.ElementType> res = EnumSet.noneOf(LSFMCPDeclaration.ElementType.class);

        for (String s : parseCsv(elementTypesCsv)) {
            LSFMCPDeclaration.ElementType t = LSFMCPDeclaration.ElementType.fromString(s);
            if (t != null) res.add(t);
        }
        return res;
    }

    // endregion

    // region Scope by modules

    private static GlobalSearchScope buildScope(@NotNull Project project, @Nullable String scopeMode) {
        if (scopeMode == null || scopeMode.isEmpty() || "project and libraries".equals(scopeMode)) {
            return ProjectScope.getAllScope(project);
        }
        if ("project".equals(scopeMode)) {
            return ProjectScope.getProjectScope(project);
        }

        GlobalSearchScope acc = null;
        ModuleManager moduleManager = ModuleManager.getInstance(project);
        for (String moduleName : parseCsv(scopeMode)) {
            Module module = moduleManager.findModuleByName(moduleName.trim());
            if (module != null) {
                GlobalSearchScope moduleScope = module.getModuleContentScope();
                acc = (acc == null) ? moduleScope : acc.union(moduleScope);
            }
        }
        return acc != null ? acc : GlobalSearchScope.EMPTY_SCOPE;
    }

    private static GlobalSearchScope buildModulesFilter(@NotNull Project project, @NotNull String modules, boolean requiredModules, @NotNull GlobalSearchScope baseScope) {
        GlobalSearchScope acc = null;
        for (String moduleName : parseCsv(modules)) {
            // We search for modules within the baseScope (which respects the 'scope' parameter)
            Collection<LSFModuleDeclaration> decls = LSFGlobalResolver.findModules(moduleName, project, baseScope);
            for (LSFModuleDeclaration md : decls) {
                LSFFile file = md.getLSFFile();

                GlobalSearchScope scope;
                if (!requiredModules) {
                    VirtualFile vf = file.getVirtualFile();
                    if (vf == null) continue;
                    scope = GlobalSearchScope.fileScope(project, vf);
                } else {
                    scope = file.getRequireScope();
                    if (scope == null) continue;
                }

                acc = acc == null ? scope : acc.union(scope);
            }
        }

        return acc == null ? GlobalSearchScope.EMPTY_SCOPE : acc;
    }

    private static GlobalSearchScope buildSearchScope(Project project, String modules, String scopeMode, boolean requiredModules) {
        GlobalSearchScope baseScope = buildScope(project, scopeMode);
        if (modules == null || modules.isEmpty()) {
            return baseScope;
        }
        GlobalSearchScope modulesFilter = buildModulesFilter(project, modules, requiredModules, baseScope);
        return baseScope.intersectWith(modulesFilter);
    }

    // endregion

    // region Matching

    private static boolean matchesAllFilters(LSFMCPDeclaration stmt,
                                             List<NameFilter> queryFilters,
                                             Set<LSFMCPDeclaration.ElementType> elementTypes,
                                             Set<LSFClassDeclaration> classDecls,
                                             Map<LSFMCPDeclaration, Direction> related,
                                             GlobalSearchScope scope,
                                             ConcurrentMap<RelatedKey, RelatedState> relatedCache) {
        LSFMCPDeclaration.ElementType t;
        return (elementTypes.isEmpty() || ((t = stmt.getMCPType()) != null && elementTypes.contains(t))) &&
                (queryFilters.isEmpty() || matchesQueryFilters(stmt, queryFilters, scope)) &&
                (classDecls.isEmpty() || matchesClassesFilter(stmt, classDecls, scope)) &&
                (related.isEmpty() || matchesRelatedFilters(stmt, related, scope, relatedCache));
    }

    private static boolean matchesQueryFilters(LSFMCPDeclaration stmt, List<NameFilter> queryFilters, GlobalSearchScope scope) {
        return matchesNameFilters(stmt, queryFilters, scope, false) ||
                matchesNameFilters(stmt, queryFilters, scope, true);
    }

    private static boolean matchesClassesFilter(LSFMCPDeclaration stmt, Set<LSFClassDeclaration> classDecls, GlobalSearchScope scope) {
        if (classDecls.isEmpty()) return true;
        // Resolve candidate declarations (can be several)

        boolean hasClasses = false;
        List<LSFClassSet> paramClasses = new ArrayList<>();
        for (LSFGlobalDeclaration<?, ?> d : LSFMCPDeclaration.getNameDeclarations(stmt)) {
            if (d instanceof LSFClassDeclaration) {
                paramClasses.add(new CustomClassSet(((LSFClassDeclaration) d)));
                hasClasses = true;
            } else if (d instanceof LSFActionOrGlobalPropDeclaration) {
                List<LSFClassSet> resolve = ((LSFActionOrGlobalPropDeclaration<?, ?>) d).resolveParamClasses();
                if(resolve != null)
                    paramClasses.addAll(resolve);
                hasClasses = true;
            }
        }
        if(hasClasses) {
            for (LSFClassDeclaration target : classDecls) {
                LSFClassSet upSet = target.getUpSet();
                for (LSFClassSet paramClass : paramClasses) {
                    if (paramClass != null && paramClass.containsAll(upSet, true)) {
                        return true;
                    }
                }
            }
            return false;
        }

        for(LSFMCPDeclaration useStmt : nextUses(stmt, scope)) {
            for(LSFGlobalDeclaration<?, ?> use : LSFMCPDeclaration.getNameDeclarations(useStmt))
                if (use instanceof LSFClassDeclaration && classDecls.contains(use))
                    return true;
        }
        return false;
    }

    enum RelatedState {
        IN_PROGRESS,
        TRUE,
        FALSE
    }

    record RelatedKey(LSFMCPDeclaration stmt, Direction dir) {}

    private static boolean matchesRelatedFilters(LSFMCPDeclaration stmt,
                                                 Map<LSFMCPDeclaration, Direction> related,
                                                 GlobalSearchScope scope,
                                                 ConcurrentMap<RelatedKey, RelatedState> memo) {
        if (related.isEmpty()) return true;

        class Rec {
            boolean memoized(RelatedKey key, java.util.function.Supplier<Boolean> calc) {
                var state = memo.get(key);
                if (state != null) {
                    return state == RelatedState.TRUE;
                }
                memo.put(key, RelatedState.IN_PROGRESS);
                boolean result = calc.get();
                memo.put(key, result ? RelatedState.TRUE : RelatedState.FALSE);
                return result;
            }

            boolean dfs(java.util.Collection<LSFMCPDeclaration> cur, Direction dir) {
                for (var c : cur) {
                    if (c == null) continue;
                    if (memoized(new RelatedKey(c, dir), () -> {
                        Direction relatedDir = related.get(c);
                        if (relatedDir != null && (relatedDir == Direction.BOTH || relatedDir != dir)) return true;

                        return dfs((dir == Direction.USES)
                                ? nextUses(c, scope)
                                : nextUsed(c, scope), dir);
                    })) {
                        return true;
                    }
                }
                return false;
            }
        }

        var rec = new Rec();
        return rec.dfs(Collections.singletonList(stmt), Direction.USES) || rec.dfs(Collections.singletonList(stmt), Direction.USED);
    }

    private static boolean processStatement(LSFFullNameDeclaration element, Processor<LSFMCPDeclaration> out) {
        return out.process(getStatement(element));
    }
    private static boolean processStatement(LSFGlobalDeclaration element, Processor<LSFMCPDeclaration> out) {
        return out.process(getStatement(element));
    }
    private static boolean processStatement(PsiElement element, Processor<LSFMCPDeclaration> out) {
        return out.process(getStatement(element));
    }

    private static LSFMCPDeclaration getStatement(LSFFullNameDeclaration decl) {
        return LSFMCPDeclaration.getMCPDeclaration(decl);
    }
    private static LSFMCPDeclaration getStatement(LSFGlobalDeclaration decl) {
        return LSFMCPDeclaration.getMCPDeclaration(decl);
    }
    private static LSFMCPDeclaration getStatement(PsiElement element) {
        return ReadAction.compute(() -> {
            LSFExtend extend = PsiTreeUtil.getParentOfType(element, LSFExtend.class, false);
            if(extend != null) {
                LSFFullNameDeclaration extendDependent = extend.resolveExtendingDeclaration();
                if(extendDependent != null)
                    return getStatement(extendDependent);
            }
            return LSFMCPDeclaration.getMCPDeclaration(element);
        });
    }
    // Unified provider for a "mega element": the declaration itself + all extends/implementations
    private static List<LSFMCPStatement> getElementWithExtends(LSFMCPDeclaration stmt, GlobalSearchScope scope) {
        // assert is not extend
        // First element is the declaration itself; following are extends/implementations
        List<LSFMCPStatement> result = new ArrayList<>();
        result.add(stmt);
        for(LSFGlobalDeclaration<?, ?> gdecl : LSFMCPDeclaration.getNameDeclarations(stmt)) {
            if(gdecl instanceof LSFFullNameDeclaration decl) {
                Query<LSFExtend> extendElements = BaseUtils.immutableCast(LSFGlobalResolver.findExtendElements(decl, decl.getProject(), scope, LSFLocalSearchScope.GLOBAL));

                if (decl instanceof LSFFormDeclaration) // adding forms design
                    extendElements = new MergeQuery<>(extendElements, BaseUtils.immutableCast(LSFGlobalResolver.findExtendElements(decl, decl.getProject(), scope, LSFStubElementTypes.DESIGN, LSFLocalSearchScope.GLOBAL)));

                for (LSFExtend extend : extendElements.findAll()) {
                    if (extend != stmt)
                        result.add(extend);
                }
            }
        }
        return result;
    }
    private static List<String> getElementCodeWithExtends(LSFMCPDeclaration stmt, GlobalSearchScope scope) {
        // assert is not extend
        List<String> result = new ArrayList<>();
        for(LSFMCPStatement exStmt: getElementWithExtends(stmt, scope))
            result.add(LSFMCPStatement.getCode(exStmt));
        return result;
    }

    // endregion

    private static Collection<LSFMCPDeclaration> nextUses(LSFMCPDeclaration cur, GlobalSearchScope scope) {
        // Forward traversal: collect all full-name references inside a "mega element"
        // which includes the declaration itself and all its extends/implementations.
        List<LSFMCPDeclaration> out = new ArrayList<>();
        // Iterate over the unified mega elements (self + extends/implementations)
        for (LSFMCPStatement el : getElementWithExtends(cur, scope)) {
            for (LSFGlobalReference<?> ref : LSFMCPStatement.getNameReferences(el)) {
                LSFDeclaration resolved = ref.resolveDecl();
                if (resolved instanceof LSFGlobalDeclaration<?,?>) {
                    LSFMCPDeclaration st = getStatement((LSFGlobalDeclaration) resolved);
                    if (st != null) out.add(st);
                }
            }
        }

        return out;
    }

    private static Collection<LSFMCPDeclaration> nextUsed(LSFMCPDeclaration stmt,
                                                          GlobalSearchScope scope) {
        // Inline reverse traversal analogous to LSFActionOrGlobalPropDeclaration.getDependents()
        Set<LSFMCPDeclaration> result = new HashSet<>();
        for(LSFGlobalDeclaration<?, ?> cur : LSFMCPDeclaration.getNameDeclarations(stmt)) {
            for(PsiReference reference : LSFMCPStatement.getNameReferenced(cur, scope)) {
                LSFMCPDeclaration dependent = getStatement(reference.getElement());
                if (dependent != null)
                    result.add(dependent);
            };
        }
        return result;
    }

    // Unified streaming traversal with global visited marks
    private static void streamRelated(LSFMCPDeclaration start,
                                      Direction dir,
                                      GlobalSearchScope scope,
                                      Processor<LSFMCPDeclaration> out,
                                      Set<LSFMCPDeclaration> visitedRelated) {
        Deque<LSFMCPDeclaration> dq = new ArrayDeque<>();
        dq.add(start);
        while (!dq.isEmpty()) {
            LSFMCPDeclaration cur = dq.removeFirst();
            if (!visitedRelated.add(cur)) continue; // global across all related traversals
            // emit enclosing statement
            if (!out.process(cur)) return;
            // enqueue neighbors according to direction
            if (dir == Direction.USES || dir == Direction.BOTH) {
                for (LSFMCPDeclaration n : nextUses(cur, scope)) dq.add(n);
            }
            if (dir == Direction.USED || dir == Direction.BOTH) {
                for (LSFMCPDeclaration n : nextUsed(cur, scope)) dq.add(n);
            }
        }
    }

    private static boolean matchesNameFilters(LSFMCPDeclaration stmt, List<NameFilter> nameFilters, GlobalSearchScope scope, boolean inCode) {
        List<String> names = null;
        List<String> code = null;
        for (NameFilter f : nameFilters) {
            List<String> hays;
            if(inCode) {
                if(code == null)
                    code = getElementCodeWithExtends(stmt, scope);
                hays = code;
            } else {
                if(names == null) {
                    names = ReadAction.compute(() -> {
                        List<String> result = new ArrayList<>();
                        Collection<LSFGlobalDeclaration<?, ?>> decls = LSFMCPDeclaration.getNameDeclarations(stmt);
                        for( LSFGlobalDeclaration<?, ?> d : decls) {
                            result.add(d.getDeclName());
                        }
                        return result;
                    });
                }
                hays = names;
            }
            for(String hay : hays)
                if ((f.word == null || hay.toLowerCase(Locale.ROOT).contains(f.word.toLowerCase(Locale.ROOT))) &&
                    (f.regex == null || f.regex.matcher(hay).find()))
                    return true;
        }
        return false;
    }

    // endregion

    // region Types mapping

    private static class NameFilter {
        final String word;
        final Pattern regex;

        NameFilter(String word, Pattern regex) {
            this.word = word;
            this.regex = regex;
        }
    }

    // endregion

    private static int getPriorityLimit(Set<LSFMCPDeclaration.ElementType> elementTypes) {
        if (elementTypes.isEmpty()) return 5;
        int limit = 0;
        for (LSFMCPDeclaration.ElementType et : elementTypes) {
            limit = Math.max(limit, getPriority(et));
        }
        return limit;
    }

    private static ExecutorService createPriorityExecutor(int threads) {
        return new ThreadPoolExecutor(threads, threads,
                0L, TimeUnit.MILLISECONDS,
                new PriorityBlockingQueue<>(),
                new ThreadFactory() {
                    private final AtomicInteger count = new AtomicInteger(0);
                    @Override
                    public Thread newThread(@NotNull Runnable r) {
                        Thread t = new Thread(r, "MCP-Search-" + count.getAndIncrement());
                        t.setDaemon(true);
                        return t;
                    }
                });
    }

    private static class PriorityTask implements Runnable, Comparable<PriorityTask> {
        private static final AtomicLong seqSource = new AtomicLong(0);
        private final int priority;
        private final long seq = seqSource.getAndIncrement();
        private final Runnable runnable;

        PriorityTask(int priority, Runnable runnable) {
            this.priority = priority;
            this.runnable = runnable;
        }

        @Override
        public void run() {
            runnable.run();
        }

        @Override
        public int compareTo(@NotNull PriorityTask o) {
            int res = Integer.compare(this.priority, o.priority);
            if (res == 0) return Long.compare(this.seq, o.seq);
            return res;
        }
    }

    private static class SearchState {
        final AtomicBoolean stopRequested;
        final AtomicInteger stopPriorityThreshold = new AtomicInteger(5);

        final AtomicInteger[] symbolsByPriority = new AtomicInteger[6];
        final AtomicInteger[] pendingTasksByPriority = new AtomicInteger[6];

        final List<SelectedStatement>[] statementsByPriority = new List[6];

        final int maxSymbols;

        SearchState(int maxSymbols) {
            this.maxSymbols = maxSymbols;
            this.stopRequested = new AtomicBoolean(false);
            this.stopPriorityThreshold.set(5);
            for (int i = 0; i < 6; i++) {
                symbolsByPriority[i] = new AtomicInteger(0);
                pendingTasksByPriority[i] = new AtomicInteger(0);
                statementsByPriority[i] = new ArrayList<>();
            }
        }

        void updateThreshold() {
            int total = 0;
            for (int p = 1; p <= 5; p++) {
                total += symbolsByPriority[p].get();
                if (total >= maxSymbols) {
                    int current = stopPriorityThreshold.get();
                    if (p < current) {
                        stopPriorityThreshold.compareAndSet(current, p);
                        checkAllPBetterDone();
                    }
                    break;
                }
            }
        }

        private void checkAllPBetterDone() {
            int p = stopPriorityThreshold.get();

            boolean allPBetterDone = true;
            for (int i = 1; i <= p; i++) {
                if (pendingTasksByPriority[i].get() > 0) {
                    allPBetterDone = false;
                    break;
                }
            }
            if (allPBetterDone) {
                stopRequested.set(true);
            }
        }
    }

    @FunctionalInterface
    private interface TaskSubmitter {
        void submit(int priority, Runnable runnable);
    }
}

