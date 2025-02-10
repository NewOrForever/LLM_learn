package org.example.rag_etl;

import com.knuddels.jtokkit.api.EncodingType;
import org.springframework.ai.document.ContentFormatter;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.BatchingStrategy;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.ai.tokenizer.JTokkitTokenCountEstimator;
import org.springframework.ai.tokenizer.TokenCountEstimator;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName:QwenTokenCountBatchingStrategy
 * Package:org.example.rag_etl
 * Description: 通义千问 text-embedding-v3模型input是字符串列表时最多支持20条，每条最长支持8,192Token
 * 所以这里进行扩展下 <br/>
 * 该类直接复制自 {@link TokenCountBatchingStrategy}，只是增加了 MAX_DOCUMENTS_PER_BATCH 常量，表示每批次最多处理的 Document 数
 * <br/>
 * 只在 batch 方法中进行了修改，增加了 Document 数达到上限时也会创建新批次的逻辑
 *
 * @Date:2025/1/26 16:50
 * @Author:qs@1.com
 */
public class QwenTokenCountBatchingStrategy implements BatchingStrategy {
    private static final int MAX_DOCUMENTS_PER_BATCH = 20;

    private static final int MAX_INPUT_TOKEN_COUNT = 8191;

    /**
     * The default percentage of tokens to reserve when calculating the actual max input
     * token count.
     */
    private static final double DEFAULT_TOKEN_COUNT_RESERVE_PERCENTAGE = 0.1;

    private final TokenCountEstimator tokenCountEstimator;

    private final int maxInputTokenCount;

    private final ContentFormatter contentFormatter;

    private final MetadataMode metadataMode;

    public QwenTokenCountBatchingStrategy() {
        this(EncodingType.CL100K_BASE, MAX_INPUT_TOKEN_COUNT, DEFAULT_TOKEN_COUNT_RESERVE_PERCENTAGE);
    }

    /**
     * @param encodingType       {@link EncodingType}
     * @param maxInputTokenCount upper limit for input tokens
     * @param reservePercentage  the percentage of tokens to reserve from the max input
     *                           token count to create a buffer.
     */
    public QwenTokenCountBatchingStrategy(EncodingType encodingType, int maxInputTokenCount, double reservePercentage) {
        this(encodingType, maxInputTokenCount, reservePercentage, Document.DEFAULT_CONTENT_FORMATTER,
                MetadataMode.NONE);
    }

    /**
     * @param encodingType       The {@link EncodingType} to be used for token counting.
     * @param maxInputTokenCount The initial upper limit for input tokens.
     * @param reservePercentage  The percentage of tokens to reserve from the max input
     *                           token count. This creates a buffer for potential token count increases during
     *                           processing.
     * @param contentFormatter   the {@link ContentFormatter} to be used for formatting
     *                           content.
     * @param metadataMode       The {@link MetadataMode} to be used for handling metadata.
     */
    public QwenTokenCountBatchingStrategy(EncodingType encodingType, int maxInputTokenCount, double reservePercentage,
                                      ContentFormatter contentFormatter, MetadataMode metadataMode) {
        Assert.notNull(encodingType, "EncodingType must not be null");
        Assert.isTrue(maxInputTokenCount > 0, "MaxInputTokenCount must be greater than 0");
        Assert.isTrue(reservePercentage >= 0 && reservePercentage < 1, "ReservePercentage must be in range [0, 1)");
        Assert.notNull(contentFormatter, "ContentFormatter must not be null");
        Assert.notNull(metadataMode, "MetadataMode must not be null");
        this.tokenCountEstimator = new JTokkitTokenCountEstimator(encodingType);
        this.maxInputTokenCount = (int) Math.round(maxInputTokenCount * (1 - reservePercentage));
        this.contentFormatter = contentFormatter;
        this.metadataMode = metadataMode;
    }

    /**
     * Constructs a TokenCountBatchingStrategy with the specified parameters.
     *
     * @param tokenCountEstimator the TokenCountEstimator to be used for estimating token
     *                            counts.
     * @param maxInputTokenCount  the initial upper limit for input tokens.
     * @param reservePercentage   the percentage of tokens to reserve from the max input
     *                            token count to create a buffer.
     * @param contentFormatter    the ContentFormatter to be used for formatting content.
     * @param metadataMode        the MetadataMode to be used for handling metadata.
     */
    public QwenTokenCountBatchingStrategy(TokenCountEstimator tokenCountEstimator, int maxInputTokenCount,
                                      double reservePercentage, ContentFormatter contentFormatter, MetadataMode metadataMode) {
        Assert.notNull(tokenCountEstimator, "TokenCountEstimator must not be null");
        Assert.isTrue(maxInputTokenCount > 0, "MaxInputTokenCount must be greater than 0");
        Assert.isTrue(reservePercentage >= 0 && reservePercentage < 1, "ReservePercentage must be in range [0, 1)");
        Assert.notNull(contentFormatter, "ContentFormatter must not be null");
        Assert.notNull(metadataMode, "MetadataMode must not be null");
        this.tokenCountEstimator = tokenCountEstimator;
        this.maxInputTokenCount = (int) Math.round(maxInputTokenCount * (1 - reservePercentage));
        this.contentFormatter = contentFormatter;
        this.metadataMode = metadataMode;
    }

    @Override
    public List<List<Document>> batch(List<Document> documents) {
        List<List<Document>> batches = new ArrayList<>();
        int currentSize = 0;
        int currentDocumentCount = 0;
        List<Document> currentBatch = new ArrayList<>();
        // Make sure the documentTokens' entry order is preserved by making it a
        // LinkedHashMap.
        Map<Document, Integer> documentTokens = new LinkedHashMap<>();

        // 计算每个 Document 的 Token 数
        for (Document document : documents) {
            int tokenCount = this.tokenCountEstimator
                    .estimate(document.getFormattedContent(this.contentFormatter, this.metadataMode));
            if (tokenCount > this.maxInputTokenCount) {
                throw new IllegalArgumentException(
                        "Tokens in a single document exceeds the maximum number of allowed input tokens");
            }
            documentTokens.put(document, tokenCount);
        }

        // 分批逻辑
        for (Document document : documentTokens.keySet()) {
            Integer tokenCount = documentTokens.get(document);

            // 如果当前批次的 Token 数或 Document 数达到上限，则创建新批次
            if (currentSize + tokenCount > this.maxInputTokenCount || currentDocumentCount >= MAX_DOCUMENTS_PER_BATCH) {
                batches.add(currentBatch);
                currentBatch = new ArrayList<>();
                currentSize = 0;
                currentDocumentCount = 0;
            }

            // 将 Document 添加到当前批次
            currentBatch.add(document);
            currentSize += tokenCount;
            currentDocumentCount++;
        }

        // 添加最后一个批次
        if (!currentBatch.isEmpty()) {
            batches.add(currentBatch);
        }
        return batches;
    }

}
