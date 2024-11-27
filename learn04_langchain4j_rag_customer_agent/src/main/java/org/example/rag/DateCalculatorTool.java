package org.example.rag;

import dev.langchain4j.agent.tool.Tool;

import java.time.LocalDate;

/**
 * ClassName:DateCalculatorTool
 * Package:org.example.rag
 * Description:
 *
 * @Date:2024/11/26 14:43
 * @Author:qs@1.com
 */
public class DateCalculatorTool {
    @Tool("计算指定天数后的具体日期")
    public String date(int days) {
        return LocalDate.now().plusDays(days).toString();
    }

    @Tool("计算指定天数后的具体日期，排除周末")
    public String dateWithoutWeekDay(int days) {
        LocalDate date = LocalDate.now();
        int addedDays = 0;
        while (addedDays < days) {
            date = date.plusDays(1);
            if (date.getDayOfWeek().getValue() < 6) { // 1 = Monday, ..., 5 = Friday
                addedDays++;
            }
        }
        return date.toString();
    }

}
