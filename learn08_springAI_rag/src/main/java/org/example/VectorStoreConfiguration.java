package org.example;

import org.example.rag_etl.QwenTokenCountBatchingStrategy;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import redis.clients.jedis.JedisPooled;

/**
 * ClassName:VectorConfiguration
 * Package:org.example.rag_native
 * Description: 向量存储配置类
 *
 * @Date:2025/1/26 13:13
 * @Author:qs@1.com
 */
@Configuration
public class VectorStoreConfiguration {

    @Bean
    @Qualifier("simple")
    public SimpleVectorStore simpleVectorStore(EmbeddingModel embeddingModel) {
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(embeddingModel).build();
        return simpleVectorStore;
    }


    @Bean
    public JedisPooled jedisPooled() {
        return new JedisPooled("101.126.54.128", 16379);
    }

    @Bean
    @Qualifier("redis")
    public RedisVectorStore redisVectorStore(JedisPooled jedisPooled, EmbeddingModel embeddingModel) {
        RedisVectorStore redisVectorStore = RedisVectorStore.builder(jedisPooled, embeddingModel)
                /**
                 * 这些属性基本上都是有默认值的
                 */
                .indexName("custom-index")
                .prefix("custom-prefix:")

                /**
                 * 需要在创建 RedisVectorStore 对象时将 filter expressions 中用到的 metadata field 显式的在这里列出
                 */
                .metadataFields(
                        // RedisVectorStore.MetadataField.tag("country"),
                        // RedisVectorStore.MetadataField.numeric("year"),
                        RedisVectorStore.MetadataField.text("filename"),
                        RedisVectorStore.MetadataField.text("question"),
                        RedisVectorStore.MetadataField.numeric("chunkNumber")
                )
                /**
                 * @see RedisVectorStore#afterPropertiesSet()
                 * 是否要初始化创建 index
                 * true 表示会初始化创建 index，默认值是 false
                 */
                .initializeSchema(true)
                /**
                 * QwenTokenCountBatchingStrategy 是一个自定义的批处理策略
                 * 通义千问 text-embedding-v3模型input是字符串列表时最多支持20条，每条最长支持8,192Token
                 */
                .batchingStrategy(new QwenTokenCountBatchingStrategy())
                .build();

        return redisVectorStore;
    }

}
