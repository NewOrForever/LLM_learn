package org.example;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.function.DefaultFunctionCallbackBuilder;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.model.function.FunctionInvokingFunctionCallback;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * ClassName:ToolForMethodInvokingController
 * Package:org.example
 * Description:
 *
 * @Date:2025/1/24 14:20
 * @Author:qs@1.com
 */
@RestController
@RequestMapping("/tool/method")
public class ToolForMethodInvokingController {

    @Autowired
    private ChatClient chatClient;

    @RequestMapping("/getWeatherNonStatic")
    public String getWeatherNonStatic() {
        // 直接自定义 Prompt
        Prompt prompt = new Prompt("What's the weather like in San Francisco, Tokyo, and Paris?  Use Celsius.",
                OpenAiChatOptions.builder()
                        .functionCallbacks(List.of(
                                FunctionCallback.builder()
                                        /**
                                         * 设置方法名、方法参数
                                         */
                                        .method("getWeatherNonStatic", String.class, Unit.class)
                                        .description("Get weather by city name")
                                        /**
                                         * 非静态方法需要指定 targetObject
                                         */
                                        .targetObject(new TestFunctionClass()).build()
                        )).build()
        );
        return chatClient.prompt(prompt).call().content();
    }

    @RequestMapping("/getWeatherStatic")
    public String getWeatherStatic() {
        return chatClient.prompt().user("What's the weather like in San Francisco, Tokyo, and Paris?  Use Celsius.")
                .functions(
                        FunctionCallback.builder()
                                .method("getWeatherStatic", String.class, Unit.class)
                                .description("Get weather by city name")
                                /**
                                 * 静态方法需要指定 targetClass
                                 * 其实用 targetObject 也可以，因为 {@link DefaultFunctionCallbackBuilder.DefaultMethodInvokingSpec#targetObject} 中会去设置 targetClass
                                 * 实际使用中，如果是静态方法，建议使用 targetClass，如果是非静态方法，则使用 targetObject
                                 */
                                .targetClass(TestFunctionClass.class).build()
                ).call().content();
    }

    @RequestMapping("/getWeatherWithContext")
    public String getWeatherWithContext() {
        return chatClient.prompt().user("What's the weather like in San Francisco, Tokyo, and Paris?  Use Celsius.")
                .functions(
                        FunctionCallback.builder()
                                .method("getWeatherWithContext", String.class, Unit.class, ToolContext.class)
                                .description("Get weather by city name")
                                .targetObject(new TestFunctionClass())
                                .build()
                )
                /**
                 * 设置 tool context 工具上下文
                 * 工具方法中如果需要使用上下文，可以加个 ToolContext 参数
                 * function invoking 的 BiFunction 中的第二个参数默认就是 ToolContext，见{@link FunctionInvokingFunctionCallback#apply(Object, ToolContext)}
                 */
                .toolContext(Map.of("tool", "this is tool context value"))
                .call().content();
    }

    @RequestMapping("/returnVoid")
    public void returnVoid() {
        String content = chatClient.prompt().user("Turn light on in the living room.")
                .functions(
                        FunctionCallback.builder()
                                .method("turnLight", String.class, boolean.class)
                                .description("Can turn lights on or off by room name")
                                .targetObject(new TestFunctionClass()).build()
                ).call().content();
        System.out.println("response: " + content);
    }

    @RequestMapping("/noParam")
    public void noParam() {
        String content = chatClient.prompt().user("Turn light on in the living room.")
                .functions(
                        FunctionCallback.builder()
                                /**
                                 * 无参方法
                                 */
                                .method("turnLivingRoomLightOn")
                                .description("Can turn lights on or off by room name")
                                .targetObject(new TestFunctionClass()).build()
                ).call().content();
        System.out.println("response: " + content);
    }


    public static class TestFunctionClass {
        public static String getWeatherStatic(String city, Unit unit) {

            System.out.println("method called：getWeatherStatic" + "，City: " + city + " Unit: " + unit);

            double temperature = 0;
            if (city.contains("Paris")) {
                temperature = 15;
            } else if (city.contains("Tokyo")) {
                temperature = 10;
            } else if (city.contains("San Francisco")) {
                temperature = 30;
            }

            return "temperature: " + temperature + " unit: " + unit;
        }

        public String getWeatherNonStatic(String city, Unit unit) {
            return getWeatherStatic(city, unit);
        }

        public String getWeatherWithContext(String city, Unit unit, ToolContext context) {
            System.out.println("method called：getWeatherWithContext with context，context has tool key and the value is " + context.getContext().get("tool"));
            return getWeatherStatic(city, unit);
        }

        public void turnLight(String roomName, boolean on) {
            System.out.println(String.format("Turn light in room: %s to: %s", roomName, on));
        }

        public void turnLivingRoomLightOn() {
            System.out.println("turnLivingRoomLightOn true");
        }
    }

    public enum Unit {
        CELSIUS, FAHRENHEIT
    }

}


