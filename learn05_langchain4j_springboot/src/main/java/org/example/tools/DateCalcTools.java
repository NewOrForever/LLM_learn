package org.example.tools;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * ClassName:BookingTools
 * Package:org.example.tools
 * Description:
 *
 * @Date:2024/12/2 13:27
 * @Author:qs@1.com
 */
@Component
public class DateCalcTools {

    @Tool("计算指定天数后的具体日期，0表示今天")
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