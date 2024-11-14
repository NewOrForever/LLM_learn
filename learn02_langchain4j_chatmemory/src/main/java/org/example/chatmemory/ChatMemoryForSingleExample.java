package org.example.chatmemory;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.service.AiServices;

/**
 * ClassName:ChatMemoryDemo
 * Package:org.example.chatmemory
 * Description: langchain4j 之 ChatMemory 示例
 * 官方示例参考：https://github.com/langchain4j/langchain4j-examples/blob/main/other-examples/src/main/java/ServiceWithMemoryExample.java
 *
 * @Date:2024/11/13 10:19
 * @Author:qs@1.com
 */
public class ChatMemoryForSingleExample {
    interface NamingMaster {
        String talk(String desc);

        static NamingMaster create() {
            OpenAiChatModel chatModel = OpenAiChatModel.builder()
                    .apiKey("demo")
                    .modelName(OpenAiChatModelName.GPT_4_O_MINI)
                    .build();
             ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);

            /* // 测试 TokenWindowChatMemory 使用时，对 List<ChatMessage> 估算 token 数超出 maxTokens 时，会清除旧的 ChatMessage
            TokenWindowChatMemory chatMemory = TokenWindowChatMemory.withMaxTokens(100,
                    new OpenAiTokenizer(OpenAiChatModelName.GPT_4_O_MINI));*/

            return AiServices.builder(NamingMaster.class)
                    .chatLanguageModel(chatModel)
                    .chatMemory(chatMemory)
                    .build();
        }
    }

    public static void main(String[] args) {
        NamingMaster namingMaster = NamingMaster.create();
        System.out.println(namingMaster.talk("帮我取一个很有中国文化内涵的男孩名字，给我一个你觉得最好的就行了"));
        System.out.println("------");
        System.out.println(namingMaster.talk("换一个"));
    }

}
