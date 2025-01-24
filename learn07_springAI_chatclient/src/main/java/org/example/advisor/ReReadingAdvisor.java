package org.example.advisor;

import org.example.ChatClientTestController;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.*;
import reactor.core.publisher.Flux;

import java.util.Map;

import static org.example.advisor.AdvisorOrderConstant.RE_READING_ADVISOR_ORDER;

/**
 * ClassName:ReReadingAdvisor
 * Package:org.example.advisor
 * Description: re-reading advisor 用于提高大模型的推理能力
 * re-reading 核心提示词：
 * {Input_Query}
 * Read the question again: {Input_Query}
 *
 * 为了测试下 runtime 中通过  {@link ChatClient.AdvisorSpec#advisors(Advisor...)} 添加增强器，该 Advisor 在 runtime {@link ChatClientTestController#chat(String)} 中添加
 * @Date:2025/1/21 16:24
 * @Author:qs@1.com
 */
public class ReReadingAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {
    private AdvisedRequest before(AdvisedRequest advisedRequest) {
        System.out.println("advised 上下文信息：" + advisedRequest.adviseContext());

        Map<String, Object> advisedUserParam = advisedRequest.userParams();
        String userMessage = advisedRequest.userText();
        advisedUserParam.put("re2_input_query", userMessage);

        return AdvisedRequest.from(advisedRequest)
                .userText("""
                        {re2_input_query}
                        Read the question again: {re2_input_query}
                        """)
                .userParams(advisedUserParam)
                .build();
    }

    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        return chain.nextAroundCall(this.before(advisedRequest));
    }

    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        return chain.nextAroundStream(this.before(advisedRequest));
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return RE_READING_ADVISOR_ORDER;
    }

}
