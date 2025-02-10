package org.example.contribute_for_springai;

import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ClassName:Config
 * Package:org.example.contribute_for_springai
 * Description:
 *
 * @Date:2025/1/26 15:26
 * @Author:qs@1.com
 */
@Configuration
public class Config {

    @Bean
    public MyOpenAiEmbeddingModel myOpenAiEmbeddingModel() {
        OpenAiEmbeddingOptions options = OpenAiEmbeddingOptions.builder().model("gpt-3.5-turbo").build();
        return new MyOpenAiEmbeddingModel(options);
    }


    @Bean
    public MyZhipuAiEmbeddingModel myZhipuAiEmbeddingModel() {
        OpenAiEmbeddingOptions options = OpenAiEmbeddingOptions.builder()
                .dimensions(768)
                .model("gpt-3.5-turbo").build();
        return new MyZhipuAiEmbeddingModel(options);
    }
}
