package org.example.advancedrag;

import dev.langchain4j.rag.query.transformer.CompressingQueryTransformer;

/**
 * ClassName:TestCompressingQueryTransformer
 * Package:org.example.advancedrag
 * Description:
 *
 * @Date:2024/11/27 13:46
 * @Author:qs@1.com
 */
public class TestCompressingQueryTransformer {
    public static void main(String[] args) {
        Assistant assistant = Assistant.create(new CompressingQueryTransformer(Assistant.getOpenAiChatModel()));
        String answer01 = assistant.answer("告诉我一些关于诸葛亮的信息");
        System.out.println(answer01);
        System.out.println("------");
        String answer02 = assistant.answer("他的家乡在哪里");
        System.out.println(answer02);
    }

}
