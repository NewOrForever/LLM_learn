package org.example.advancedrag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.injector.ContentInjector;
import dev.langchain4j.rag.content.injector.DefaultContentInjector;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Scanner;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;
import static java.util.Arrays.asList;
import static org.example.advancedrag.Assistant.toPath;

/**
 * ClassName:TestContentInject
 * Package:org.example.advancedrag
 * Description:
 *
 * @Date:2024/11/28 16:52
 * @Author:qs@1.com
 */
public class TestDefaultContentInjectorWithMetadata {
    public static void main(String[] args) {
        Assistant assistant = createAssistant("miles-of-smiles-terms-of-use.txt");

        // Ask "What is the name of the file where cancellation policy is defined?".
        // Observe how "file_name" metadata entry was injected into the prompt.
        // startConversationWith(assistant);
        String answer = assistant.answer("What is the name of the file");
        /**
         * RAG 后的 UserMessage：
         * What is the name of the file
         *
         * Answer using the following information:
         * content: 1. Introduction
         * file_name: miles-of-smiles-terms-of-use.txt
         * index: 1
         *
         * content: 8. Changes to These Terms
         * We may revise these terms of use at any time by amending this page. You are expected to check this page from time to time to take notice of any changes we made.
         * file_name: miles-of-smiles-terms-of-use.txt
         * index: 9
         *
         * content: Miles of Smiles Car Rental Services Terms of Use
         * file_name: miles-of-smiles-terms-of-use.txt
         * index: 0
         */
        System.out.println(answer);
    }

    public static void startConversationWith(Assistant assistant) {
        Logger log = LoggerFactory.getLogger(Assistant.class);
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                log.info("==================================================");
                log.info("User: ");
                String userQuery = scanner.nextLine();
                log.info("==================================================");

                if ("exit".equalsIgnoreCase(userQuery)) {
                    break;
                }

                String agentAnswer = assistant.answer(userQuery);
                log.info("==================================================");
                log.info("Assistant: " + agentAnswer);
            }
        }
    }


    private static Assistant createAssistant(String documentPath) {

        Document document = loadDocument(toPath(documentPath), new TextDocumentParser());

        EmbeddingModel embeddingModel = new BgeSmallEnV15QuantizedEmbeddingModel();

        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(DocumentSplitters.recursive(300, 0))
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();

        ingestor.ingest(document);

        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .build();

        // Each retrieved segment should include "file_name" and "index" metadata values in the prompt
        ContentInjector contentInjector = DefaultContentInjector.builder()
                // .promptTemplate(...) // Formatting can also be changed
                /**
                 * 在 ContentInjector 中将 "file_name" 和 "index" 两个 metadata 作为 prompt 的一部分
                 * 如果 TextSegment 的 Metadata 中没有 "file_name" 和 "index"，则不会注入到 prompt 中
                 */
                .metadataKeysToInclude(asList("file_name", "index"))
                .build();

        RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                .contentRetriever(contentRetriever)
                .contentInjector(contentInjector)
                .build();

        ChatLanguageModel chatLanguageModel = OpenAiChatModel.builder()
                .apiKey("demo")
                .modelName(OpenAiChatModelName.GPT_4_O_MINI)
                // .logRequests(true)
                .build();

        return AiServices.builder(Assistant.class)
                .chatLanguageModel(chatLanguageModel)
                .retrievalAugmentor(retrievalAugmentor)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
    }

}
