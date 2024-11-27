package org.example.rag;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.DimensionAwareEmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2q.AllMiniLmL6V2QuantizedEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModelName;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.redis.RedisEmbeddingStore;

/**
 * ClassName:AiCustomerSupport
 * Package:org.example.rag
 * Description:
 *
 * @Date:2024/11/26 9:21
 * @Author:qs@1.com
 */
public interface AiCustomerSupport {
    static OpenAiChatModel getOpenAiChatModel() {
        // 大模型
        return OpenAiChatModel.builder()
                .apiKey("demo")
                .modelName(OpenAiChatModelName.GPT_4_O_MINI)
                .build();
    }

    static OpenAiEmbeddingModel getOpenAiEmbeddingModel() {
        // 向量大模型
        return getOpenAiEmbeddingModel(OpenAiEmbeddingModelName.TEXT_EMBEDDING_3_SMALL);
    }

    static OpenAiEmbeddingModel getOpenAiEmbeddingModel(OpenAiEmbeddingModelName modelName) {
        // 向量大模型
        return OpenAiEmbeddingModel.builder()
                .apiKey("demo")
                .modelName(modelName)
                .build();
    }

    static AllMiniLmL6V2QuantizedEmbeddingModel getOfflineEmbeddingModel() {
        // 离线向量大模型
        return new AllMiniLmL6V2QuantizedEmbeddingModel();
    }

    static RedisEmbeddingStore getRedisEmbeddingStore() {
        return getRedisEmbeddingStore(OpenAiEmbeddingModelName.TEXT_EMBEDDING_3_SMALL.dimension());
    }

    static RedisEmbeddingStore getRedisEmbeddingStore(int dimension) {
        return RedisEmbeddingStore.builder()
                .host("101.126.54.128")
                .port(16379)
                .dimension(dimension)
                .build();
    }

    static ContentRetriever getContentRetriever() {
        return getContentRetriever(getRedisEmbeddingStore(), getOpenAiEmbeddingModel(), 5, 0.7);
    }

    static ContentRetriever getContentRetriever(int maxResults, double minScore) {
        return getContentRetriever(getRedisEmbeddingStore(), getOpenAiEmbeddingModel(), maxResults, minScore);
    }

    static ContentRetriever getContentRetriever(EmbeddingStore<TextSegment> embeddingStore,
                                                EmbeddingModel embeddingModel, int maxResults, double minScore) {
        return EmbeddingStoreContentRetriever.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .maxResults(maxResults)     // 最相似的maxResults个结果
                .minScore(minScore)            // 只找相似度在minScore以上的内容
                .build();
    }

}
