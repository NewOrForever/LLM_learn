package org.example.langchain4j.aiservice;

import dev.langchain4j.model.moderation.ModerationModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.openai.OpenAiModerationModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.Moderate;
import dev.langchain4j.service.SystemMessage;

/**
 * ClassName:AiServiceSystemMessage
 * Package:org.example.langchain4j.aiservice
 * Description: 测试 @SystemMessage 和 @Moderate 同时使用
 * @SystemMessage：角色扮演的 prompt
 * @Moderate：敏感内容检测
 *
 * 使用 @Moderate 加 ModerationModel 后会请求 OpenAI 两次：
 *  - 第一次是检测敏感内容，如果有敏感内容则直接报错
 *  - 第二次是生成回复
 *
 */
public class AiServiceSystemMessageModerateTest {
    interface Writer {
        @SystemMessage("请扮演一名作家，根据输入的文章题目写一篇200字以内的作文")
        @Moderate
        String write(String title);

        static Writer create() {
            OpenAiChatModel chatModel = OpenAiChatModel.builder()
                    .apiKey("demo")
                    .modelName(OpenAiChatModelName.GPT_4_O_MINI)
                    .build();

            ModerationModel moderationModel = OpenAiModerationModel.builder()
                    .apiKey("demo")
                    .build();

            return AiServices.builder(Writer.class)
                    .chatLanguageModel(chatModel)
                    .moderationModel(moderationModel)
                    .build();
        }
    }

    public static void main(String[] args) {
        Writer writer = Writer.create();
        // 宏观经济课题论文狗屎屁 -> 有敏感内容，会被 ModerationModel 拦截并报错
        // 宏观经济课题论文 -> 无敏感内容，正常返回
        String content = writer.write("宏观经济课题论文");
        System.out.println(content);
    }

}
