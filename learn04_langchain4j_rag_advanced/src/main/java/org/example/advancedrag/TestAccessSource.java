package org.example.advancedrag;

import dev.langchain4j.model.openai.*;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.service.Result;

import java.util.List;

/**
 * ClassName:Test
 * Package:org.example.advancedrag
 * Description:
 *
 * @Date:2024/11/29 9:43
 * @Author:qs@1.com
 */
public class TestAccessSource {

    public static void main(String[] args) {
        // testResult();
        testTokenStreaming();
    }

    private static void testResult() {
        Assistant assistant = Assistant.createSqlAssistant();
        Result<String> result = assistant.chat("How many customers do we have?");
        /**
         * @see Result#content()
         * 最终的结果
         */
        String answer = result.content();
        /**
         * @see Result#sources()
         * RAG 检索到的用于增强消息的内容
         */
        List<Content> sources = result.sources();
    }

    private static void testTokenStreaming() {
        OpenAiStreamingChatModel streamingChatModel = OpenAiStreamingChatModel.builder()
                .apiKey("demo")
                .modelName(OpenAiChatModelName.GPT_4_O_MINI)
                /**
                 * @see InternalOpenAiHelper.OPENAI_DEMO_URL
                 */
                .baseUrl("http://langchain4j.dev/demo/openai/v1")
                .build();
        Assistant assistant = Assistant.createSqlAssistant(streamingChatModel);

        assistant.chatStream("How many customers do we have?")
                .onRetrieved(sources -> {
                    System.out.println("sources -> " + sources);
                })
                .onNext(token -> {
                    System.out.println("token -> " + token);
                })
                .onError(error -> {
                    System.out.println("error -> " + error);
                })
                .start();
    }

}
