package org.example.contribute_for_springai;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.AbstractEmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.Assert;

/**
 * ClassName:MyOpenAiEmbeddingModel
 * Package:org.example.contribute_for_springai
 * Description:
 *
 * @Date:2025/1/26 15:20
 * @Author:qs@1.com
 */
public class MyZhipuAiEmbeddingModel extends MyAbstractEmbeddingModel {

    private final OpenAiEmbeddingOptions defaultOptions;

    public MyZhipuAiEmbeddingModel(OpenAiEmbeddingOptions options) {

        Assert.notNull(options, "options must not be null");

        this.defaultOptions = options;

        /**
         * yaml 中配置了 dimensions
         * 每个 EmbeddingModel 都去设置 embeddingDimensions
         * pull request for spring ai
         */
        if (options.getDimensions() != null && options.getDimensions() > 0) {
            this.embeddingDimensions.set(options.getDimensions());
        }
    }

}
