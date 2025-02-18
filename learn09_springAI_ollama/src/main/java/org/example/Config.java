package org.example;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ClassName:Config
 * Package:org.example
 * Description:
 *
 * @Date:2025/1/17 17:30
 * @Author:qs@1.com
 */
@Configuration
public class Config {
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder.build();
    }

}

