package org.example.chatmemory;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.util.List;
import java.util.Map;

import static dev.langchain4j.data.message.ChatMessageDeserializer.messagesFromJson;
import static dev.langchain4j.data.message.ChatMessageSerializer.messagesToJson;
import static org.mapdb.Serializer.STRING;

/**
 * ClassName:ChatMemoryDemo
 * Package:org.example.chatmemory
 * Description: langchain4j 之 ChatMemory 持久化示例
 * 官方示例参考：https://github.com/langchain4j/langchain4j-examples/blob/main/other-examples/src/main/java/ServiceWithPersistentMemoryExample.java
 *
 * @Date:2024/11/13 10:19
 * @Author:qs@1.com
 */
public class ChatMemoryPersistentForSingleExample {
    interface NamingMaster {
        String talk(String desc);

        static NamingMaster create() {
            OpenAiChatModel chatModel = OpenAiChatModel.builder()
                    .apiKey("demo")
                    .modelName(OpenAiChatModelName.GPT_4_O_MINI)
                    .build();
            ChatMemory chatMemory = MessageWindowChatMemory.builder()
                    .maxMessages(10)
                    .chatMemoryStore(new PersistentChatMemoryStore())
                    .build();

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

    // You can create your own implementation of ChatMemoryStore and store chat memory whenever you'd like
    static class PersistentChatMemoryStore implements ChatMemoryStore {

        private final DB db = DBMaker.fileDB("chat-memory.db").transactionEnable().make();
        private final Map<String, String> map = db.hashMap("messages", STRING, STRING).createOrOpen();

        @Override
        public List<ChatMessage> getMessages(Object memoryId) {
            String json = map.get((String) memoryId);
            return messagesFromJson(json);
        }

        @Override
        public void updateMessages(Object memoryId, List<ChatMessage> messages) {
            String json = messagesToJson(messages);
            map.put((String) memoryId, json);
            db.commit();
        }

        @Override
        public void deleteMessages(Object memoryId) {
            map.remove((String) memoryId);
            db.commit();
        }
    }

}
