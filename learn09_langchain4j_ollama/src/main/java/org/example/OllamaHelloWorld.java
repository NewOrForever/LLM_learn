package org.example;


import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.model.output.Response;

import java.util.concurrent.TimeUnit;

public class OllamaHelloWorld {
    public static void main(String[] args) {
        // testLanguageModel();
        testStreamingModel();
    }

    private static void testStreamingModel() {
        OllamaStreamingChatModel streamingChatModel = OllamaStreamingChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("deepseek-r1:8b")
                .build();

        streamingChatModel.chat("你是谁", new StreamingChatResponseHandler() {
            @Override
            public void onPartialResponse(String partialResponse) {
                System.out.println(partialResponse);
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onCompleteResponse(ChatResponse completeResponse) {
                System.out.println(completeResponse);
            }

            @Override
            public void onError(Throwable error) {

            }
        });

        // LangChain 4j 1.0.0-beta1 中不会自动阻塞了，需要手动阻塞
        try {
            TimeUnit.SECONDS.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void testLanguageModel() {
        ChatLanguageModel chatLanguageModel = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("deepseek-r1:8b")
                .build();

        String answer = chatLanguageModel.chat("你是谁");
        System.out.println(answer);
    }

}
