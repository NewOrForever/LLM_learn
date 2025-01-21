package org.example;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * ClassName:DateServices
 * Package:org.example
 * Description:
 *
 * @Date:2025/1/17 16:31
 * @Author:qs@1.com
 */
@Component
@Description("获取当前时间")
public class DateServiceForSupplier implements Supplier<DateServiceForSupplier.Response> {

    public record Response(String date) {}

    @Override
    public Response get() {
        System.out.println("当前时间：" + LocalDateTime.now());
        return new Response(String.format("当前时间是%s", LocalDateTime.now()));
    }

}
