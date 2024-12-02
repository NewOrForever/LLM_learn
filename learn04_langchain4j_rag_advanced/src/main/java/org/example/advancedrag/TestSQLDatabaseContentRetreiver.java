package org.example.advancedrag;

import dev.langchain4j.rag.query.transformer.ExpandingQueryTransformer;

/**
 * ClassName:TestCompressingQueryTransformer
 * Package:org.example.advancedrag
 * Description:
 *
 * @Date:2024/11/27 13:46
 * @Author:qs@1.com
 */
public class TestSQLDatabaseContentRetreiver {
    public static void main(String[] args) {
        Assistant assistant = Assistant.createSqlAssistant();
        String answer = assistant.answer("How many customers do we have?");
        System.out.println(answer);
    }

}
