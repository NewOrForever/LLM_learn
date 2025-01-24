package org.example;

import net.coobird.thumbnailator.Thumbnails;

import java.io.File;
import java.io.IOException;

/**
 * ClassName:Main
 * Package:org.example
 * Description: 图片压缩工具测试
 *
 * @Date:2025/1/24 10:59
 * @Author:qs@1.com
 */
public class PictureCompressTest {
        public static void main(String[] args) {
            try {
                // 输入图片路径
                String inputImagePath = "C:\\Users\\Admin\\Desktop\\p.png";
                // 输出图片路径
                String outputImagePath = "C:\\Users\\Admin\\Desktop\\output_compressed.jpg";

                // 压缩图片并保存
                Thumbnails.of(new File(inputImagePath))
                        .scale(0.1) // 保持原始尺寸
                        // .outputQuality(0.5) // 设置压缩质量，0.0 到 1.0 之间，1.0 为最高质量
                        .toFile(new File(outputImagePath));

                System.out.println("图片压缩完成，输出路径: " + outputImagePath);

            } catch (IOException e) {
                e.printStackTrace();
            }
    }

}
