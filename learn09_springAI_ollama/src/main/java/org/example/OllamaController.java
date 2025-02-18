package org.example;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ollama")
public class OllamaController {
    @Autowired
    private OllamaChatModel ollamaChatModel;
    @Autowired
    private OllamaEmbeddingModel ollamaEmbeddingModel;
    @Autowired
    private ChatClient chatClient;

    @GetMapping("/chat")
    public String chat() {
        return ollamaChatModel.call("你是谁");
    }

    @GetMapping("/chatRuntimeOption")
    public String chatRuntimeOption() {
        ChatResponse response = ollamaChatModel.call(
                // OllamaOptions implements ChatOptions
                new Prompt("你是谁", OllamaOptions.builder()
                        .model("deepseek-r1:8b")
                        .build()));
        return response.getResult().getOutput().getText();
    }

    @GetMapping("/chatClient")
    public String chatClient() {
        return chatClient.prompt().user("你是谁").call().content();
    }

    @GetMapping("/embedding")
    public float[] embedding() {
        return ollamaEmbeddingModel.embed("你好");
    }

    @GetMapping("/tool")
    public String tool() {
        Prompt prompt = new Prompt("今天是日期", OllamaOptions.builder()
                .model("llama3.1:8b")
                .build());
        return chatClient.prompt(prompt)
                /**
                 * 1.0.0-M6 开始 function call 重新设计了，改成 tools calling 的方式，类似于 langchain4j
                 * 见官方文档：https://docs.spring.io/spring-ai/reference/1.0/api/tools.html
                 */
                .tools(new DateTimeTools())
                .call().content();
    }


}
