package org.example;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName:TestWorkFlowController
 * Package:org.example
 * Description: 构建有效 Agent - workflow
 * 受限于本地部署的性能问题，可能无法输出正确的结果
 *
 * @Date:2025/2/18 14:47
 * @Author:qs@1.com
 */
@RestController
@RequestMapping("/workflow")
public class TestWorkFlowController {
    @Autowired
    private ChatClient chatClient;

    // Sample business report with various metrics in natural language
    String report = """
            Q3 Performance Summary:
            Our customer satisfaction score rose to 92 points this quarter.
            Revenue grew by 45% compared to last year.
            Market share is now at 23% in our primary market.
            Customer churn decreased to 5% from 8%.
            New user acquisition cost is $43 per user.
            Product adoption rate increased to 78%.
            Employee satisfaction is at 87 points.
            Operating margin improved to 34%.
            """;

    @GetMapping("/chain")
    public String chain() {
        ChainWorkflow chainWorkflow = new ChainWorkflow(chatClient);
        return chainWorkflow.chain(report);
    }

    /**
     * 实现一个prompt chain workflow，将复杂任务分解为一系列更简单的 LLM 调用，其中每个步骤的输出都提供给下一步
     */
    public class ChainWorkflow {
        /**
         * System prompts that define each transformation step in the chain.
         * Each prompt acts as a gate that validates and transforms the output
         * before proceeding to the next step.
         */
        private static final String[] CHAIN_PROMPTS = {
                // Step 1: Extract numerical values
                """
                Extract only the numerical values and their associated metrics from the text.
                Format each as 'value: metric' on a new line.
                Example format:
                92: customer satisfaction
                45%: revenue growth""",

                // Step 2: Standardize to percentages
                """
                Convert all numerical values to percentages where possible.
                If not a percentage or points, convert to decimal (e.g., 92 points -> 92%).
                Keep one number per line.
                Example format:
                92%: customer satisfaction
                45%: revenue growth""",

                // Step 3: Sort in descending order
                """
                Sort all lines in descending order by numerical value.
                Keep the format 'value: metric' on each line.
                Example:
                92%: customer satisfaction
                87%: employee satisfaction""",

                // Step 4: Format as markdown
                """
                Format the sorted data as a markdown table with columns:
                | Metric | Value |
                |:--|--:|
                | Customer Satisfaction | 92% |"""
        };

        private final ChatClient chatClient;

        public ChainWorkflow(ChatClient chatClient) {
            this.chatClient = chatClient;
        }

        public String chain(String userInput) {
            String response = userInput;

            for (String prompt : CHAIN_PROMPTS) {
                response = chatClient.prompt(
                        String.format("{%s}\n{%s}", prompt, response)
                ).call().content();
            }

            return response;
        }
    }

}
