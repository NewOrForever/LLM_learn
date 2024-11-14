package org.example.toolspecification;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.agent.tool.ToolSpecifications;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.output.Response;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

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
    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .apiKey("demo")
                .modelName(OpenAiChatModelName.GPT_4_O_MINI)
                .build();

        // System.out.println(chatModel.generate("今天是几月几号？"));

        ToolSpecification toolSpecification = ToolSpecifications.toolSpecificationFrom(DateUtilWithoutTools.class.getMethod("dateUtil"));

        UserMessage userMessage = UserMessage.from("今天是几月几号？");

        Response<AiMessage> response = chatModel.generate(Collections.singletonList(userMessage), toolSpecification);

        System.out.println(response.content());

        AiMessage aiMessage = response.content();
        if (aiMessage.hasToolExecutionRequests()) {
            for (ToolExecutionRequest toolExecutionRequest : aiMessage.toolExecutionRequests()) {
                String methodName = toolExecutionRequest.name();
                Method method = DateUtilWithoutTools.class.getMethod(methodName);

                // result就是当前时间
                String result = (String) method.invoke(null);
                System.out.println(result);
            }
        }
    }


    @Tool("获取当前日期")
    public static String dateUtil(){
        return LocalDateTime.now().toString();
    }
}
