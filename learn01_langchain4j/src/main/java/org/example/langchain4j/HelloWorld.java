package org.example.langchain4j;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;

/**
 * ClassName:HelloWorld
 * Package:org.example.langchain4j
 * Description: langchain4j HelloWorld
 *
 * @Date:2024/11/11 15:24
 * @Author:qs@1.com
 */
public class HelloWorld {
    public static void main(String[] args) {
        OpenAiChatModel chat = OpenAiChatModel.builder()
                .apiKey("demo")
                .modelName(GPT_4_O_MINI)
                .build();
        /*
        // 使用 one-api 代理时需要加上 /v1
        ChatLanguageModel chat = OpenAiChatModel.builder()
                .baseUrl("http://101.126.54.128:8100/v1")
                .apiKey("sk-KhjZ7aIN88gn0Hk5EeB4Fb82Ca46425186285e341aB570C3")
                .modelName("glm-4")
                .build();*/
        /*String answer = chat.generate("你好，你是谁？");
        System.out.println(answer);
        System.out.println("------");
        System.out.println(chat.generate("请重复"));*/

        String answer = chat.generate("你好，我是诸葛亮");
        System.out.println(answer);
        System.out.println("------");
        System.out.println(chat.generate("我是谁？"));
    }

}
