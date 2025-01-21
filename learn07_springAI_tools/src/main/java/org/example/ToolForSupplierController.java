package org.example;

import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName:ToolForSupplierController
 * Package:org.example
 * Description:
 *
 * @Date:2025/1/17 16:37
 * @Author:qs@1.com
 */
@RestController
@RequestMapping("/tool/supplier")
public class ToolForSupplierController {
    @Autowired
    private OpenAiChatModel openAiChatModel;

    @RequestMapping("/functionTest")
    public String functionTest(String message) {
        Prompt prompt = new Prompt(message, OpenAiChatOptions.builder().function("dateServiceForSupplier").build());
        Generation generation = openAiChatModel.call(prompt).getResult();
        return (generation != null) ? generation.getOutput().getContent() : "";
    }

}
