package org.example.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * ClassName:EmbeddingStoreIngestorTest
 * Package:org.example.rag
 * Description: langchain4j 之 向量存储器数据导入器测试
 *
 * @Date:2024/11/26 15:25
 * @Author:qs@1.com
 */
public class EmbeddingStoreIngestorTest {
    public static void main(String[] args) {
        Document document = loadAndParseDocument("meituan-qa.txt");

        /**
         * DocumentTransformer 用于转换文档
         * TextSegmentTransformer 用于转换切分后的文本片段
         */
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .embeddingStore(AiCustomerSupport.getRedisEmbeddingStore())
                .embeddingModel(AiCustomerSupport.getOpenAiEmbeddingModel())
                .documentSplitter(new CustomerServiceDocumentSplitter())
                .build();
        ingestor.ingest(document);
    }

    private static Document loadAndParseDocument(String path) {
        Document document;
        try {
            Path documentPath = Paths.get(CustomerKnowledgeBaseBuilder.class.getClassLoader().getResource(path).toURI());
            DocumentParser documentParser = new TextDocumentParser();
            document = FileSystemDocumentLoader.loadDocument(documentPath, documentParser);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return document;
    }

}
