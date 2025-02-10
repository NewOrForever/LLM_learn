package org.example.advanced_rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.DefaultChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName:QuestionAnswerAdvisorTestController
 * Package:org.example.advanced_rag
 * Description: 测试 Spring AI RAG 模块 - {@link org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor}
 *
 * @Date:2025/2/8 15:44
 * @Author:qs@1.com
 */
@RestController
@RequestMapping("/questionAnswerAdvisor")
public class QuestionAnswerAdvisorRAGTestController {
    @Autowired
    private ChatClient chatClient;
    @Autowired
    @Qualifier("redis")
    private VectorStore vectorStore;
    @RequestMapping("/search")
    public String search(String message) {
        return chatClient.prompt()
                .user(message)
                .advisors(new QuestionAnswerAdvisor(vectorStore))
                .call().content();
    }

    @RequestMapping("/metadataSearch")
    public String metadataSearch(String message) {
        return chatClient.prompt()
                .user(message)
                .advisors(advisorSpec -> {
                    advisorSpec.advisors(QuestionAnswerAdvisor.builder(vectorStore)
                            .searchRequest(SearchRequest.builder().similarityThreshold(0.75).build())
                            .build());
                    /**
                     * 会在 {@link DefaultChatClient#toAdvisedRequest(DefaultChatClient.DefaultChatClientRequestSpec, String)}
                     * 添加到 Advisor context 上下文 -> 供 QuestionAnswerAdvisor 设置 FilterExpression（这个会覆盖 SearchRequest 中设置的 FilterExpression）
                     */
                    advisorSpec.param(QuestionAnswerAdvisor.FILTER_EXPRESSION, "chunkNumber > 27");
                })
                .call().content();
    }

}
