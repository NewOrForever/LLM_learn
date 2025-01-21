package org.example.practice;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.function.Function;

/**
 * ClassName:DateNowService
 * Package:org.example.practice
 * Description:
 *
 * @Date:2025/1/20 16:30
 * @Author:qs@1.com
 */
@Component
public class DateNowService implements Function<DateNowService.Request, String> {
    /** 测试下从 functionInputType 的 @JsonClassDescription 获取 description 描述信息 */
    @JsonClassDescription("获取当前时间")
    public record Request(String noUse) {}

    public record Response(String date) {}

    @Override
    public String apply(Request request) {
        System.out.println("执行DateNowService 工具");
        return LocalDateTime.now().toString();
    }

}
