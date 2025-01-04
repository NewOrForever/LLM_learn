package org.example.aiservice;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;

import static dev.langchain4j.service.spring.AiServiceWiringMode.EXPLICIT;

/**
 * ClassName:OpenAiAssistant
 * Package:org.example.aiservice
 * Description: 显示装配模式，所有的组件都需要显示指定
 *
 * @Date:2024/12/2 15:38
 * @Author:qs@1.com
 */
@AiService(wiringMode = EXPLICIT, chatModel = "openAiChatModel", tools = {"dateCalcTools"})
public interface ExplicitOpenAiAssistant {

    @SystemMessage("You are a polite assistant")
    String chat(String userMessage);

}