package org.example;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.function.Consumer;

/**
 * ClassName:DateServices
 * Package:org.example
 * Description: 对于实现 Consumer 接口的工具，主要是用来消费用户的输入
 * 所以该工具不应该处理一些需要返回消息给用户的场景
 * 用来消费AI Message，比如保存到数据库
 *
 * @Date:2025/1/17 16:31
 * @Author:qs@1.com
 */
//@Component
//@Description("持久化文章内容")
//public class WriterServiceForConsumerTest implements Consumer<WriterServiceForConsumerTest.Request> {
//    public record Request(@Schema(description = "文章内容") String content) {}
//
//    @Override
//    public void accept(Request request) {
//        System.out.println("保存文章内容：" + request.content);
//    }
//
//}
