package org.example.vector;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModelName;
import dev.langchain4j.model.output.Response;

/**
 * ClassName:HelloWorld
 * Package:org.example.vector
 * Description: langchain4j 之向量化演示
 *
 * @Date:2024/11/20 16:57
 * @Author:qs@1.com
 */
public class HelloWorld {
    public static void main(String[] args) {
        OpenAiEmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder()
                .apiKey("demo")
                .modelName(OpenAiEmbeddingModelName.TEXT_EMBEDDING_3_SMALL)
                .build();

        Response<Embedding> embed = embeddingModel.embed("你好，我叫周瑜");
        System.out.println(embed.content());
        System.out.println(embed.content().vector().length);
    }

}
