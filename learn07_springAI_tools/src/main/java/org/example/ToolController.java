package org.example;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ClassName:ToolController
 * Package:org.example
 * Description:
 *
 * @Date:2025/1/17 16:37
 * @Author:qs@1.com
 */
@RestController
@RequestMapping("/tool")
public class ToolController {
    @Autowired
    private OpenAiChatModel openAiChatModel;

    @RequestMapping("/functionTest")
    public String functionTest(String message) {
        Prompt prompt = new Prompt(message, OpenAiChatOptions.builder().function("dateService").build());
        Generation generation = openAiChatModel.call(prompt).getResult();
        return (generation != null) ? generation.getOutput().getContent() : "";
    }


    @RequestMapping("/functionCallbackTest")
    public String functionCallbackTest(String message) {
        Prompt prompt = new Prompt(message, OpenAiChatOptions.builder()
                .functionCallbacks(
                        List.of(FunctionCallback.builder()
                                .function("dateService", new DateService())
                                .description("获取指定地点的当前时间")
                                .inputType(DateService.Request.class)
                                .build())
                ).build());
        Generation generation = openAiChatModel.call(prompt).getResult();
        return (generation != null) ? generation.getOutput().getContent() : "";
    }

    @RequestMapping("/functionCallbackWithResponseConverterTest")
    public String functionCallbackWithResponseConverterTest(String message) {
        Prompt prompt = new Prompt(message, OpenAiChatOptions.builder()
                .functionCallbacks(
                        List.of(FunctionCallback.builder()
                                .function("dateService", new DateService())
                                .description("获取指定地点的当前时间")
                                .inputType(DateService.Request.class)
                                // 需要传入 json 格式的字符串，不然 AI 大模型会一直返回需要工具调用
                                // 毕竟 content-type 是 application/json
                                .responseConverter((toolResponse) -> "{\"date\":\"" + "2025年1月1日17点00分" + "\"}")
                                .build())
                ).build());
        Generation generation = openAiChatModel.call(prompt).getResult();
        return (generation != null) ? generation.getOutput().getContent() : "";
    }

    @RequestMapping("/systemMessageTest")
    public String systemMessageTest(String message) {
        // 有时候大模型给你的答案或工具参数可能是英文的，这时候我们可以通过使用 SystemMessage 这个系统消息来解决
        SystemMessage systemMessage = new SystemMessage("请用中文回答我");
        UserMessage userMessage = new UserMessage(message);

        Prompt prompt = new Prompt(List.of(systemMessage, userMessage), OpenAiChatOptions.builder()
                .functionCallbacks(
                        List.of(FunctionCallback.builder()
                                .function("dateService", new DateService())
                                .description("获取指定地点的当前时间")
                                .inputType(DateService.Request.class).build())
                ).build());
        Generation generation = openAiChatModel.call(prompt).getResult();
        return (generation != null) ? generation.getOutput().getContent() : "";
    }

}
