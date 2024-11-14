package org.example.langchain4j;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.Tokenizer;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.output.Response;

import java.net.Proxy;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * ClassName:ResponseStreamingTest
 * Package:org.example.langchain4j
 * Description: langchain4j 打字机式流式响应测试
 *
 * @Date:2024/11/12 8:37
 * @Author:qs@1.com
 */
public class ResponseStreamingTest {
    public static void main(String[] args) {
        StreamingChatLanguageModel streamingChat = OpenAiStreamingChatModel.builder()
                /**
                 * @see OpenAiChatModel#OpenAiChatModel(String, String, String, String, Double, Double, List, Integer, Integer, Double, Double, Map, String, Boolean, Integer, String, Boolean, Boolean, Duration, Integer, Proxy, Boolean, Boolean, Tokenizer, Map, List)
                 * @see dev.langchain4j.model.openai.InternalOpenAiHelper#OPENAI_DEMO_URL
                 * 当 apiKey 为 demo 时，baseUrl 为 http://langchain4j.dev/demo/openai/v1，由langchain4j 代理到 OpenAI
                 *
                 * 这里使用 demo 作为 apiKey 时，需要设置 baseUrl了，因为 StreamingChatLanguageModel 的构造方法中没有为 demo 这个 apiKey 设置默认的 baseUrl
                 */
                .baseUrl("http://langchain4j.dev/demo/openai/v1")
                .apiKey("demo")
                .modelName(OpenAiChatModelName.GPT_4_O_MINI)
                .build();

        // StreamingChatLanguageModel 流式响应在使用中文回复时会出现乱码，暂时还不确定是不是 langchain4j 的问题，还是需要修改什么配置
        streamingChat.generate("hello, who are you?", new StreamingResponseHandler<AiMessage>() {
            @Override
            public void onNext(String token) {
                System.out.println(token);
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onComplete(Response<AiMessage> response) {
                System.out.println(response);
            }

            @Override
            public void onError(Throwable error) {
                error.printStackTrace();
            }
        });
    }

}
