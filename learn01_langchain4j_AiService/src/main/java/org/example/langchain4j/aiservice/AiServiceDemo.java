package org.example.langchain4j.aiservice;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.service.AiServices;

/**
 * ClassName:HelloWorld
 * Package:org.example.langchain4j.aiservice
 * Description:
 *
 * @Date:2024/11/12 13:21
 * @Author:qs@1.com
 */
public class AiServiceDemo {
    /**
     * 定义一个接口
     */
    interface Writer {
        String write(String title);
    }

    public static void main(String[] args) {
        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .apiKey("demo")
                .modelName(OpenAiChatModelName.GPT_4_O_MINI)
                .build();

        /**
         * 底层使用 jdk 动态代理，使得 Writer 接口具有了大模型的智能能力
         * 因为是 jdk 动态代理，所以只能代理接口
         * 代理方法中会调用 OpenAiChatModel 的 generate 方法
         */
        Writer writer = AiServices.create(Writer.class, chatModel);
        String content = writer.write("宏观经济");
        System.out.println(content);
    }

}
