package org.example.chatmemory;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import java.util.List;
import java.util.Map;

import static dev.langchain4j.data.message.ChatMessageDeserializer.messagesFromJson;
import static dev.langchain4j.data.message.ChatMessageSerializer.messagesToJson;
import static org.mapdb.Serializer.STRING;

/**
 * ClassName:ChatMemoryDemo
 * Package:org.example.chatmemory
 * Description: langchain4j 之 ChatMemory 多用户持久化示例
 * 官方示例参考：https://github.com/langchain4j/langchain4j-examples/blob/main/other-examples/src/main/java/ServiceWithPersistentMemoryForEachUserExample.java
 *
 * @Date:2024/11/13 10:19
 * @Author:qs@1.com
 */
public class ChatMemoryPersistentForEachUserExample {
    interface NamingMaster {
        String talk(@MemoryId long userId, @UserMessage String desc);

        static NamingMaster create() {
            OpenAiChatModel chatModel = OpenAiChatModel.builder()
                    .apiKey("demo")
                    .modelName(OpenAiChatModelName.GPT_4_O_MINI)
                    .build();

            PersistentChatMemoryStore persistentStore = new PersistentChatMemoryStore();

            return AiServices.builder(NamingMaster.class)
                    .chatLanguageModel(chatModel)
                    /**
                     * 使用 ChatMemoryProvider 为每个用户提供一个 ChatMemory 实例
                     * 每个用户的 ChatMemory 实例需要有一个唯一的 id
                     *  - 不要漏了设置 id 这一步，不然 ChatMemory 会使用默认的 default 作为 id，导致持久化的 memoryId 类型不匹配
                     */
                    .chatMemoryProvider((userId) -> MessageWindowChatMemory.builder()
                            .id(userId)
                            .maxMessages(10)
                            .chatMemoryStore(persistentStore)
                            .build())
                    .build();
        }
    }

    public static void main(String[] args) {
        NamingMaster namingMaster = NamingMaster.create();
        System.out.println(namingMaster.talk(1L, "你好，我叫周瑜"));
        System.out.println(namingMaster.talk(2L, "你好，我叫诸葛亮"));
        System.out.println("------");
        System.out.println(namingMaster.talk(1L, "我叫什么"));
        System.out.println(namingMaster.talk(2L, "我叫什么"));
    }

    // You can create your own implementation of ChatMemoryStore and store chat memory whenever you'd like
    static class PersistentChatMemoryStore implements ChatMemoryStore {

        private final DB db = DBMaker.fileDB("multi-user-chat-memory.db")
                .transactionEnable()
                .make();
        private final Map<Long, String> map = db.hashMap("messages", Serializer.LONG, STRING).createOrOpen();

        @Override
        public List<ChatMessage> getMessages(Object memoryId) {
            String json = map.get((long) memoryId);
            return messagesFromJson(json);
        }

        @Override
        public void updateMessages(Object memoryId, List<ChatMessage> messages) {
            String json = messagesToJson(messages);
            map.put((long) memoryId, json);
            db.commit();
        }

        @Override
        public void deleteMessages(Object memoryId) {
            map.remove((long) memoryId);
            db.commit();
        }
    }

}
