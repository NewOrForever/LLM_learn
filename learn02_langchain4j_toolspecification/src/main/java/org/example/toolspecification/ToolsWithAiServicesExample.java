package org.example.toolspecification;

import com.google.common.collect.Lists;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ClassName:ToolsWithAiServicesExample
 * Package:org.example.toolspecification
 * Description: langchain4j 之 function call 演示
 * 通过 AiServices 来简化 ToolSpecification 的使用
 *
 * @Date:2024/11/14 15:13
 * @Author:qs@1.com
 */
public class ToolsWithAiServicesExample {
    static class MyTools {
        @Tool("获取当前日期")
        public String now() {
            return LocalDateTime.now().toString();
        }

        @Tool("获取指定日期注册的用户信息")
        public static List<User> getUserInfo(String date) {
            System.out.println("接收到的date参数的值：" + date);
            User user1 = new User("周瑜", 18);
            User user2 = new User("曹操", 18);
            return Lists.newArrayList(user1, user2);
        }

        @Tool("给指定用户发送邮件")
        public void email(String user) {
            System.out.println("发送邮件：" + user);
        }
    }

    interface UserAiService {
        @SystemMessage("先获取具体日期，然后再解决用户问题")
        String getUserInfo(String desc);
    }

    public static void main(String[] args) {
        // 配置大模型
        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .apiKey("demo")
                .modelName(OpenAiChatModelName.GPT_4_O_MINI)
                .build();

        // 构建  AI 服务
        UserAiService userAiService = AiServices.builder(UserAiService.class)
                .chatLanguageModel(chatModel)
                .tools(new MyTools())
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();

        String content = userAiService.getUserInfo("获取今天注册的新用户信息");
        // String content = userAiService.getUserInfo("获取今天注册的新用户信息，然后基于这些用户发送一份邮件");
        System.out.println(content);
    }

    static class User {
        private String username;
        private Integer age;

        public User(String username, Integer age) {
            this.username = username;
            this.age = age;
        }
    }

}
