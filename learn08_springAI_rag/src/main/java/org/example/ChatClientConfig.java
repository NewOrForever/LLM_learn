package org.example;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ClassName:ChatClientConfig
 * Package:org.example.rag_etl
 * Description:
 *
 * @Date:2025/1/27 15:46
 * @Author:qs@1.com
 */
@Configuration
public class ChatClientConfig {
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder.build();
    }
}
