package org.example;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.function.Function;

/**
 * ClassName:DateServices
 * Package:org.example
 * Description:
 *
 * @Date:2025/1/17 16:31
 * @Author:qs@1.com
 */
@Description("获取指定地点的当前时间")
public class DateServiceForFunctionCallBackTest implements Function<DateServiceForFunctionCallBackTest.Request, DateServiceForFunctionCallBackTest.Response> {
    public record Request(@Schema(description = "地点") String address) {}

    public record Response(String date) {}

    @Override
    public Response apply(Request request) {
        System.out.println("funcion callback test 地点：" + request.address);
        return new Response(String.format("%s的当前时间是%s，测试 callback 用", request.address, LocalDateTime.now()));
    }

}
