package org.example.langchain4j.aiservice;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;

/**
 * ClassName:AiServiceSystemMessage
 * Package:org.example.langchain4j.aiservice
 * Description:
 *
 * @Date:2024/11/12 14:47
 * @Author:qs@1.com
 * @see org.example.langchain4j.aiservice.AiServiceDemo 中只会回答问题，不会扮演作家来写文章
 * 这里使用 SystemMessage 来让 Writer 接口扮演作家来写文章
 */
public class AiServiceSystemMessage {
    interface Writer {
        @SystemMessage("请扮演一名作家，根据输入的文章题目写一篇200字以内的作文")
        String write(String title);

        /**
         * 将 Writer 代理对象的创建封装在接口中
         */
        static Writer create() {
            OpenAiChatModel chatModel = OpenAiChatModel.builder()
                    .apiKey("demo")
                    .modelName(OpenAiChatModelName.GPT_4_O_MINI)
                    .build();
            return AiServices.create(Writer.class, chatModel);
        }
    }

    public static void main(String[] args) {
        Writer writer = Writer.create();
        String content = writer.write("宏观经济课题论文");
        System.out.println(content);
    }

}
