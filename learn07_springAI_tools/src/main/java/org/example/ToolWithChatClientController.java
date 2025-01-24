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
 * Description: 使用 ChatClient
 *
 * @Date:2025/1/17 16:37
 * @Author:qs@1.com
 */
@RestController
@RequestMapping("/tool/chatclient")
public class ToolWithChatClientController {
    @Autowired
    private ChatClient chatClient;

    @RequestMapping("/functionTest")
    public String functionTest(String message) {
        return chatClient.prompt()
                .user(message)
                .functions("dateService")
                .call().content();
    }

    @RequestMapping("/functionTest2")
    public String functionTest2(String message) {
        // 直接自定义 Prompt
        Prompt prompt = new Prompt(message,
                OpenAiChatOptions.builder()
                        .function("dateService").build()
        );
        return chatClient.prompt(prompt).call().content();
    }

    @RequestMapping("/functionCallbackTest")
    public String functionCallbackTest(String message) {
        return chatClient.prompt().user(message)
                .functions(
                        FunctionCallback.builder()
                                .function("dateServiceCallbackTest", new DateServiceForFunctionCallBackTest())
                                .description("获取指定地点的当前时间")
                                .inputType(DateServiceForFunctionCallBackTest.Request.class).build()
                ).call().content();
    }

    @RequestMapping("/systemMessageTest")
    public String systemMessageTest(String message) {
        return chatClient.prompt()
                .system("请用中文回答我")
                .user(message)
                .functions(
                        FunctionCallback.builder()
                                .function("dateService", new DateService())
                                .description("获取指定地点的当前时间")
                                .inputType(DateService.Request.class).build()
                ).call().content();
    }

}
