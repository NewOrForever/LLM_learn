package org.example;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.autoconfigure.chat.client.ChatClientAutoConfiguration;
import org.springframework.ai.autoconfigure.chat.client.ChatClientBuilderConfigurer;
import org.springframework.ai.autoconfigure.openai.OpenAiAutoConfiguration;
import org.springframework.ai.autoconfigure.zhipuai.ZhiPuAiAutoConfiguration;
import org.springframework.ai.autoconfigure.zhipuai.ZhiPuAiChatProperties;
import org.springframework.ai.autoconfigure.zhipuai.ZhiPuAiConnectionProperties;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.observation.ChatModelObservationConvention;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.model.function.FunctionCallbackResolver;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * ClassName:Config
 * Package:org.example
 * Description:
 *
 * @Date:2025/1/4 14:53
 * @Author:qs@1.com
 */
@Configuration
public class Config {
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder.build();
    }


/**
     * 因为我依赖了 OpenAI 和 ZhiPuAI
     * - {@link ZhiPuAiAutoConfiguration} 会自动配置 ZhiPuAI 的 ChatModel
     * - {@link OpenAiAutoConfiguration} 会自动配置 OpenAI 的 ChatModel
     * @see ChatClientAutoConfiguration#chatClientBuilder(ChatClientBuilderConfigurer, ChatModel, ObjectProvider, ObjectProvider) 会自动装配 {@link ChatModel}
     * 如果我不想在 pom.xml 中排除 OpenAI 或 ZhiPuAI 的依赖，那么我就得使用 @Primary 注解来指定默认的 ChatModel
     * 下面的代码直接是复制 {@link ZhiPuAiAutoConfiguration#zhiPuAiChatModel} 方法
     *
     * 如果要多个模型配合使用：
     *  - 可以把 {@link ChatClientAutoConfiguration#chatClientBuilder} 方法复制出来然后自己配置需要的模型
     *  - 推荐：不用 {@link ChatClient}，直接使用 {@link ChatModel}，但是需要 spring.ai.chat.client.enable = false 关闭自动装配 {@link ChatClient}
     *  - 推荐：使用 {@link ChatClient}，但是需要自己创建 {@link ChatClient}，也需要 spring.ai.chat.client.enable = false 关闭自动装配 {@link ChatClient}
     *      - 通过 {@link ChatClient.Builder} 创建 {@link ChatClient}
     *      - 通过 {@link ChatClient#create(ChatModel)} 使用默认的 builder 创建 {@link ChatClient}
     */
/*

    @Primary
    @Bean
    @ConditionalOnProperty(prefix = ZhiPuAiChatProperties.CONFIG_PREFIX, name = "enabled", havingValue = "true",
            matchIfMissing = true)
    public ZhiPuAiChatModel zhiPuAiChatModel(ZhiPuAiConnectionProperties commonProperties,
                                             ZhiPuAiChatProperties chatProperties, ObjectProvider<RestClient.Builder> restClientBuilderProvider,
                                             List<FunctionCallback> toolFunctionCallbacks, FunctionCallbackResolver functionCallbackResolver,
                                             RetryTemplate retryTemplate, ResponseErrorHandler responseErrorHandler,
                                             ObjectProvider<ObservationRegistry> observationRegistry,
                                             ObjectProvider<ChatModelObservationConvention> observationConvention) {

        var zhiPuAiApi = zhiPuAiApi(chatProperties.getBaseUrl(), commonProperties.getBaseUrl(),
                chatProperties.getApiKey(), commonProperties.getApiKey(),
                restClientBuilderProvider.getIfAvailable(RestClient::builder), responseErrorHandler);

        var chatModel = new ZhiPuAiChatModel(zhiPuAiApi, chatProperties.getOptions(), functionCallbackResolver,
                toolFunctionCallbacks, retryTemplate, observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP));

        observationConvention.ifAvailable(chatModel::setObservationConvention);

        return chatModel;
    }
    private ZhiPuAiApi zhiPuAiApi(String baseUrl, String commonBaseUrl, String apiKey, String commonApiKey,
                                  RestClient.Builder restClientBuilder, ResponseErrorHandler responseErrorHandler) {

        String resolvedBaseUrl = StringUtils.hasText(baseUrl) ? baseUrl : commonBaseUrl;
        Assert.hasText(resolvedBaseUrl, "ZhiPuAI base URL must be set");

        String resolvedApiKey = StringUtils.hasText(apiKey) ? apiKey : commonApiKey;
        Assert.hasText(resolvedApiKey, "ZhiPuAI API key must be set");

        return new ZhiPuAiApi(resolvedBaseUrl, resolvedApiKey, restClientBuilder, responseErrorHandler);
    }
*/



    // 测试原型 Bean
    /*@Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public AProtoType aProtoType() {
        return new AProtoType();
    }*/

}
