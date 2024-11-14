package org.example.langchain4j;

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
