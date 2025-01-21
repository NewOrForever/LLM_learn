package org.example;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.AbstractToolCallSupport;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.model.function.FunctionCallbackResolver;
import org.springframework.ai.model.function.FunctionCallbackWrapper;
import org.springframework.ai.model.function.FunctionCallingOptions;
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
        return builder.build();
    }

    /**
     * @see org.springframework.ai.autoconfigure.openai.OpenAiAutoConfiguration#openAiChatModel(org.springframework.ai.autoconfigure.openai.OpenAiConnectionProperties, org.springframework.ai.autoconfigure.openai.OpenAiChatProperties, org.springframework.beans.factory.ObjectProvider, org.springframework.beans.factory.ObjectProvider, java.util.List, org.springframework.ai.model.function.FunctionCallbackResolver, org.springframework.retry.support.RetryTemplate, org.springframework.web.client.ResponseErrorHandler, org.springframework.beans.factory.ObjectProvider, org.springframework.beans.factory.ObjectProvider)
     * @see AbstractToolCallSupport#AbstractToolCallSupport(FunctionCallbackResolver, FunctionCallingOptions, List)
     * 会在@Bean OpenaAiChatModel 时注入 FunctionCallback，并在创建 OpenAIChatModel 时将 FunctionCallback 注册到
     * @see AbstractToolCallSupport#functionCallbackRegister 中
     * 但是要启用 FunctionCallback，还需要在 runtime ChatOptions 中显示设置 functions(functionCallback name)
     *
     * 当然也可以直接在 runtime ChatOptions 中设置 functionCallbacks(List<FunctionCallback>)，这样就不需要 @Bean FunctionCallback 了
     */
    /*@Bean
    public FunctionCallback functionCallbackForDefaultOptions() {
        return FunctionCallback.builder()
                .function("dateService", new DateService())
                .description("获取指定地点的当前时间")
                .inputType(DateService.Request.class)
                .build();
    }*/

}

