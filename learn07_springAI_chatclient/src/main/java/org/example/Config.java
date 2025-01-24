package org.example;

import org.example.advisor.MySimpleLoggerAdvisor;
import org.springframework.ai.autoconfigure.chat.client.ChatClientAutoConfiguration;
import org.springframework.ai.autoconfigure.chat.client.ChatClientBuilderConfigurer;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.AbstractToolCallSupport;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.model.function.FunctionCallbackResolver;
import org.springframework.ai.model.function.FunctionCallbackWrapper;
import org.springframework.ai.model.function.FunctionCallingOptions;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

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
        /**
         * 设置默认 systemMessage
         * 当然还可以设置其他的默认属性
         */
        builder.defaultSystem("你是一个友好的聊天机器人，用 {who} 的口吻回答问题！").defaultAdvisors(new MySimpleLoggerAdvisor());
        return builder.build();
    }

    @Bean
    public ChatMemory chatMemory() {
        return new InMemoryChatMemory();
    }

    /**
     * 同一个 ChatModel 可以添加到多个 ChatClient 中
     * 因为 ChatClient.Builder 是一个原型 Bean，每次 getBean 都会创建一个新的 ChatClient.Builder
     * @see ChatClientAutoConfiguration#chatClientBuilder(ChatClientBuilderConfigurer, ChatModel, ObjectProvider, ObjectProvider)
     */
    @Bean
    public ChatClient advisorChatClient(ChatClient.Builder builder, ChatMemory chatMemory) {

        return builder.defaultAdvisors(
                new MessageChatMemoryAdvisor(chatMemory),
                /**
                 * SimpleLoggerAdvisor 默认是直接将 request 和 response 执行 toString() 方法输出
                 * 我们可以自定义 request 和 response 的输出格式，本质就是传入一个 Function 函数
                 */
                new SimpleLoggerAdvisor(
                        /**
                         * 自定义 request 和 response 的输出内容：request 的 messages 和 response 的 result
                         */
                        advisedRequest -> "Custom request: " + advisedRequest.messages(),
                        advisedResponse -> "Custom response: " + advisedResponse.getResult(),
            0
                ))
                .build();
    }

}

