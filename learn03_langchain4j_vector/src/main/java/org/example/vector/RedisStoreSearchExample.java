package org.example.vector;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModelName;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.redis.RedisEmbeddingStore;
import redis.clients.jedis.search.SearchProtocol;

import java.util.Collection;
import java.util.List;

/**
 * ClassName:RedisStoreExample
 * Package:org.example.vector
 * Description: langchain4j 之向量化演示
 * 搜索 redis 中的向量
 *
 * @Date:2024/11/21 10:16
 * @Author:qs@1.com
 */
public class RedisStoreSearchExample {
    public static void main(String[] args) {
        EmbeddingStore<TextSegment> embeddingStore = RedisEmbeddingStore.builder()
                .host("101.126.54.128")
                .port(16379)
                .dimension(OpenAiEmbeddingModelName.TEXT_EMBEDDING_3_SMALL.dimension())
                .build();

        OpenAiEmbeddingModel embedModel = OpenAiEmbeddingModel.builder()
                .apiKey("demo")
                .modelName(OpenAiEmbeddingModelName.TEXT_EMBEDDING_3_SMALL)
                .build();
        Response<Embedding> embed = embedModel.embed("我的名字叫周瑜");
        Response<Embedding> embed02 = embedModel.embed("我不知道谁叫周瑜");
        Response<Embedding> embed03 = embedModel.embed("你好，今天的天气好好啊");

        // 从 redis 中获取向量
        List<EmbeddingMatch<TextSegment>> result = embeddingStore.findRelevant(embed.content(), 3);
        for (EmbeddingMatch<TextSegment> embeddingMatch : result) {
            System.out.println(embeddingMatch.score());
        }

        System.out.println("-------------------");
        List<EmbeddingMatch<TextSegment>> result02 = embeddingStore.findRelevant(embed02.content(), 3);
        for (EmbeddingMatch<TextSegment> embeddingMatch : result02) {
            System.out.println(embeddingMatch.score());
        }

        System.out.println("-------------------");
        List<EmbeddingMatch<TextSegment>> result03 = embeddingStore.findRelevant(embed03.content(), 3);
        for (EmbeddingMatch<TextSegment> embeddingMatch : result03) {
            System.out.println(embeddingMatch.score());
        }
    }

}
