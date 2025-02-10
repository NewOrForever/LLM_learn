package org.example.contribute_for_springai;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName:TestEmbeddingDimensionsController
 * Package:org.example.contribute_for_springai
 * Description:
 *
 * @Date:2025/1/26 15:30
 * @Author:qs@1.com
 */
@RestController
@RequestMapping("/test")
public class TestEmbeddingDimensionsController {
    @Autowired
    private MyOpenAiEmbeddingModel myOpenAiEmbeddingModel;

    @Autowired
    private MyZhipuAiEmbeddingModel myZhipuAiEmbeddingModel;

    @GetMapping("/index")
    public String testDimensions() {
        /**
         * 仅仅只是为了验证 embeddingDimensions 是否生效
         * 以及不同的 EmbeddingModel 的 embeddingDimensions 是否是不同的对象，有各自不同的值
         */
        return "test Dimensions";
    }

}
