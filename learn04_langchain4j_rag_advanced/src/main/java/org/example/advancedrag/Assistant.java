package org.example.advancedrag;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.experimental.rag.content.retriever.sql.SqlDatabaseContentRetriever;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModelName;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.injector.ContentInjector;
import dev.langchain4j.rag.content.injector.DefaultContentInjector;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.router.DefaultQueryRouter;
import dev.langchain4j.rag.query.transformer.QueryTransformer;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.redis.RedisEmbeddingStore;
import org.h2.jdbcx.JdbcDataSource;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

/**
 * ClassName:Assistant
 * Package:org.example.advancedrag
 * Description:
 *
 * @Date:2024/11/27 13:46
 * @Author:qs@1.com
 */
public interface Assistant {
    /**
     * 当 AiSerivce 中有多个方法时，为了避免历史消息的交叉混乱，给每个方法加个 @MemoryId 注解的参数用来区分历史消息
     * 参数值可以传入：chat、answer
     * 如果还涉及到了多用户的话，@MemoryId 可以加上用户的唯一标识
     * 参数值可以传入：chat:userId、answer:userId
     *
     * 这里只是为了测试各种 RAG 组件的高级功能，所以就不加 @MemoryId 了
     */

    String answer(String question);

    Result<String> chat(String userMessage);

    TokenStream chatStream(String userMessage);

    static Assistant create() {
        return create(null);
    }

    static Assistant create(QueryTransformer queryTransformer) {
        OpenAiChatModel chatModel = getOpenAiChatModel();

        EmbeddingStore<TextSegment> embeddingStore = getTextSegmentEmbeddingStore();

        OpenAiEmbeddingModel embeddingModel = getOpenAiEmbeddingModel();

        ContentRetriever contentRetriever = getContentRetriever(embeddingStore, embeddingModel);

        DefaultQueryRouter defaultQueryRouter = getDefaultQueryRouter(contentRetriever);

        // 创建检索增强器
        RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                .queryRouter(defaultQueryRouter)
                .queryTransformer(queryTransformer)
                .build();

        return AiServices.builder(Assistant.class)
                .chatLanguageModel(chatModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .retrievalAugmentor(retrievalAugmentor)
                .build();
    }

    static Assistant createSqlAssistant() {
        OpenAiChatModel chatLanguageModel = getOpenAiChatModel();
        return createSqlAssistant(chatLanguageModel);
    }

    static Assistant createSqlAssistant(ChatLanguageModel chatLanguageModel) {
        DataSource dataSource = createDataSource();

        ContentRetriever contentRetriever = SqlDatabaseContentRetriever.builder()
                .dataSource(dataSource)
                .chatLanguageModel(chatLanguageModel)
                .build();

        return AiServices.builder(Assistant.class)
                .chatLanguageModel(chatLanguageModel)
                .contentRetriever(contentRetriever)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
    }


    static Assistant createSqlAssistant(StreamingChatLanguageModel streamingChatLanguageModel) {
        OpenAiChatModel openAiChatModel = getOpenAiChatModel();

        return createSqlAssistant(streamingChatLanguageModel, openAiChatModel);
    }

    static Assistant createSqlAssistant(StreamingChatLanguageModel streamingChatLanguageModel, ChatLanguageModel chatLanguageModel) {
        DataSource dataSource = createDataSource();

        ContentRetriever contentRetriever = SqlDatabaseContentRetriever.builder()
                .dataSource(dataSource)
                .chatLanguageModel(chatLanguageModel)
                .build();

        return AiServices.builder(Assistant.class)
                .streamingChatLanguageModel(streamingChatLanguageModel)
                .contentRetriever(contentRetriever)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
    }

    static Assistant createSqlAssistantWithContentInjector() {

        DataSource dataSource = createDataSource();

        OpenAiChatModel chatLanguageModel = getOpenAiChatModel();

        ContentRetriever contentRetriever = SqlDatabaseContentRetriever.builder()
                .dataSource(dataSource)
                .chatLanguageModel(chatLanguageModel)
                .build();

        // Each retrieved segment should include "file_name" and "index" metadata values in the prompt
        ContentInjector contentInjector = DefaultContentInjector.builder()
                // .promptTemplate(...) // Formatting can also be changed
                .metadataKeysToInclude(Arrays.asList("file_name", "index"))
                .build();

        // 创建检索增强器
        RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                .contentRetriever(contentRetriever)
                .contentInjector(contentInjector)
                .build();

        return AiServices.builder(Assistant.class)
                .chatLanguageModel(chatLanguageModel)
                .retrievalAugmentor(retrievalAugmentor)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
    }

    private static DataSource createDataSource() {

        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");
        dataSource.setPassword("sa");

        String createTablesScript = read("sql/create_tables.sql");
        execute(createTablesScript, dataSource);

        String prefillTablesScript = read("sql/prefill_tables.sql");
        execute(prefillTablesScript, dataSource);

        return dataSource;
    }

    private static String read(String path) {
        try {
            return new String(Files.readAllBytes(toPath(path)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static Path toPath(String relativePath) {
        try {
            URL fileUrl = Assistant.class.getClassLoader().getResource(relativePath);
            return Paths.get(fileUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static void execute(String sql, DataSource dataSource) {
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            for (String sqlStatement : sql.split(";")) {
                statement.execute(sqlStatement.trim());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private static DefaultQueryRouter getDefaultQueryRouter(ContentRetriever contentRetriever) {
        DefaultQueryRouter defaultQueryRouter = new DefaultQueryRouter(contentRetriever);
        return defaultQueryRouter;
    }

    private static ContentRetriever getContentRetriever(EmbeddingStore<TextSegment> embeddingStore, OpenAiEmbeddingModel embeddingModel) {
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .minScore(0.8)
                .maxResults(5)
                .build();
        return contentRetriever;
    }

    private static OpenAiEmbeddingModel getOpenAiEmbeddingModel() {
        OpenAiEmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder()
                .apiKey("demo")
                .modelName(OpenAiEmbeddingModelName.TEXT_EMBEDDING_3_SMALL)
                .build();
        return embeddingModel;
    }

    private static EmbeddingStore<TextSegment> getTextSegmentEmbeddingStore() {
        EmbeddingStore<TextSegment> embeddingStore = RedisEmbeddingStore.builder()
                .host("101.126.54.128")
                .port(16379)
                .dimension(OpenAiEmbeddingModelName.TEXT_EMBEDDING_3_SMALL.dimension())
                .build();
        return embeddingStore;
    }

    static OpenAiChatModel getOpenAiChatModel() {
        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .apiKey("demo")
                .modelName(OpenAiChatModelName.GPT_4_O_MINI)
                .build();
        return chatModel;
    }

}
