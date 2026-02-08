package com.lsfusion.mcp;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.ProjectScope;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.util.LSFFileUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

public final class LocalMcpRagService {
    private static final Logger LOG = Logger.getInstance(LocalMcpRagService.class);
    private static final Key<LocalMcpRagService> KEY = Key.create("lsfusion.mcp.localRagService");
    private static final String FIELD_ID = "elementId";
    private static final String FIELD_FILE = "filePath";
    private static final String FIELD_MODULE = "module";
    private static final String FIELD_TYPE = "type";
    private static final String FIELD_CODE = "code";
    private static final String FIELD_VECTOR = "vector";

    private final Project project;
    private final Directory directory;
    private final IndexWriter writer;
    private final EmbeddingProvider embeddingProvider;
    private final AtomicBoolean indexing = new AtomicBoolean(false);
    private static final String MODEL_DIR_PROPERTY = "lsfusion.mcp.embedding.modelDir";
    private static final String MODEL_DIR_ENV = "LSFUSION_MCP_EMBEDDING_MODEL_DIR";
    private static final String DEFAULT_MODEL_DIR_NAME = ".mcp-model";
    private static final AtomicBoolean ONNX_SELF_TEST_DONE = new AtomicBoolean(false);

    private LocalMcpRagService(Project project) throws Exception {
        this.project = project;
        Path base = Path.of(project.getBasePath());
        Path indexPath = base.resolve(".mcp-index");
        Files.createDirectories(indexPath);
        this.directory = FSDirectory.open(indexPath);
        this.writer = new IndexWriter(directory, new IndexWriterConfig(new StandardAnalyzer()));
        this.embeddingProvider = createEmbeddingProvider();
    }

    public static @NotNull LocalMcpRagService getInstance(@NotNull Project project) throws Exception {
        LocalMcpRagService service = project.getUserData(KEY);
        if (service != null) return service;
        service = new LocalMcpRagService(project);
        project.putUserData(KEY, service);
        return service;
    }

    public void indexProjectAsync() {
        if (indexing.getAndSet(true)) return;
        if (com.intellij.openapi.project.DumbService.isDumb(project)) {
            com.intellij.openapi.project.DumbService.getInstance(project)
                    .runWhenSmart(this::indexProjectAsync);
            indexing.set(false);
            return;
        }
        com.intellij.openapi.application.ApplicationManager.getApplication().executeOnPooledThread(() -> {
            long start = System.currentTimeMillis();
            try {
                if (embeddingProvider == null) {
                    LOG.warn("MCP RAG index skipped: embedding provider not available");
                    return;
                }
                LOG.info("MCP RAG index started");
                indexProject();
                LOG.info("MCP RAG index built in " + (System.currentTimeMillis() - start) + "ms");
            } catch (Throwable t) {
                LOG.warn("MCP RAG index build failed", t);
            } finally {
                indexing.set(false);
            }
        });
    }

    public void indexProject() throws Exception {
        if (embeddingProvider == null) return;
        if (com.intellij.openapi.project.DumbService.isDumb(project)) {
            LOG.info("MCP RAG index skipped: IDE is in dumb mode");
            return;
        }
        ReadAction.run(() -> {
            int fileCount = 0;
            for (LSFFile lsfFile : LSFFileUtils.getLsfFiles(ProjectScope.getAllScope(project))) {
                indexFile(lsfFile);
                fileCount++;
            }
            LOG.info("MCP RAG index pass completed: " + fileCount + " files");
        });
        writer.commit();
    }

    public void updateFile(@NotNull VirtualFile file) {
        if (!file.getName().endsWith(".lsf")) return;
        if (embeddingProvider == null) return;
        if (com.intellij.openapi.project.DumbService.isDumb(project)) return;
        try {
            long start = System.currentTimeMillis();
            LOG.info("MCP RAG reindex start: " + file.getPath());
            ReadAction.run(() -> {
                PsiFile psi = PsiManager.getInstance(project).findFile(file);
                if (psi instanceof LSFFile lsfFile) {
                    indexFile(lsfFile);
                }
            });
            writer.commit();
            LOG.info("MCP RAG reindex done: " + file.getPath() + " in " + (System.currentTimeMillis() - start) + "ms");
        } catch (Throwable t) {
            LOG.warn("MCP RAG updateFile failed: " + file.getPath(), t);
        }
    }

    public void deleteFile(@NotNull VirtualFile file) {
        if (!file.getName().endsWith(".lsf")) return;
        try {
            writer.deleteDocuments(new Term(FIELD_FILE, file.getPath()));
            writer.commit();
        } catch (Throwable t) {
            LOG.warn("MCP RAG deleteFile failed: " + file.getPath(), t);
        }
    }

    public @NotNull List<String> search(@NotNull String queryText, int topK) {
        if (queryText.isBlank()) return List.of();
        if (embeddingProvider == null) return List.of();
        try {
            long start = System.currentTimeMillis();
            long vectorStart = System.currentTimeMillis();
            LOG.info("MCP RAG query vectorization start: length=" + queryText.length());
            float[] vector = embeddingProvider.embed(queryText);
            LOG.info("MCP RAG query vectorization done: dim=" + vector.length +
                    ", " + (System.currentTimeMillis() - vectorStart) + "ms");
            if (vector.length == 0) return List.of();
            try (IndexReader reader = DirectoryReader.open(writer)) {
                IndexSearcher searcher = new IndexSearcher(reader);
                TopDocs hits = searcher.search(new MatchAllDocsQuery(), Math.max(1, reader.numDocs()));
                List<String> out = new ArrayList<>();
                List<ScoredId> scored = new ArrayList<>(hits.scoreDocs.length);
                for (ScoreDoc hit : hits.scoreDocs) {
                    Document doc = searcher.storedFields().document(hit.doc);
                    String location = doc.get(FIELD_ID);
                    BytesRef br = doc.getBinaryValue(FIELD_VECTOR);
                    byte[] raw = br != null ? Arrays.copyOfRange(br.bytes, br.offset, br.offset + br.length) : null;
                    if (location == null || raw == null) continue;
                    float[] vec = decodeVector(raw);
                    float score = dot(vector, vec);
                    scored.add(new ScoredId(location, score));
                }
                scored.sort((a, b) -> Float.compare(b.score, a.score));
                for (int i = 0; i < Math.min(topK, scored.size()); i++) {
                    out.add(scored.get(i).id);
                }
                LOG.info("MCP RAG search: " + out.size() + " hits in " + (System.currentTimeMillis() - start) + "ms");
                return out;
            }
        } catch (Throwable t) {
            LOG.warn("MCP RAG search failed", t);
            return List.of();
        }
    }

    private void indexFile(@NotNull LSFFile file) {
        VirtualFile vf = file.getVirtualFile();
        if (vf == null) return;
        if (embeddingProvider == null) return;
        try {
            long start = System.currentTimeMillis();
            LOG.info("MCP RAG indexFile start: " + vf.getPath());
            writer.deleteDocuments(new Term(FIELD_FILE, vf.getPath()));
            Collection<LSFMCPDeclaration> decls = LSFMCPDeclaration.getMCPDeclarations(file);
            for (LSFMCPDeclaration decl : decls) {
                indexStatement(file, decl);
            }
            LOG.info("MCP RAG indexFile done: " + vf.getPath() + " (" + decls.size() + " items) in " +
                    (System.currentTimeMillis() - start) + "ms");
        } catch (Throwable t) {
            LOG.warn("MCP RAG indexFile failed: " + vf.getPath(), t);
        }
    }

    private void indexStatement(@NotNull LSFFile file, @NotNull LSFMCPDeclaration decl) throws Exception {
        String location = MCPSearchUtils.getLocationByStatement(decl);
        if (location == null) return;
        float[] vector = embeddingProvider.embed(buildText(decl));
        if (vector.length == 0) return;
        Document doc = new Document();
        doc.add(new StringField(FIELD_ID, location, Field.Store.YES));
        doc.add(new StringField(FIELD_FILE, file.getVirtualFile().getPath(), Field.Store.YES));
        doc.add(new StringField(FIELD_MODULE, file.getModuleDeclaration() != null ? file.getModuleDeclaration().getDeclName() : "", Field.Store.YES));
        String typeName;
        try {
            typeName = decl.getMCPType().apiName;
        } catch (Throwable t) {
            typeName = "unknown";
        }
        doc.add(new StringField(FIELD_TYPE, typeName, Field.Store.YES));
        doc.add(new TextField(FIELD_CODE, decl.getText(), Field.Store.NO));
        doc.add(new StoredField(FIELD_VECTOR, encodeVector(vector)));
        writer.addDocument(doc);
    }

    private static String buildText(LSFMCPDeclaration decl) {
        StringBuilder sb = new StringBuilder();
        sb.append(decl.getMCPType().apiName).append(' ');
        for (var d : LSFMCPDeclaration.getNameDeclarations(decl)) {
            if (d.getDeclName() != null) sb.append(d.getDeclName()).append(' ');
            if (d.getCanonicalName() != null) sb.append(d.getCanonicalName()).append(' ');
        }
        sb.append(decl.getText());
        return sb.toString();
    }

    private static byte[] encodeVector(float[] v) {
        ByteBuffer buf = ByteBuffer.allocate(v.length * 4).order(ByteOrder.LITTLE_ENDIAN);
        for (float x : v) buf.putFloat(x);
        return buf.array();
    }

    private static float[] decodeVector(byte[] raw) {
        ByteBuffer buf = ByteBuffer.wrap(raw).order(ByteOrder.LITTLE_ENDIAN);
        float[] out = new float[raw.length / 4];
        for (int i = 0; i < out.length; i++) out[i] = buf.getFloat();
        return out;
    }

    private static float dot(float[] a, float[] b) {
        int n = Math.min(a.length, b.length);
        float sum = 0f;
        for (int i = 0; i < n; i++) sum += a[i] * b[i];
        return sum;
    }

    private static final class ScoredId {
        final String id;
        final float score;

        private ScoredId(String id, float score) {
            this.id = id;
            this.score = score;
        }
    }

    private @Nullable EmbeddingProvider createEmbeddingProvider() {
        LOG.info("MCP ONNX env: os.name=" + System.getProperty("os.name") +
                ", os.arch=" + System.getProperty("os.arch") +
                ", java.version=" + System.getProperty("java.version") +
                ", java.io.tmpdir=" + System.getProperty("java.io.tmpdir") +
                ", onnxruntime.native.path=" + System.getProperty("onnxruntime.native.path") +
                ", onnxruntime.native.onnxruntime.path=" + System.getProperty("onnxruntime.native.onnxruntime.path") +
                ", onnxruntime.native.onnxruntime4j_jni.path=" + System.getProperty("onnxruntime.native.onnxruntime4j_jni.path"));
        String modelDir = System.getProperty(MODEL_DIR_PROPERTY);
        if (modelDir == null || modelDir.isBlank()) {
            modelDir = System.getenv(MODEL_DIR_ENV);
        }
        if (modelDir == null || modelDir.isBlank()) {
            String basePath = project.getBasePath();
            if (basePath != null) {
                Path fallback = Path.of(basePath).resolve(DEFAULT_MODEL_DIR_NAME);
                if (Files.isDirectory(fallback)) {
                    modelDir = fallback.toString();
                }
            }
        }
        if (modelDir == null || modelDir.isBlank()) {
            String cwd = System.getProperty("user.dir");
            if (cwd != null && !cwd.isBlank()) {
                Path fallback = Path.of(cwd).resolve(DEFAULT_MODEL_DIR_NAME);
                if (Files.isDirectory(fallback)) {
                    modelDir = fallback.toString();
                }
            }
        }

        if (modelDir == null || modelDir.isBlank()) {
            LOG.warn("MCP embedding model dir not set: set -D" + MODEL_DIR_PROPERTY +
                    " or env " + MODEL_DIR_ENV + " or place model under <project>/" + DEFAULT_MODEL_DIR_NAME +
                    " or <working dir>/" + DEFAULT_MODEL_DIR_NAME);
            return null;
        }
        try {
            LOG.info("MCP embedding model dir: " + modelDir);
            if (ONNX_SELF_TEST_DONE.compareAndSet(false, true)) {
                try {
                    ai.onnxruntime.OrtEnvironment.getEnvironment();
                    LOG.info("MCP ONNX self-test: OK");
                } catch (Throwable t) {
                    LOG.warn("MCP ONNX self-test failed: " + t.getMessage(), t);
                }
            }
            EmbeddingProvider provider = new OnnxEmbeddingProvider(Path.of(modelDir));
            LOG.info("MCP embedding provider init: OK");
            return provider;
        } catch (Throwable t) {
            String message = t.getMessage();
            if (message != null && !message.isBlank()) {
                LOG.warn("MCP embedding provider init failed: " + message);
            }
            if (t instanceof UnsatisfiedLinkError) {
                LOG.warn("MCP embedding provider init failed: native ONNX runtime could not load. " +
                        "On Windows this is usually missing MSVC runtime. " +
                        "Install Microsoft Visual C++ 2015-2022 Redistributable (x64) and restart IDE.");
            }
            LOG.warn("MCP embedding provider init failed", t);
            return null;
        }
    }
}
