package org.example.rag;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2q.AllMiniLmL6V2QuantizedEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModelName;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.store.embedding.redis.RedisEmbeddingStore;

import java.util.List;

/**
 * ClassName:InMemoryEmbeddingStoreTest
 * Package:org.example.rag
 * Description: langchain4j 之 内存向量存储测试
 *
 * @Date:2024/11/25 17:34
 * @Author:qs@1.com
 */
public class InMemoryEmbeddingStoreTest {
    public static void main(String[] args) {
        // 大模型
        EmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder()
                .apiKey("demo")
                .modelName(OpenAiEmbeddingModelName.TEXT_EMBEDDING_3_SMALL)
                .build();

        // 向量化文本
        TextSegment textSegment = TextSegment.from("你好，我叫周瑜");
        Response<Embedding> embed = embeddingModel.embed(textSegment);

        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        embeddingStore.add(embed.content(), textSegment);

        Response<Embedding> queryEmbded = embeddingModel.embed("我是周瑜");
        List<EmbeddingMatch<TextSegment>> relevantList = embeddingStore.findRelevant(queryEmbded.content(), 3);
        for (EmbeddingMatch<TextSegment> relevant : relevantList) {
            System.out.println(relevant.embedded().text() + "，相似度：" + relevant.score());
        }
    }

}
