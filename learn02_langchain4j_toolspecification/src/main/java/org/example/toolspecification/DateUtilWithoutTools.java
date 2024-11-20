package org.example.toolspecification;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;

/**
 * ClassName:DateUtilWithoutTools
 * Package:org.example.toolspecification
 * Description: langchain4j 之大模型的不足演示
 * 大模型在解决问题时，是基于互联网上很多历史资料进行预测的，无法获取当前最新的数据
 * 这里以获取当前时间为例，演示大模型的不足
 *
 * @Date:2024/11/13 17:19
 * @Author:qs@1.com
 */
public class DateUtilWithoutTools {
    public static void main(String[] args)  {
        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .apiKey("demo")
                .modelName(OpenAiChatModelName.GPT_4_O_MINI)
                .build();

        System.out.println(chatModel.generate("今天是几月几号？"));
    }

}
