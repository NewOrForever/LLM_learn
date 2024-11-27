package org.example.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.DimensionAwareEmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2q.AllMiniLmL6V2QuantizedEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * ClassName:BuildKnowledgeBase
 * Package:org.example.rag
 * Description: langchain4j 之 AI 智能客服 - 知识库构建器
 *
 * @Date:2024/11/25 16:02
 * @Author:qs@1.com
 */
@Slf4j
public class CustomerKnowledgeBaseBuilder {
    public static void main(String[] args) {
        // 向量化大模型 - OpenAI
        OpenAiEmbeddingModel embeddingModel = AiCustomerSupport.getOpenAiEmbeddingModel();
        /*// 向量化大模型 - 离线
        AllMiniLmL6V2QuantizedEmbeddingModel embeddingModel = AiCustomerSupport.getOfflineEmbeddingModel();*/
        // 构建知识库只向量化问题时，embedOnlyQuestion 设置为 true，否则设置为 false
        build("meituan-qa.txt", embeddingModel, false);
    }

    private static void build(String path, DimensionAwareEmbeddingModel embeddingModel,
                              boolean embedOnlyQuestion) {
        // 加载并解析文档
        log.debug("加载并解析文档");
        Document document = loadAndParseDocument(path);

        // 切分文档
        log.debug("切分文档");
        List<TextSegment> textSegments = splitterDocument(document);

        // 将切分后的文档向量化并收集
        log.debug("----------> 向量化文本开始");
        List<Embedding> embeddingList = embeddingSegments(textSegments, embeddingModel, embedOnlyQuestion);
        log.debug("向量化文本结束 <-----------");

        // 将向量和原始文本保存到知识库
        log.debug("----------> 存储向量开始");
        store(textSegments, embeddingList, embeddingModel);
        log.debug("存储向量结束 <-----------");
    }

    private static void store(List<TextSegment> textSegments, List<Embedding> embeddingList, DimensionAwareEmbeddingModel embeddingModel) {
        EmbeddingStore<TextSegment> embeddingStore = AiCustomerSupport.getRedisEmbeddingStore(embeddingModel.dimension());
        embeddingStore.addAll(embeddingList, textSegments);
    }

    @NotNull
    private static List<Embedding> embeddingSegments(List<TextSegment> textSegments, DimensionAwareEmbeddingModel embeddingModel,
                                                     boolean onlyQuestion) {
        // 请求大模型获取文本向量
        if (onlyQuestion) {
            // 只向量化问题
            return embeddingQuestions(textSegments, embeddingModel);
        } else {
            // 向量化所有文本
            return embeddingAll(textSegments, embeddingModel);
        }
    }

    private static List<Embedding> embeddingAll(List<TextSegment> textSegments, DimensionAwareEmbeddingModel embeddingModel) {
        Response<List<Embedding>> embedList = embeddingModel.embedAll(textSegments);
        return embedList.content();
    }

    private static List<Embedding> embeddingQuestions(List<TextSegment> textSegments, DimensionAwareEmbeddingModel embeddingModel) {
        List<TextSegment> questions = new ArrayList<>();
        for (TextSegment segment : textSegments) {
            questions.add(TextSegment.from(segment.text().split("\n")[0]));
        }
        Response<List<Embedding>> embedList = embeddingModel.embedAll(questions);
        return embedList.content();
    }

    private static List<TextSegment> splitterDocument(Document document) {
        CustomerServiceDocumentSplitter splitter = new CustomerServiceDocumentSplitter();
        List<TextSegment> textSegments = splitter.split(document);
        return textSegments;
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
