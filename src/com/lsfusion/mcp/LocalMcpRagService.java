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
        com.intellij.openapi.application.ApplicationManager.getApplication().executeOnPooledThread(() -> {
            long start = System.currentTimeMillis();
            try {
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
        ReadAction.run(() -> {
            for (LSFFile lsfFile : LSFFileUtils.getLsfFiles(ProjectScope.getAllScope(project))) {
                indexFile(lsfFile);
            }
        });
        writer.commit();
    }

    public void updateFile(@NotNull VirtualFile file) {
        if (!file.getName().endsWith(".lsf")) return;
        try {
            ReadAction.run(() -> {
                PsiFile psi = PsiManager.getInstance(project).findFile(file);
                if (psi instanceof LSFFile lsfFile) {
                    indexFile(lsfFile);
                }
            });
            writer.commit();
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
            float[] vector = embeddingProvider.embed(queryText);
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
        try {
            writer.deleteDocuments(new Term(FIELD_FILE, vf.getPath()));
            for (LSFMCPDeclaration decl : LSFMCPDeclaration.getMCPDeclarations(file)) {
                indexStatement(file, decl);
            }
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
            LOG.warn("MCP embedding model dir not set: set -D" + MODEL_DIR_PROPERTY +
                    " or env " + MODEL_DIR_ENV + " or place model under <project>/" + DEFAULT_MODEL_DIR_NAME);
            return null;
        }
        try {
            LOG.info("MCP embedding model dir: " + modelDir);
            return new OnnxEmbeddingProvider(Path.of(modelDir));
        } catch (Throwable t) {
            LOG.warn("MCP embedding provider init failed", t);
            return null;
        }
    }
}
