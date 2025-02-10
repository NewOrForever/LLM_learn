package org.example.rag_etl;

import org.springframework.ai.transformer.splitter.TextSplitter;

import java.util.List;

/**
 * ClassName:CustomTextSplitter
 * Package:org.example.rag_native
 * Description: 自定义文本切分器
 * 1. 继承 {@link TextSplitter} 抽象类
 * 2. 实现按段落切分文本的功能
 *
 * @Date:2025/1/26 10:57
 * @Author:qs@1.com
 */
public class CustomTextSplitter extends TextSplitter {
    @Override
    protected List<String> splitText(String text) {
        return List.of(split(text));
    }

    private String[] split(String text) {
        return text.split("\\s*\\R\\s*\\R\\s*");
    }

}
