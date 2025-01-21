package org.example;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

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
    private ChatClient chatClient;
    @Autowired
    private OpenAiImageModel openAiImageModel;
  /*
    // 测试下原型 bean
    @Autowired
    private ChatClient chatClient1;
    @Autowired
    private AProtoType aProtoType;
    @Autowired
    private AProtoType aProtoType1;*/

    @GetMapping("/chat")
    public String completion(@RequestParam(value = "message", defaultValue = "给我讲个笑话") String message) {
        String content = chatClient.prompt().user(message).call().content();
        return content;
    }

    @GetMapping("/image")
    public String image() {
        ImageResponse imageResponse = openAiImageModel.call(new ImagePrompt("A light cream colored mini golden doodle dog sitting on a hardwood floor",
                OpenAiImageOptions.builder()
                        .quality("hd")
                        .N(4)
                        .height(1024)
                        .width(1024).build())
        );

        System.out.println(imageResponse);
        return imageResponse.getResult().getOutput().getUrl();

    }

    /**
     * 测试下原型 bean
     * 原型bean 每次 getBean 都会创建一个新的实例
     */
    /*@PostConstruct
    public void init() {
        System.out.println(aProtoType);
        System.out.println(aProtoType1);
        System.out.println(chatClient);
        System.out.println(chatClient1);
    }*/

}
