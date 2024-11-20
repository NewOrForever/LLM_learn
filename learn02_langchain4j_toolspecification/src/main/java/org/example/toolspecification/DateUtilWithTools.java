package org.example.toolspecification;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.agent.tool.ToolSpecifications;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.output.Response;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Collections;

/**
 * ClassName:DateUtilWithoutTools
 * Package:org.example.toolspecification
 * Description: langchain4j 之 function call 演示
 * - 使用 toolspecification 来调用本地方法
 * - 这里没有使用 AiServices，而是直接使用 ToolSpecifications
 *
 * @Date:2024/11/13 17:19
 * @Author:qs@1.com
 */
public class DateUtilWithTools {
    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .apiKey("demo")
                .modelName(OpenAiChatModelName.GPT_4_O_MINI)
                .build();

        // 创建一个 ToolSpecification 工具对象
        ToolSpecification toolSpecification = ToolSpecifications.toolSpecificationFrom(DateUtilWithTools.class.getMethod("dateUtil"));

        UserMessage userMessage = UserMessage.from("今天是几月几号？");

        // 调用 ChatLanguageModel 的 generate 方法，传入了 ToolSpecification，告诉大模型需要调用工具来解决问题
        Response<AiMessage> response = chatModel.generate(Collections.singletonList(userMessage), toolSpecification);

        System.out.println(response.content());

        AiMessage aiMessage = response.content();
        if (aiMessage.hasToolExecutionRequests()) {
            // 大模型返回的 AiMessage 中包含了工具调用请求，表示大模型在解决问题时，需要调用工具来解决问题
            for (ToolExecutionRequest toolExecutionRequest : aiMessage.toolExecutionRequests()) {
                // 获取工具调用的方法名（该 name 就是 Tool 注解中的 name，如果没有指定 name，则默认为方法名）
                String methodName = toolExecutionRequest.name();
                Method method = DateUtilWithTools.class.getMethod(methodName);

                // result就是当前时间
                String result = (String) method.invoke(null);
                System.out.println(result);

                // 把这个响应结果告诉给大模型从而让大模型告诉我今天是几月几号
                ToolExecutionResultMessage toolExecutionResultMessage = ToolExecutionResultMessage.from(toolExecutionRequest, result);
                AiMessage finalAiMessage = chatModel.generate(userMessage, aiMessage, toolExecutionResultMessage).content();
                System.out.println(finalAiMessage.text());
            }
        }
    }

    @Tool("获取当前日期")
    public static String dateUtil() {
        return LocalDateTime.now().toString();
    }

}
