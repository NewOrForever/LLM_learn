package org.example.advancedrag;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.Filter;
import dev.langchain4j.store.embedding.filter.MetadataFilterBuilder;
import dev.langchain4j.store.embedding.filter.builder.sql.LanguageModelSqlFilterBuilder;
import dev.langchain4j.store.embedding.filter.builder.sql.TableDefinition;
import dev.langchain4j.store.embedding.filter.logical.And;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.store.embedding.redis.RedisEmbeddingStore;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ClassName:TestMetadataFilteringExamples
 * Package:org.example.advancedrag
 * Description:
 *
 * @Date:2024/11/29 17:07
 * @Author:qs@1.com
 */
public class TestMetadataFilteringExamples {

    ChatLanguageModel chatLanguageModel = OpenAiChatModel.builder()
            .apiKey("demo")
            .modelName(OpenAiChatModelName.GPT_4_O_MINI)
            .build();

    EmbeddingModel embeddingModel = new BgeSmallEnV15QuantizedEmbeddingModel();

    @Test
    void Static_Metadata_Filter_Example() {

        // 指定文本内容的元数据
        TextSegment dogsSegment = TextSegment.from("Article about dogs ...", Metadata.metadata("animal", "dog"));
        TextSegment birdsSegment = TextSegment.from("Article about birds ...", Metadata.metadata("animal", "bird"));

        // 文本向量化存储
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        embeddingStore.add(embeddingModel.embed(dogsSegment).content(), dogsSegment);
        embeddingStore.add(embeddingModel.embed(birdsSegment).content(), birdsSegment);
        // embeddingStore contains segments about both dogs and birds

        // 创建一个静态过滤器，只搜索元数据中的 animal 这个 key 的值为 dog 的文本
        Filter onlyDogs = MetadataFilterBuilder.metadataKey("animal").isEqualTo("dog");

        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                /**
                 * 通过指定静态过滤器，我们限制搜索范围仅限于文本元数据中 animal 这个 key 的值为 dog 的文本
                 * 如果不指定静态过滤器，那么搜索范围将包括所有文本
                 *
                 * @see InMemoryEmbeddingStore#search(EmbeddingSearchRequest) 内存向量存储的搜索方法使用到了 filter
                 * 有些 EmbeddingStore 的实现没有使用到 filter，这时候 filter 就不会生效，比如：
                 * @see RedisEmbeddingStore，RedisEmbeddingStore 的 search 方法使用的是接口默认的 {@link EmbeddingStore#search(EmbeddingSearchRequest)}
                 * 最终执行的是 {@link RedisEmbeddingStore#findRelevant(Embedding, int, double)}，filter 没有传入该方法
                 *
                 * 所以有些 EmbeddingStore 的实现可能不支持 filter，需要自己继承这些实现类，重写 search 方法
                 * 测试的时候我们就用 {@link InMemoryEmbeddingStore} 这个实现类就行了
                 */
                .filter(onlyDogs) // by specifying the static filter, we limit the search to segments only about dogs
                .build();

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(chatLanguageModel)
                .contentRetriever(contentRetriever)
                .build();

        // when
        String answer = assistant.answer("there are what animals, just tell me the name, don't tell me the details");

        System.out.println(answer);
    }


    interface PersonalizedAssistant {

        String chat(@MemoryId String userId, @UserMessage String userMessage);
    }

    @Test
    void Dynamic_Metadata_Filter_Example() {

        // 设置文本内容的元数据
        TextSegment user1Info = TextSegment.from("My favorite color is green", Metadata.metadata("userId", "1"));
        TextSegment user2Info = TextSegment.from("My favorite color is red", Metadata.metadata("userId", "2"));

        // 文本向量化存储
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        embeddingStore.add(embeddingModel.embed(user1Info).content(), user1Info);
        embeddingStore.add(embeddingModel.embed(user2Info).content(), user2Info);
        // embeddingStore contains information about both first and second user

        // 创建一个动态过滤器，只搜索元数据中的 userId 这个 key 的值为当前用户（@MemoryId 的值）的文本
        Function<Query, Filter> filterByUserId =
                (query) -> MetadataFilterBuilder.metadataKey("userId").isEqualTo(query.metadata().chatMemoryId().toString());

        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                // by specifying the dynamic filter, we limit the search to segments that belong only to the current user
                /**
                 * @see EmbeddingStoreContentRetriever.EmbeddingStoreContentRetrieverBuilder#filter(Filter) 静态过滤器的本质也是
                 * 设置 {@link EmbeddingStoreContentRetriever.EmbeddingStoreContentRetrieverBuilder#dynamicFilter}
                 *
                 * 动态过滤器的本质是一个 {@link java.util.function.Function} 函数对象
                 */
                .dynamicFilter(filterByUserId)
                .build();

        PersonalizedAssistant personalizedAssistant = AiServices.builder(PersonalizedAssistant.class)
                .chatLanguageModel(chatLanguageModel)
                .contentRetriever(contentRetriever)
                .build();

        // when
        String answer1 = personalizedAssistant.chat("1", "Which color would be best for a dress?");

        // then
        System.out.println(answer1);

        // when
        String answer2 = personalizedAssistant.chat("2", "Which color would be best for a dress?");

        // then
        System.out.println(answer2);
    }

    @Test
    void LLM_generated_Metadata_Filter_Example() {

        // 设置文本内容的元数据
        TextSegment forrestGump = TextSegment.from("Forrest Gump", Metadata.metadata("genre", "drama").put("year", 1994));
        TextSegment groundhogDay = TextSegment.from("Groundhog Day", Metadata.metadata("genre", "comedy").put("year", 1993));
        TextSegment dieHard = TextSegment.from("Die Hard", Metadata.metadata("genre", "action").put("year", 1998));

        // describe metadata keys as if they were columns in the SQL table
        /**
         * @see LanguageModelSqlFilterBuilder#format(TableDefinition) 将 TableDefinition 转换为建表语句
         * @see LanguageModelSqlFilterBuilder#DEFAULT_PROMPT_TEMPLATE
         * 通过 TableDefinition 来描述元数据的 key，这样就可以通过大模型来生成 Filter
         * LLM 生成 sql 后，解析 sql 生成对应的 Filter
         * 具体看下 {@link Filter} 的实现类，比如 {@link And}
         */
        TableDefinition tableDefinition = TableDefinition.builder()
                .name("movies")
                .addColumn("genre", "VARCHAR", "one of: [comedy, drama, action]")
                .addColumn("year", "INT")
                .build();

        LanguageModelSqlFilterBuilder sqlFilterBuilder = new LanguageModelSqlFilterBuilder(chatLanguageModel, tableDefinition);

        // 文本向量化存储
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        embeddingStore.add(embeddingModel.embed(forrestGump).content(), forrestGump);
        embeddingStore.add(embeddingModel.embed(groundhogDay).content(), groundhogDay);
        embeddingStore.add(embeddingModel.embed(dieHard).content(), dieHard);

        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                /**
                 * @see LanguageModelSqlFilterBuilder#build(Query)
                 * @see LanguageModelSqlFilterBuilder#DEFAULT_PROMPT_TEMPLATE
                 * 通过大模型来生成 Filter
                 * 当前例子的最后生成的 Filter 为：
                 * And(left=IsEqualTo(key=genre, comparisonValue=drama),
                 *         right=And(left=IsGreaterThanOrEqualTo(key=year, comparisonValue=1990), right=IsLessThanOrEqualTo(key=year, comparisonValue=1999)))
                 *
                 * 核心还是 LLM 根据 query 基于 TableDefinition 生成 sql，然后解析 sql 生成 Filter
                 */
                .dynamicFilter(query -> sqlFilterBuilder.build(query)) // LLM will generate the filter dynamically
                .build();

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(chatLanguageModel)
                .contentRetriever(contentRetriever)
                .build();

        // when
        String answer = assistant.answer("Recommend me a good drama from 90s");

        // then
        System.out.println(answer);
    }
}
