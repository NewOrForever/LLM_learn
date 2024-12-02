package org.example.advancedrag;

import dev.langchain4j.rag.query.transformer.CompressingQueryTransformer;
import dev.langchain4j.rag.query.transformer.ExpandingQueryTransformer;

/**
 * ClassName:TestCompressingQueryTransformer
 * Package:org.example.advancedrag
 * Description:
 *
 * @Date:2024/11/27 13:46
 * @Author:qs@1.com
 */
public class TestExpandingQueryTransformer {
    public static void main(String[] args) {
        Assistant assistant = Assistant.create(new ExpandingQueryTransformer(Assistant.getOpenAiChatModel()));
        String answer01 = assistant.answer("告诉我一些关于诸葛亮的信息");
        System.out.println(answer01);
    }

}
