package org.example.rag_etl;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * ClassName:TestNativeRagController
 * Package:org.example.rag_native
 * Description:
 *
 * @Date:2025/1/26 9:38
 * @Author:qs@1.com
 */
@RestController
@RequestMapping("/document")
public class TestDocumentController {
    @Autowired
    private DocumentService documentService;

    @RequestMapping("/load")
    public List<Document> load() {
        return documentService.loadText();
    }

    @RequestMapping("/search")
    public List<Document> search(String message) {
        return documentService.search(message);
    }

    @RequestMapping("/metadataSearch")
    public List<Document> metadataSearch(String message) {
        return documentService.metadataSearch(message);
    }


    @Autowired
    private ChatClient chatClient;

    @RequestMapping("/simpleRag")
    public String simpleRag(String message) {
        // 向量检索
        List<Document> documentList = documentService.search(message);

        // 提示词模板
        PromptTemplate promptTemplate = new PromptTemplate("""
                {userMessage}
                基于以下信息回答问题：
                {documents}
                """);

        // 组装提示词
        Prompt prompt = promptTemplate.create(Map.of(
                "userMessage", message,
                "documents", documentList
        ));

        // 调用大模型
        return chatClient.prompt(prompt).call().content();
    }

}
