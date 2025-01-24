package org.example;

import org.example.advisor.ReReadingAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

/**
 * ClassName:ChatClientTestController
 * Package:org.example
 * Description: 测试下 Spring AI 自带的一些 Advisor 实现类
 * 更多功能参考官方文档：https://docs.spring.io/spring-ai/reference/1.0/api/chatclient.html
 *
 * @Date:2025/1/21 10:54
 * @Author:qs@1.com
 */
@RestController
@RequestMapping("/advisor")
public class AdvisorTestController {

    @Autowired
    private ChatClient advisorChatClient;

    @RequestMapping(value = "/chat", produces = "text/html;charset=UTF-8")
    public Flux<String> chat(String chatId, String userMessageContent) {
        return advisorChatClient.prompt()
                .user(userMessageContent)
                /**
                 * @see org.springframework.ai.chat.client.ChatClient.ChatClientRequestSpec#advisors(java.util.function.Consumer)
                 * 通过这个 Consumer 方法可以为 advisorParams 添加参数，以便后续 Advisor 使用
                 */
                .advisors(advisorSpec -> {
                    advisorSpec.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                            .param(AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100);
                })
                .stream().content();
    }

}
