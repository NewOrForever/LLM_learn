package org.example.controller;

import org.example.aiservice.Assistant;
import org.example.aiservice.FluxAssistant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

/**
 * ClassName:AssistantController
 * Package:org.example.controller
 * Description:
 *
 * @Date:2024/12/2 11:30
 * @Author:qs@1.com
 */
@RestController
class AssistantController {

    @Autowired
    private Assistant assistant;
    @Autowired
    private FluxAssistant fluxAssistant;

    @GetMapping("/chat")
    public String chat(String message) {
        return assistant.chat(message);
    }

    /**
     * @GetMapping(value = "/fluxChat", produces = TEXT_EVENT_STREAM_VALUE)
     * 这个方式也测试下，看看有什么区别
     * demo api-key 测试下来这个 stream 方式不支持中文响应呢
     */
    @GetMapping(value = "/fluxChat")
    public Flux<String> fluxChat(String message) {
        return fluxAssistant.chat(message);
    }

}