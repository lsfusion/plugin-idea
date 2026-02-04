package com.lsfusion.mcp;

public interface EmbeddingProvider extends AutoCloseable {
    float[] embed(String text) throws Exception;
    int dimension();

    @Override
    default void close() throws Exception {
        // no-op by default
    }
}
