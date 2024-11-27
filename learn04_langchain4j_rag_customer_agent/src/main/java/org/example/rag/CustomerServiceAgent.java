package org.example.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.embedding.onnx.allminilml6v2q.AllMiniLmL6V2QuantizedEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.service.AiServices;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * ClassName:CustomerServiceAgent
 * Package:org.example.rag
 * Description: langchain4j 之 AI 智能客服
 *
 * @Date:2024/11/25 10:53
 * @Author:qs@1.com
 */
public interface CustomerServiceAgent {
    String answer(String question);

    static CustomerServiceAgent create() {
        OpenAiChatModel chatModel = AiCustomerSupport.getOpenAiChatModel();
        ContentRetriever contentRetriever = AiCustomerSupport.getContentRetriever();
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .maxMessages(10)
                .build();

        // 指定模型并返回代理对象
        return AiServices.builder(CustomerServiceAgent.class)
                .chatLanguageModel(chatModel)
                .chatMemory(chatMemory)
                /**
                 * 设置内容检索器
                 */
                .contentRetriever(contentRetriever)
                .tools(new DateCalculatorTool())
                .build();
    }

}
