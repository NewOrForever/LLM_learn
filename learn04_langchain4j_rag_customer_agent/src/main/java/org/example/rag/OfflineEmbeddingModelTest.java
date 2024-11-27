package org.example.rag;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2q.AllMiniLmL6V2QuantizedEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModelName;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.redis.RedisEmbeddingStore;

import java.util.List;

/**
 * ClassName:OfflineEmbeddingModelTest
 * Package:org.example.rag
 * Description: langchain4j 之 离线向量化模型测试
 *
 * @Date:2024/11/25 16:48
 * @Author:qs@1.com
 */
public class OfflineEmbeddingModelTest {
    public static void main(String[] args) {
        // 离线向量化模型
        AllMiniLmL6V2QuantizedEmbeddingModel embeddingModel = new AllMiniLmL6V2QuantizedEmbeddingModel();

        // 向量化文本
        TextSegment textSegment = TextSegment.from("你好，我叫周瑜");
        Response<Embedding> embed = embeddingModel.embed(textSegment);

        EmbeddingStore<TextSegment> embeddingStore = RedisEmbeddingStore.builder()
                .host("101.126.54.128")
                .port(16379)
                /**
                 * 这里的向量维度需要和模型的维度一致
                 */
                .dimension(embeddingModel.dimension())
                .build();
        embeddingStore.add(embed.content(), textSegment);

        Response<Embedding> queryEmbded = embeddingModel.embed("我是周瑜");
        List<EmbeddingMatch<TextSegment>> relevantList = embeddingStore.findRelevant(queryEmbded.content(), 3);
        for (EmbeddingMatch<TextSegment> relevant : relevantList) {
            System.out.println(relevant.embedded().text() + "，相似度：" + relevant.score());
        }
    }

}
