package org.example.advisor;

import org.example.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.model.MessageAggregator;
import reactor.core.publisher.Flux;

import static org.example.advisor.AdvisorOrderConstant.SIMPLE_LOGGER_ADVISOR_ORDER;

/**
 * ClassName:advisor
 * Package:org.example
 * Description: 这里是一个简单的日志增强器，用于记录未被增强过的请求和响应信息
 * 在 {@link Config#chatClient(ChatClient.Builder)} 中配置了默认的增强器
 * 推荐使用 {@link org.springframework.ai.chat.client.ChatClient.Builder#defaultAdvisors(Advisor...)} 设置默认增强器
 * 当然也可以在 runtime 中通过 {@link ChatClient.AdvisorSpec#advisors(Advisor...)} 添加增强器
 *
 * @Date:2025/1/21 15:46
 * @Author:qs@1.com
 */
public class MySimpleLoggerAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {
    private static final Logger logger = LoggerFactory.getLogger(MySimpleLoggerAdvisor.class);

    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        // 该 advisor 因为设置了优先级最高，所以最先执行 request
        logger.debug("BEFORE: {}", advisedRequest);

        advisedRequest = advisedRequest.updateContext(context -> {
            context.put("aroundCallBefore" + getName(), "AROUND_CALL_BEFORE " + getName());
            context.put("lastBefore", getName());
            return context;
        });

        AdvisedResponse advisedResponse = chain.nextAroundCall(advisedRequest);
        // 该 advisor 因为设置了优先级最高，所以最后执行 response
        logger.debug("AFTER: {}", advisedResponse);
        System.out.println("响应后 advised 上下文信息：" + advisedResponse.adviseContext());
        return advisedResponse;
    }

    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        logger.debug("BEFORE: {}", advisedRequest);
        Flux<AdvisedResponse> advisedResponseFlux = chain.nextAroundStream(advisedRequest);
        return new MessageAggregator().aggregateAdvisedResponse(advisedResponseFlux,
                advisedResponse -> logger.debug("AFTER: {}", advisedResponse));
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return SIMPLE_LOGGER_ADVISOR_ORDER;
    }

}
