package org.example;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDateTime;

/**
 * ClassName:DateTimeTools
 * Package:org.example
 * Description:
 *
 * @Date:2025/2/18 11:30
 * @Author:qs@1.com
 */
public class DateTimeTools {
    @Tool(description = "Get the current date and time in the user's timezone")
    public String getCurrentDateTime() {
        System.out.println("method called：getCurrentDateTime");
        return LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString();
    }

}
