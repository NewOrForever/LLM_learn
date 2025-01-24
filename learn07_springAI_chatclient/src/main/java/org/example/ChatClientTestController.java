package org.example;

import org.example.advisor.ReReadingAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * ClassName:ChatClientTestController
 * Package:org.example
 * Description: 这里就是测试下ChatClient的一些我不熟悉的功能
 * 更多功能参考官方文档：https://docs.spring.io/spring-ai/reference/1.0/api/chatclient.html
 *
 * @Date:2025/1/21 10:54
 * @Author:qs@1.com
 */
@RestController
@RequestMapping("/chatclient")
public class ChatClientTestController {

    record ActorFilms(String actor, List<String> movies) {}

    @Autowired
    private ChatClient chatClient;

    @RequestMapping("/chat")
    public String chat(String message) {
        return chatClient.prompt()
                .user(message)
                .system(promptSystemSpec -> promptSystemSpec.param("who", "鲁迅"))
                .advisors(new ReReadingAdvisor())
                .call().content();
    }

    @RequestMapping("/actorFilms")
    public List<ActorFilms> actorFilms() {
        List<ActorFilms> actorFilms = chatClient.prompt()
                .user("生成成龙和李连杰的5部电影作品名单")
                .call().entity(new ParameterizedTypeReference<List<ActorFilms>>() {
                });
        return actorFilms;
    }

    @RequestMapping("/actorFilmsMap")
    public Map<String, List<String>> actorFilmsMap() {
        Map<String, List<String>> actorFilms = chatClient.prompt()
                .user("生成成龙和李连杰的5部电影作品名单")
                .call().entity(new ParameterizedTypeReference<Map<String, List<String>>>() {
                });
        return actorFilms;
    }

}
