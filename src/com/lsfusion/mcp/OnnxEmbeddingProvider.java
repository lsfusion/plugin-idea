package com.lsfusion.mcp;

import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OnnxValue;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * ONNX Runtime embedding provider using e5-small.
 * Model directory is expected to contain:
 * - model.onnx
 * - tokenizer.json (HuggingFace tokenizer)
 */
public final class OnnxEmbeddingProvider implements EmbeddingProvider {
    private static final Logger LOG = Logger.getInstance(OnnxEmbeddingProvider.class);

    private final OrtEnvironment env;
    private final OrtSession session;
    private final HuggingFaceTokenizer tokenizer;
    private final int dimension;
    private final String inputIdsName;
    private final String attentionMaskName;
    private final String tokenTypeIdsName;
    private final String outputName;

    public OnnxEmbeddingProvider(@NotNull Path modelDir) throws Exception {
        Path modelPath = modelDir.resolve("model.onnx");
        Path tokenizerPath = modelDir.resolve("tokenizer.json");
        if (!Files.exists(modelPath) || !Files.exists(tokenizerPath)) {
            throw new IllegalStateException("Missing model.onnx or tokenizer.json in " + modelDir);
        }

        this.env = OrtEnvironment.getEnvironment();
        this.session = env.createSession(modelPath.toString(), new OrtSession.SessionOptions());
        this.tokenizer = HuggingFaceTokenizer.newInstance(tokenizerPath);

        this.inputIdsName = pickInputName("input_ids");
        this.attentionMaskName = pickInputName("attention_mask");
        this.tokenTypeIdsName = pickInputName("token_type_ids");
        this.outputName = pickOutputName();
        this.dimension = inferDimension();
    }

    @Override
    public float[] embed(String text) throws Exception {
        var encoding = tokenizer.encode(text);
        long[] inputIds = encoding.getIds();
        long[] attentionMask = encoding.getAttentionMask();
        long[] tokenTypeIds = encoding.getTypeIds();

        if (tokenTypeIds == null || tokenTypeIds.length == 0) {
            tokenTypeIds = new long[inputIds.length];
        }

        long[] shape = new long[]{1, inputIds.length};
        try (OnnxTensor idsTensor = OnnxTensor.createTensor(env, wrapLong(inputIds), shape);
             OnnxTensor maskTensor = OnnxTensor.createTensor(env, wrapLong(attentionMask), shape);
             OnnxTensor typeTensor = OnnxTensor.createTensor(env, wrapLong(tokenTypeIds), shape)) {

            Map<String, OnnxTensor> inputs = new HashMap<>();
            if (inputIdsName != null) inputs.put(inputIdsName, idsTensor);
            if (attentionMaskName != null) inputs.put(attentionMaskName, maskTensor);
            if (tokenTypeIdsName != null) inputs.put(tokenTypeIdsName, typeTensor);

            try (OrtSession.Result result = session.run(inputs)) {
                OnnxValue value = result.get(outputName);
                float[] vector = extractEmbedding(value, attentionMask);
                normalize(vector);
                return vector;
            }
        }
    }

    @Override
    public int dimension() {
        return dimension;
    }

    @Override
    public void close() throws Exception {
        session.close();
        env.close();
    }

    private String pickInputName(String preferred) {
        if (session.getInputNames().contains(preferred)) {
            return preferred;
        }
        return session.getInputNames().stream().findFirst().orElse(null);
    }

    private String pickOutputName() {
        if (session.getOutputNames().contains("sentence_embedding")) {
            return "sentence_embedding";
        }
        return session.getOutputNames().stream().findFirst().orElseThrow();
    }

    private int inferDimension() throws OrtException {
        var outputInfo = session.getOutputInfo().get(outputName).getInfo();
        if (outputInfo instanceof ai.onnxruntime.TensorInfo ti) {
            long[] shape = ti.getShape();
            if (shape.length == 2 && shape[1] > 0) {
                return (int) shape[1];
            }
            if (shape.length == 3 && shape[2] > 0) {
                return (int) shape[2];
            }
        }
        return 384; // fallback for e5-small
    }

    private static long[][] wrapLong(long[] input) {
        long[][] out = new long[1][input.length];
        System.arraycopy(input, 0, out[0], 0, input.length);
        return out;
    }

    private float[] extractEmbedding(OnnxValue value, long[] attentionMask) throws OrtException {
        Object raw = value.getValue();
        if (raw instanceof float[][] vec2d) {
            return vec2d[0];
        }
        if (raw instanceof float[][][] vec3d) {
            return meanPool(vec3d[0], attentionMask);
        }
        LOG.warn("Unexpected ONNX output type: " + raw.getClass().getName());
        return new float[dimension];
    }

    private static float[] meanPool(float[][] tokenEmbeds, long[] attentionMask) {
        int dim = tokenEmbeds[0].length;
        float[] out = new float[dim];
        float denom = 0f;
        for (int i = 0; i < tokenEmbeds.length; i++) {
            float mask = (attentionMask != null && i < attentionMask.length) ? attentionMask[i] : 1f;
            if (mask <= 0) continue;
            denom += mask;
            float[] t = tokenEmbeds[i];
            for (int d = 0; d < dim; d++) {
                out[d] += t[d] * mask;
            }
        }
        if (denom > 0) {
            for (int d = 0; d < dim; d++) out[d] /= denom;
        }
        return out;
    }

    private static void normalize(float[] v) {
        double sum = 0;
        for (float x : v) sum += x * x;
        double norm = Math.sqrt(sum);
        if (norm == 0) return;
        for (int i = 0; i < v.length; i++) v[i] /= norm;
    }
}
