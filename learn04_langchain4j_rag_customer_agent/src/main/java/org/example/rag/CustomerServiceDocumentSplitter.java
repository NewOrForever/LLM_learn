package org.example.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.segment.TextSegment;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName:CustomerServiceDocumentSplitter
 * Package:org.example.rag
 * Description: langchain4j 之 AI 智能客服 - 文档拆分器
 *
 * @Date:2024/11/25 11:17
 * @Author:qs@1.com
 */
public class CustomerServiceDocumentSplitter implements DocumentSplitter {

    @Override
    public List<TextSegment> split(Document document) {
        List<TextSegment> segments = new ArrayList<>();

        String[] parts = doSplit(document.text());
        for (String part : parts) {
            segments.add(TextSegment.from(part));
        }
        return segments;
    }

    private String[] doSplit(String text) {
        /**
         * 字符串根据正则表达式拆分
         *
         * 这则表达式的含义：匹配两个连续的换行符，换行符之间可以有任意数量的空白字符（包括空格、制表符等），并且两个换行符的前后也可以有任意数量的空白字符
         * 简化理解：匹配两个相邻的段落之间的空行，且允许空行前后有空格或制表符
          */
        return text.split("\\s*\\R\\s*\\R\\s*");
    }

    public static void main(String[] args) {
        String str = "Q：在线支付取消订单后钱怎么返还？\n" +
                "订单取消后，款项会在一个工作日内，直接返还到您的美团账户余额。\n" +
                "\n" +
                "Q：怎么查看退款是否成功？\n" +
                "退款会在一个工作日之内到美团账户余额，可在“账号管理——我的账号”中查看是否到账。\n" +
                "\n" +
                "Q：美团账户里的余额怎么提现？\n" +
                "余额可到美团网（meituan.com）——“我的美团→美团余额”里提取到您的银行卡或者支付宝账号，另外，余额也可直接用于支付外卖订单（限支持在线支付的商家）。";
        CustomerServiceDocumentSplitter splitter = new CustomerServiceDocumentSplitter();
        String[] parts = splitter.doSplit(str);
    }

}
