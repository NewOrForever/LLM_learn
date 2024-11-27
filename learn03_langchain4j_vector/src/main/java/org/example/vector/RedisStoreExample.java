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
 * 通过 Redis 来存储向量
 *
 * @Date:2024/11/21 10:16
 * @Author:qs@1.com
 */
public class RedisStoreExample {
    public static void main(String[] args) {
        /**
         * @see RedisEmbeddingStore#RedisEmbeddingStore(String, Integer, String, String, String, Integer, Collection)
         * 在 build 时，会先判断 indexName 是否在 redis 中存在，如果不存在则会创建
         * @see RedisEmbeddingStore#isIndexExist(String)
         * 但是一旦我的 redis 设置了密码，就会报错 {@link SearchProtocol.SearchCommand#_LIST} 这个指令就会报错
         *
         * 注意：
         * @see RedisEmbeddingStore#RedisEmbeddingStore(String, Integer, String, String, String, Integer, Collection) 中默认要使用 redis ACL 来进行认证
         *  - 如果没有设置 user 的话，password 也不会生效
         *  - 需要同时设置 user 和 password 才能生效
         *  - 在 redis 中执行如下命令：acl list -> 展现用户权限列表，输出类似如下：
         *    user default on nopass sanitize-payload ~* &* +@all
         *    所以高版本的 redis 的 acl 中，如果 redis 只设置了密码的话，默认的 user  就是 default
         */
        EmbeddingStore<TextSegment> embeddingStore = RedisEmbeddingStore.builder()
                .host("101.126.54.128")
                .port(16379)
                /**
                 * 设置向量维度：这里使用的是 TEXT_EMBEDDING_3_SMALL 模型，维度是 1536
                 * 向量维度是 1536，那么这个向量坐标点的坐标就是(n1, n2, n3, ......, n1536)，也就是一个长度为 n 的 float 数组
                 */
                .dimension(OpenAiEmbeddingModelName.TEXT_EMBEDDING_3_SMALL.dimension())
                .build();

        OpenAiEmbeddingModel embedModel = OpenAiEmbeddingModel.builder()
                .apiKey("demo")
                .modelName(OpenAiEmbeddingModelName.TEXT_EMBEDDING_3_SMALL)
                .build();
        Response<Embedding> embed = embedModel.embed("你好，我叫周瑜");
        // 将向量存储到 redis 中
        embeddingStore.add(embed.content());


        // 从 redis 中获取向量
        List<EmbeddingMatch<TextSegment>> result = embeddingStore.findRelevant(embed.content(), 3);
        for (EmbeddingMatch<TextSegment> embeddingMatch : result) {
            System.out.println(embeddingMatch.score());
        }
    }

}
