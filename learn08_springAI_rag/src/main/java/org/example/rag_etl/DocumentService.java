package org.example.rag_etl;

import org.example.VectorStoreConfiguration;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.document.DocumentTransformer;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPooled;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ClassName:DocumentService
 * Package:org.example.rag_native
 * Description: Spring AI RAG 原生方式：DocumentService
 *
 * @Date:2025/1/26 9:21
 * @Author:qs@1.com
 */
@Component
public class DocumentService {
    @Value("classpath:meituan-qa.txt")
    private Resource resource;

    /**
     * 向量存储
     * 需要 SimpleVectorStore bean 的话就用 @Qualifier("simple")
     * 需要 RedisVectorStore bean 的话就用 @Qualifier("redis")
     */
    @Autowired
    @Qualifier("redis")
    private VectorStore vectorStore;

    public List<Document> loadText() {
        TextReader textReader = new TextReader(resource);
        /**
         * 添加 filename 自定义元数据
         */
        textReader.getCustomMetadata().put("filename", "meituan-qa.txt");
        /**
         * 加载文档封装成Document对象
         * @see DocumentReader#read() 默认执行实现类的 get() 方法
         * @see TextReader#get() 读取文本文件，封装成Document对象
         */
        List<Document> documentList = textReader.read();

        // 切分文档
        List<Document> segmentList = splitDocumentWithCustomTextSplitter(documentList);

        // 把问题存到元数据中
        AtomicInteger i = new AtomicInteger(1);
        segmentList.forEach(document -> {
                    document.getMetadata().put("question", document.getText().split("\\n")[0]);
                    document.getMetadata().put("chunkNumber", i.getAndIncrement());
                }
        );
        // 向量存储切分后的文档
        vectorStore.add(segmentList);

        return segmentList;
    }

    private static List<Document> splitDocument(List<Document> documentList) {
        TextSplitter textSplitter = new TokenTextSplitter();
        /**
         * 切分器拆分文本
         * @see DocumentTransformer#transform(List) 默认执行实现类的 apply() 方法
         * @see TextSplitter#apply(List) 执行切分文本
         * {@link TokenTextSplitter} extends {@link TextSplitter} 抽象类 implements {@link DocumentTransformer}
         */
        List<Document> segmentList = textSplitter.transform(documentList);
        return segmentList;
    }

    private static List<Document> splitDocumentWithCustomTextSplitter(List<Document> documentList) {
        // 自定义文本切分器 - 按段落切分
        TextSplitter textSplitter = new CustomTextSplitter();
        List<Document> segmentList = textSplitter.transform(documentList);
        return segmentList;
    }

    public List<Document> search(String message) {
        return vectorStore.similaritySearch(message);
    }

    public List<Document> metadataSearch(String message) {
        /**
         * 使用  Filter.Expression DSL 过滤表达式的方式
         * @see SearchRequest.Builder#filterExpression(Filter.Expression) 方法的 doc 中各种使用示例
         * 或者参考官方文档：https://docs.spring.io/spring-ai/reference/1.0/api/vectordbs/redis.html#_metadata_filtering
         */
        FilterExpressionBuilder filterExpressionBuilder = new FilterExpressionBuilder();
        Filter.Expression filterExpression = filterExpressionBuilder.and(
                        filterExpressionBuilder.in("question", "Q：如何联系客服解决问题？", "Q：如果对商家服务不满意如何进行投诉？"),
                        filterExpressionBuilder.gt("chunkNumber", 27)
                )
                .build();

        /**
         * 使用  metadata filter 时需要显式的在 RedisVectorStore 的 metadataFields 中定义用于 filter expressions 中的 metadata 字段
         * @see VectorStoreConfiguration#redisVectorStore(JedisPooled, EmbeddingModel) 示例
         */
        return vectorStore.similaritySearch(
                SearchRequest.builder().query(message)
                        .topK(3)
                        .similarityThreshold(0.8)
                        /*
                        // 类似 SQL 的文本过滤表达式字符串会被解析成 Filter.Expression 对象
                        .filterExpression("question in ['Q：如何联系客服解决问题？', 'Q：如果对商家服务不满意如何进行投诉？'] && chunkNumber > 27")
                        */
                        .filterExpression(filterExpression)
                        .build());
    }


}
