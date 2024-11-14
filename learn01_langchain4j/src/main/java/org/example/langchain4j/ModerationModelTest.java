package org.example.langchain4j;

import dev.langchain4j.model.moderation.Moderation;
import dev.langchain4j.model.moderation.ModerationModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.openai.OpenAiModerationModel;
import dev.langchain4j.model.output.Response;

/**
 * ClassName:ModerationModelTest
 * Package:org.example.langchain4j
 * Description: langchain4j 语言模型敏感内容检测测试
 *
 * @Date:2024/11/12 10:23
 * @Author:qs@1.com
 */
public class ModerationModelTest {
    public static void main(String[] args) {
        ModerationModel moderationModel = OpenAiModerationModel.builder()
                .apiKey("demo")
                .build();
        Response<Moderation> response = moderationModel.moderate("我要杀了你");
        System.out.println(response.content());
    }

}
