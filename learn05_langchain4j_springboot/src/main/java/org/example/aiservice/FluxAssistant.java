package org.example.aiservice;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;
import reactor.core.publisher.Flux;

/**
 * ClassName:FluxAssistant
 * Package:org.example.aiservice
 * Description: streaming 流媒体方式返回
 *
 * @Date:2024/12/2 15:52
 * @Author:qs@1.com
 */
@AiService
public interface FluxAssistant {
    @SystemMessage("You are a polite assistant")
    Flux<String> chat(String userMessage);

}
