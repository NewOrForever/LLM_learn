package org.example.chatmemory;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.service.AiServiceContext;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;

/**
 * ClassName:ChatMemoryDemo
 * Package:org.example.chatmemory
 * Description: langchain4j 之 ChatMemory 示例
 * 官方示例参考：https://github.com/langchain4j/langchain4j-examples/blob/main/other-examples/src/main/java/ServiceWithMemoryForEachUserExample.java
 *
 * @Date:2024/11/13 10:19
 * @Author:qs@1.com
 */
public class ChatMemoryForEachUserExample {
    interface NamingMaster {
        String talk(@MemoryId int memoryId, @UserMessage String desc);

        static NamingMaster create() {
            OpenAiChatModel chatModel = OpenAiChatModel.builder()
                    .apiKey("demo")
                    .modelName(OpenAiChatModelName.GPT_4_O_MINI)
                    .build();

            return AiServices.builder(NamingMaster.class)
                    .chatLanguageModel(chatModel)
                    /**
                     * 这里为什么不设置 ChatMemory 的 id？
                     * @see AiServiceContext#chatMemory(Object)
                     * - 多用户的核心在于 {@link AiServiceContext#chatMemories} 这个 ConcurrentHashMap
                     * - 虽然如果 ChatMemory 的 id 为 null，会导致使用默认的 default 作为 ChatMemory 的 id，每个用户还是以 @MemoryId 为 key 存储在 ConcurrentHashMap 中的
                     * - 在这里没有涉及到持久化的类型不匹配问题，所以可以不设置 id
                     * 但是最好还是设置 id，以免出现不必要的问题
                     */
                    .chatMemoryProvider((memoryId) -> MessageWindowChatMemory.withMaxMessages(10))
                    .build();
        }
    }

    public static void main(String[] args) {
        NamingMaster namingMaster = NamingMaster.create();
        System.out.println(namingMaster.talk(1, "你好，我叫周瑜"));
        System.out.println(namingMaster.talk(2, "你好，我叫诸葛亮"));
        System.out.println("------");
        System.out.println(namingMaster.talk(1, "我叫什么"));
        System.out.println(namingMaster.talk(2, "我叫什么"));
    }

}
