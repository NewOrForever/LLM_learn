package org.example.langchain4j;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.output.Response;

/**
 * ClassName:MultiRountChatTest
 * Package:org.example.langchain4j
 * Description: langchain4j 多轮对话测试
 *
 * @Date:2024/11/11 16:06
 * @Author:qs@1.com
 */
public class MultiRountChatTest {
    public static void main(String[] args) {
        OpenAiChatModel chat = OpenAiChatModel.builder()
                .apiKey("demo")
                .modelName(OpenAiChatModelName.GPT_4_O_MINI)
                .build();

        // 第一次对话
        // 用户发送给大模型的消息
        UserMessage userMessage01 = UserMessage.userMessage("你好，我是周瑜");
        // 大模型响应给用户的消息
        Response<AiMessage> response01 = chat.generate(userMessage01);
        AiMessage aiMessage01 = response01.content();
        System.out.println(aiMessage01.text());

        System.out.println("------");

        // 第二次对话
        UserMessage userMessage02 = UserMessage.userMessage("我叫什么？");
        // 为了实现多轮对话，需要将历史消息传递给大模型
        Response<AiMessage> response02 = chat.generate(userMessage01, aiMessage01, userMessage02);
        AiMessage aiMessage02 = response02.content();
        System.out.println(aiMessage02.text());
    }

}
