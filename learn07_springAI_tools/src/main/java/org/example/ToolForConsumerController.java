package org.example;

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
 * 测试结果：Consumer 工具使用后，AI 大模型会一直返回需要工具调用，暂时这个版本应该对于 Consumer 工具的支持还不够
 * 也可能是我对于 Consumer 工具的使用方式不了解
 *
 * @Date:2025/1/17 16:37
 * @Author:qs@1.com
 */
//@RestController
//@RequestMapping("/tool/consumer")
//public class ToolForConsumerController {
//    @Autowired
//    private OpenAiChatModel openAiChatModel;
//
//    @RequestMapping("/functionTest")
//    public String functionTest(String message) {
//        SystemMessage systemMessage = new SystemMessage("当 tool message 返回的是 'null' 时，表示工具已经调用完成，不需要再去调用工具了");
//        UserMessage userMessage = new UserMessage(message);
//        Prompt prompt = new Prompt(List.of(systemMessage, userMessage), OpenAiChatOptions.builder().function("writerServiceForConsumerTest").build());
//        Generation generation = openAiChatModel.call(prompt).getResult();
//        return (generation != null) ? generation.getOutput().getContent() : "";
//    }
//
//}
