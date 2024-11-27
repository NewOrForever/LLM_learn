package org.example.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.onnx.allminilml6v2q.AllMiniLmL6V2QuantizedEmbeddingModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.Query;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * ClassName:AiCustomerTest
 * Package:org.example.rag
 * Description:
 *
 * @Date:2024/11/26 10:52
 * @Author:qs@1.com
 */
public class AiCustomerTest {
    public static void main(String[] args) {
        // testSimple();
        // testLoadAndSplitDocument();
        testContentRetrieverWithOpenAI();
        // testContentRetrieverWithOfflineSmallModel();
    }

    static void testContentRetrieverWithOfflineSmallModel() {
        AllMiniLmL6V2QuantizedEmbeddingModel offlineEmbeddingModel = new AllMiniLmL6V2QuantizedEmbeddingModel();
        ContentRetriever contentRetriever = AiCustomerSupport.getContentRetriever(
                AiCustomerSupport.getRedisEmbeddingStore(offlineEmbeddingModel.dimension()),
                offlineEmbeddingModel,
                5, 0.8);
        Query query = Query.from("余额提现什么时候到账？");
        List<Content> retrieve = contentRetriever.retrieve(query);
        System.out.println(retrieve);
    }

    private static void testContentRetrieverWithOpenAI() {
        ContentRetriever contentRetriever = AiCustomerSupport.getContentRetriever(5, 0.7);
        Query query = Query.from("余额提现什么时候到账？");
        List<Content> retrieve = contentRetriever.retrieve(query);
        System.out.println(retrieve);
    }

    private static void testLoadAndSplitDocument() {
        Document document;
        try {
            Path documentPath = Paths.get(CustomerServiceAgent.class.getClassLoader().getResource("meituan-qa.txt").toURI());
            DocumentParser documentParser = new TextDocumentParser();
            document = FileSystemDocumentLoader.loadDocument(documentPath, documentParser);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        // 切分文档
        CustomerServiceDocumentSplitter splitter = new CustomerServiceDocumentSplitter();
        List<TextSegment> segments = splitter.split(document);
        for (TextSegment segment : segments) {
            System.out.println(segment.text());
        }
    }

    private static void simpleTest() {
        CustomerServiceAgent agent = CustomerServiceAgent.create();
        String result = agent.answer("你是谁？");
        System.out.println(result);
    }

}
