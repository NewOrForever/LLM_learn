package org.example;


import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;

import java.util.concurrent.TimeUnit;

public class OllamaForEmbedding {
    public static void main(String[] args) {
//         testOllamaEmbed();
        testOllamaUseOpenAiApiEmbed();
    }

    private static void testOllamaUseOpenAiApiEmbed() {
        EmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder()
                .baseUrl("http://localhost:11434/v1")
                .modelName("nomic-embed-text:latest")
                .apiKey("nop")
                .build();

        Embedding content = embeddingModel.embed("你是谁").content();
        System.out.println(content);
    }

    private static void testOllamaEmbed() {
        EmbeddingModel embeddingModel = OllamaEmbeddingModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("nomic-embed-text:latest")
                .build();

        Embedding content = embeddingModel.embed("你是谁").content();
        System.out.println(content);
    }

}
