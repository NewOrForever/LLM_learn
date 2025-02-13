package org.example.advanced_rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.DefaultChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.AdvisedRequest;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.evaluation.RelevancyEvaluator;
import org.springframework.ai.model.Content;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.retrieval.join.ConcatenationDocumentJoiner;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * ClassName:QuestionAnswerAdvisorTestController
 * Package:org.example.advanced_rag
 * Description: 测试 Spring AI 通用 RAG  flow 模块 - {@link org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor}
 *
 * @Date:2025/2/8 15:44
 * @Author:qs@1.com
 */
@RestController
@RequestMapping("/rag")
public class GeneralRagTestController {
    @Autowired
    private ChatClient chatClient;
    @Autowired
    @Qualifier("redis")
    private VectorStore vectorStore;
    @Autowired
    private ChatClient.Builder chatClientBuilder;

    @RequestMapping(value = "/search", produces = "text/html;charset=UTF-8")
    public Flux<String> search(String message) {

        RetrievalAugmentationAdvisor ragAdvisor = RetrievalAugmentationAdvisor.builder()
                /**
                 * QueryTransformer 组件使用 RewriteQueryTransformer，重写优化 Query
                 * 可以设置多个 QueryTransformer
                 * @see RetrievalAugmentationAdvisor#before(AdvisedRequest)
                 */
                .queryTransformers(RewriteQueryTransformer.builder().chatClientBuilder(chatClientBuilder).build())
                // QueryExpander 组件使用 MultiQueryExpander，对 Query 扩展出 多个 Query
                .queryExpander(MultiQueryExpander.builder().chatClientBuilder(chatClientBuilder).numberOfQueries(3).build())
                // DocumentRetriever 组件使用 VectorStoreDocumentRetriever，使用 VectorStore 进行检索
                .documentRetriever(VectorStoreDocumentRetriever.builder().vectorStore(vectorStore).similarityThreshold(0.7).build())
                // DocumentJoiner 组件使用 ConcatenationDocumentJoiner，将检索到的多个 Document 进行拼接
                .documentJoiner(new ConcatenationDocumentJoiner())
                /**
                 * @see ContextualQueryAugmenter#augmentQueryWhenEmptyContext(Query)
                 * @see ContextualQueryAugmenter#allowEmptyContext 默认为 false
                 * allowEmptyContext 为 false，则当检索结果为空时，Argumented Query 用的是
                 * {@link ContextualQueryAugmenter#DEFAULT_EMPTY_CONTEXT_PROMPT_TEMPLATE}，让大模型礼貌的回答不知道
                 * allowEmptyContext 为 true，则当检索结果为空时，Argumented Query 就是原始的 Query，让大模型自行思考回答
                 */
                .queryAugmenter(ContextualQueryAugmenter.builder().allowEmptyContext(true).build())
                .build();

        return chatClient.prompt()
                .system("用中文回答")
                .user(message)
                .advisors(ragAdvisor)
                .stream().content();
    }

}
