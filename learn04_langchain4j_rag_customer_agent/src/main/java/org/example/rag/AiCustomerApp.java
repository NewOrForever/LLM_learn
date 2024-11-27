package org.example.rag;

import java.nio.file.Paths;

/**
 * ClassName:AiCustomerApp
 * Package:org.example.rag
 * Description: langchain4j 之 AI 智能客服应用
 *
 * @Date:2024/11/25 10:57
 * @Author:qs@1.com
 */
public class AiCustomerApp {
    public static void main(String[] args) {
        /**
         * 先执行下 {@link CustomerKnowledgeBaseBuilder} 构建知识库
         * 然后再去创建智能客服代理对象
         */
        CustomerServiceAgent customerServiceAgent = CustomerServiceAgent.create();
        // String answer = customerServiceAgent.answer("今天的余额提现，最晚什么时候能到账？");
        // String answer = customerServiceAgent.answer("今天的余额提现，最晚哪天能到账？给我具体的日期，不用排除周末");
        String answer = customerServiceAgent.answer("今天的余额提现，最晚哪天能到账？给我具体的日期，排除周末");
        System.out.println(answer);
    }

}
