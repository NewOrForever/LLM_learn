package org.example;


import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;

import java.util.concurrent.TimeUnit;

/**
 * Ollama 是兼容部分 OpenAI 的Api 的，可参考官方文档：https://github.com/ollama/ollama/blob/main/docs/openai.md
 */
public class OllamaUseOpenAiApi {
    public static void main(String[] args) {
//         testLanguageModel();
         testStreamingModel();
    }

    private static void testStreamingModel() {
        StreamingChatLanguageModel streamingChatModel = OpenAiStreamingChatModel.builder()
                .baseUrl("http://localhost:11434/v1")
                .modelName("deepseek-r1:8b")
                .apiKey("nop")
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
        ChatLanguageModel chatLanguageModel = OpenAiChatModel.builder()
                .baseUrl("http://localhost:11434/v1")
                .modelName("deepseek-r1:8b")
                .apiKey("nop")
                .build();

        String answer = chatLanguageModel.chat("你是谁");
        System.out.println(answer);
    }

}
