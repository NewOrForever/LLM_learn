package org.example.controller;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName:HelloWorldController
 * Package:org.example.controller
 * Description:
 *
 * @Date:2024/11/12 9:53
 * @Author:qs@1.com
 */
@RestController
public class HelloWorldController {
    @Autowired
    private ChatLanguageModel chatLanguageModel;

    @GetMapping("/hello")
    public String hello() {
        return chatLanguageModel.generate("你好啊");
    }

}
