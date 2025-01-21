package org.example.practice;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.model.function.FunctionCallbackWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName:UserController
 * Package:org.example.practice
 * Description:
 *
 * @Date:2025/1/20 16:45
 * @Author:qs@1.com
 */
@RestController
public class UserController {

    @Autowired
    private ChatClient chatClient;

    @RequestMapping("/user")
    public String user(String message) {
        return chatClient.prompt().user(message)
                .functions("dateNowService")
                .functions(FunctionCallback.builder()
                        .function("userService", new UserService())
                        .description("获取指定时间的注册用户")
                        .inputType(UserService.Request.class)
                        .build())
                .call().content();
    }

}
