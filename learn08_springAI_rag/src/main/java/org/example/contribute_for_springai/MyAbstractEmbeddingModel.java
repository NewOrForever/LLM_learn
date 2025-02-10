package org.example.contribute_for_springai;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * ClassName:MyAbstractEmbeddingModel
 * Package:org.example.contribute_for_springai
 * Description:
 *
 * @Date:2025/1/26 15:34
 * @Author:qs@1.com
 */
public abstract class MyAbstractEmbeddingModel {
    private static Map<String, Integer> KNOWN_EMBEDDING_DIMENSIONS = loadKnownModelDimensions();

    /**
     * Default constructor.
     */
    public MyAbstractEmbeddingModel() {
    }

    /**
     * Cached embedding dimensions.
     */
    protected final AtomicInteger embeddingDimensions = new AtomicInteger(-1);


    private static Map<String, Integer> loadKnownModelDimensions() {
        try {
            Properties properties = new Properties();
            properties.load(new DefaultResourceLoader()
                    .getResource("classpath:/embedding/embedding-model-dimensions.properties")
                    .getInputStream());
            return properties.entrySet()
                    .stream()
                    .collect(Collectors.toMap(e -> e.getKey().toString(), e -> Integer.parseInt(e.getValue().toString())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected Integer getFromKnownModelDimensions(String modelName) {
        return KNOWN_EMBEDDING_DIMENSIONS.getOrDefault(modelName, -1);
    }

    public int dimensions() {
        if (this.embeddingDimensions.get() < 0) {
            this.embeddingDimensions.set(0);
        }
        return this.embeddingDimensions.get();
    }

}
