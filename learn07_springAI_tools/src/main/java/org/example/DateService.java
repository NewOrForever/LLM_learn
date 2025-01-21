package org.example;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.function.Function;

/**
 * ClassName:DateServices
 * Package:org.example
 * Description:
 *
 * @Date:2025/1/17 16:31
 * @Author:qs@1.com
 */
@Component
@Description("获取指定地点的当前时间")
public class DateService implements Function<DateService.Request, DateService.Response> {

    public record Request(@Schema(description = "地点") String address) {}

    public record Response(String date) {}

    @Override
    public Response apply(Request request) {
        System.out.println("地点：" + request.address);
        //  这个地方可以调用第三方API获取指定地点的时间
        //  这里直接为了演示直接返回当前时间
        return new Response(String.format("%s的当前时间是%s", request.address, LocalDateTime.now()));
    }

}
