package org.example.langchain4j.aiservice;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * ClassName:AiServiceSystemMessage
 * Package:org.example.langchain4j.aiservice
 * Description: 测试 @SystemMessage 中使用变量
 * 由用户来指定变量的值
 */
public class AiServiceSystemMessageVariableTest {
    interface Writer {
        /**
         * @see dev.langchain4j.service.DefaultAiServices#validateParameters(java.lang.reflect.Method)
         * 当方法参数数量大于等2 时，每个参数至少需要一个注解
         * @param title
         * @param num
         * @return
         */
        @SystemMessage("请扮演一名作家，根据输入的文章题目写一篇{{num}}字以内的作文")
        String write(@UserMessage String title, @V("num") int num);

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
        String content = writer.write("宏观经济课题论文", 300);
        System.out.println(content);
    }

}
