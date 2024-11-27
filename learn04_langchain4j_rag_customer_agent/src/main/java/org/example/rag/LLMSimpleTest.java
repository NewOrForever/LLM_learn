package org.example.rag;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;

/**
 * ClassName:LLMSimpleTest
 * Package:org.example.rag
 * Description:
 *
 * @Date:2024/11/25 10:51
 * @Author:qs@1.com
 */
public class LLMSimpleTest {
    public static void main(String[] args) {
        // 大模型
        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .apiKey("demo")
                .modelName(OpenAiChatModelName.GPT_4_O_MINI)
                .build();

        String result = chatModel.generate("你是谁？");
        System.out.println(result);
    }

}
