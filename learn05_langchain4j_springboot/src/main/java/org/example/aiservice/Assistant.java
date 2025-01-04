package org.example.aiservice;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;

/**
 * ClassName:Assistant
 * Package:org.example
 * Description:
 *
 * @Date:2024/12/2 11:18
 * @Author:qs@1.com
 */
@AiService
public interface Assistant {
    @SystemMessage("你是个有礼貌的助手")
    String chat(String userMessage);
}
