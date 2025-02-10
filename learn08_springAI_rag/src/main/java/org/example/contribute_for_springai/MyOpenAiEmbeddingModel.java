package org.example.contribute_for_springai;

import io.micrometer.observation.ObservationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.*;
import org.springframework.ai.embedding.observation.DefaultEmbeddingModelObservationConvention;
import org.springframework.ai.embedding.observation.EmbeddingModelObservationContext;
import org.springframework.ai.embedding.observation.EmbeddingModelObservationConvention;
import org.springframework.ai.embedding.observation.EmbeddingModelObservationDocumentation;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.common.OpenAiApiConstants;
import org.springframework.ai.openai.metadata.OpenAiUsage;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.lang.Nullable;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * ClassName:MyOpenAiEmbeddingModel
 * Package:org.example.contribute_for_springai
 * Description:
 *
 * @Date:2025/1/26 15:20
 * @Author:qs@1.com
 */
public class MyOpenAiEmbeddingModel extends MyAbstractEmbeddingModel {

    private final OpenAiEmbeddingOptions defaultOptions;

    public MyOpenAiEmbeddingModel(OpenAiEmbeddingOptions options) {

        Assert.notNull(options, "options must not be null");

        this.defaultOptions = options;

        /**
         * yaml 中配置了 dimensions
         * 每个 EmbeddingModel 都去设置 embeddingDimensions
         * pull request for spring ai
         */
        if (options.getDimensions() != null && options.getDimensions() > 0) {
            this.embeddingDimensions.set(options.getDimensions());
        } else if (StringUtils.hasText(options.getModel()) && getFromKnownModelDimensions(options.getModel()) > 0) {
            this.embeddingDimensions.set(getFromKnownModelDimensions(options.getModel()));
            // 给 options 设置 dimensions
            this.defaultOptions.setDimensions(this.embeddingDimensions.get());
        } else {
            // 抛异常
            // throw new IllegalArgumentException("No dimensions specified for OpenAI model");
        }
    }

}
