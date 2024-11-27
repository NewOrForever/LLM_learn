package org.example.vector;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModelName;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.redis.RedisEmbeddingStore;

import java.util.List;

/**
 * ClassName:RedisStoreAddAndSearchExample
 * Package:org.example.vector
 * Description: langchain4j 之向量化演示
 * 通过 Redis 来存储向量，并且搜索
 *
 * @Date:2024/11/25 9:09
 * @Author:qs@1.com
 */
public class RedisStoreAddAndSearchExample {
    public static void main(String[] args) {
        // 向量化模型
        OpenAiEmbeddingModel embedModel = OpenAiEmbeddingModel.builder()
                .apiKey("demo")
                .modelName(OpenAiEmbeddingModelName.TEXT_EMBEDDING_3_SMALL)
                .build();

        TextSegment textSegment1 = TextSegment.textSegment("客服电话是400-8558558");
        TextSegment textSegment2 = TextSegment.textSegment("客服工作时间是周一到周五");
        TextSegment textSegment3 = TextSegment.textSegment("客服投诉电话是400-8668668");
        Response<Embedding> embed1 = embedModel.embed(textSegment1);
        Response<Embedding> embed2 = embedModel.embed(textSegment2);
        Response<Embedding> embed3 = embedModel.embed(textSegment3);

        EmbeddingStore<TextSegment> embeddingStore = RedisEmbeddingStore.builder()
                .host("101.126.54.128")
                .port(16379)
                .dimension(OpenAiEmbeddingModelName.TEXT_EMBEDDING_3_SMALL.dimension())
                .build();
        // 存储向量时将原始文本一并存储
        embeddingStore.add(embed1.content(), textSegment1);
        embeddingStore.add(embed2.content(), textSegment2);
        embeddingStore.add(embed3.content(), textSegment3);

        /**
         * 搜索相似向量
         * 每次执行时，先清除掉之前存储在 redis 中的向量，不然会重复存储，导致搜索到的结果重复
         * 清除命令：redis-cli FT.DROPINDEX embedding-index DD
          */
        searchRelevant(embedModel, embeddingStore);
    }

    private static void searchRelevant(OpenAiEmbeddingModel embedModel, EmbeddingStore<TextSegment> embeddingStore) {
        System.out.println("--------------------------- 搜索相似向量");
        Response<Embedding> searchTextEmbed = embedModel.embed("客服电话是多少");
        List<EmbeddingMatch<TextSegment>> searchResultList = embeddingStore.findRelevant(searchTextEmbed.content(), 5);
        for (EmbeddingMatch<TextSegment> embeddingMathch : searchResultList) {
            System.out.println(embeddingMathch.embedded().text() + "，相似度：" + embeddingMathch.score());
        }
    }

}
