package org.example;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.model.Media;
import org.springframework.ai.openai.*;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.OpenAiImageApi;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionRequest.AudioParameters;

/**
 * ClassName:ZhiPuAIController
 * Package:org.example
 * Description: 看智谱的 API 文档，应该是支持 OpenAI 规范的 <br>
 * 所以这里直接使用 OpenAI 的相关类来调用智谱的 API，只不过需要改下 baseUrl 和 uriPath <br>
 * 我这里因为不想用 spring-ai-zhipuai-spring-boot-starter，也不想在 application.yml 中配置使用自动装配的 Bean，所以都是手动创建 OpenAI 相关的类 <br>
 * 一方面验证自己对于源码的理解，另一方面从更底层的角度来使用 OpenAI 的 API <br>
 * 另：测试时尽量使用免费的模型 -> https://bigmodel.cn/dev/activities/free/glm-4-flash <br>
 * 有些接口还是有些区别的，具体可以看下智谱的 API 文档 <br>
 * 还得说一点：真正要使用智谱AI 的话还是使用对应的智谱AI 的 springboot 依赖 spring-ai-zhipuai-spring-boot-starter 更方便
 *
 * @Date:2025/1/16 13:42
 * @Author:qs@1.com
 */
@RestController
@RequestMapping("/zhipu")
public class ZhiPuAIController {
    @Value("classpath:/jfk.flac")
    private Resource audioFile;
    private static final String zhiPuApiKey;

    static {
        zhiPuApiKey = System.getenv("zhipu_api_key");
    }

    @GetMapping("/chat")
    public String completion(@RequestParam(value = "message", defaultValue = "给我讲个笑话") String message) {
        OpenAiApi openAiApi = new OpenAiApi("https://open.bigmodel.cn", zhiPuApiKey,
                "/api/paas/v4/chat/completions", "/api/paas/v4/embeddings",
                RestClient.builder(), WebClient.builder(), RetryUtils.DEFAULT_RESPONSE_ERROR_HANDLER);

        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model("GLM-4-Flash")
                .build();
        OpenAiChatModel chatModel = new OpenAiChatModel(openAiApi, options);

        Prompt prompt = new Prompt(new UserMessage(message));
        ChatResponse response = chatModel.call(prompt);
        return response.getResult().getOutput().getContent();
    }

    @GetMapping(value = "/stream/chat", produces = "text/html;charset=UTF-8")
    public Flux<String> generateStream(@RequestParam(value = "message", defaultValue = "给我讲个笑话") String message) {
        OpenAiApi openAiApi = new OpenAiApi("https://open.bigmodel.cn", zhiPuApiKey,
                "/api/paas/v4/chat/completions", "/api/paas/v4/embeddings",
                RestClient.builder(), WebClient.builder(), RetryUtils.DEFAULT_RESPONSE_ERROR_HANDLER);

        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model("GLM-4-Flash")
                .build();
        OpenAiChatModel chatModel = new OpenAiChatModel(openAiApi, options);

        Prompt prompt = new Prompt(new UserMessage(message));
        Flux<ChatResponse> responseFlux = chatModel.stream(prompt);
        return responseFlux.map(response -> (response.getResult() == null || response.getResult().getOutput() == null
                || response.getResult().getOutput().getText() == null) ? ""
                : response.getResult().getOutput().getText());
    }

    @GetMapping("/image")
    public List<String> image(@RequestParam(value = "message", required = false) String message) {
        message = StringUtils.isBlank(message) ? "A light cream colored mini golden doodle dog sitting on a hardwood floor" : message;

        OpenAiImageApiForZhiPu openAiImageApi = new OpenAiImageApiForZhiPu("https://open.bigmodel.cn", zhiPuApiKey,
                CollectionUtils.toMultiValueMap(Map.of()),
                RestClient.builder(), RetryUtils.DEFAULT_RESPONSE_ERROR_HANDLER);
        OpenAiImageModel openAiImageModel = new OpenAiImageModel(openAiImageApi);


        ImageResponse imageResponse = openAiImageModel.call(new ImagePrompt(message,
                OpenAiImageOptions.builder()
                        .model("Cogview-3-Flash")
                        .quality("hd") // 图片质量，不同的 Model Provider 对应的参数值可能不一样
                        .N(4) // 图片生成数量，智谱只生成一张图片
                        .height(1024)
                        .width(1024).build())
        );

        System.out.println(imageResponse);
        // 如果设置了 N=1，直接获取第一个图片的 url 即可
        String firstImageUrl = imageResponse.getResult().getOutput().getUrl();
        System.out.println("第一个图片的 url：" + firstImageUrl);

        return imageResponse.getResults().stream().map(result -> result.getOutput().getUrl()).toList();
    }

    static class OpenAiImageApiForZhiPu extends OpenAiImageApi {
        private final RestClient restClient;

        public OpenAiImageApiForZhiPu(String baseUrl, String apiKey, MultiValueMap<String, String> headers, RestClient.Builder restClientBuilder, ResponseErrorHandler responseErrorHandler) {
            super(baseUrl, apiKey, headers, restClientBuilder, responseErrorHandler);
            // @formatter:off
            this.restClient = restClientBuilder.baseUrl(baseUrl)
                    .defaultHeaders(h -> {
                        h.setBearerAuth(apiKey);
                        h.setContentType(MediaType.APPLICATION_JSON);
                        h.addAll(headers);
                    })
                    .defaultStatusHandler(responseErrorHandler)
                    .build();
            // @formatter:on
        }

        @Override
        public ResponseEntity<OpenAiImageResponse> createImage(OpenAiImageRequest openAiImageRequest) {
            Assert.notNull(openAiImageRequest, "Image request cannot be null.");
            Assert.hasLength(openAiImageRequest.prompt(), "Prompt cannot be empty.");

            return restClient.post()
                    .uri("/api/paas/v4/images/generations")
                    .body(openAiImageRequest)
                    .retrieve()
                    .toEntity(OpenAiImageResponse.class);
        }
    }

    @GetMapping("/embedding")
    public float[] embed(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        OpenAiApi openAiApi = new OpenAiApi("https://open.bigmodel.cn", zhiPuApiKey,
                "/api/paas/v4/chat/completions", "/api/paas/v4/embeddings",
                RestClient.builder(), WebClient.builder(), RetryUtils.DEFAULT_RESPONSE_ERROR_HANDLER);


        OpenAiEmbeddingOptions options = OpenAiEmbeddingOptions.builder()
                .model("Embedding-3")
                .build();
        OpenAiEmbeddingModel embeddingModel = new OpenAiEmbeddingModel(openAiApi, MetadataMode.EMBED, options);

        EmbeddingResponse embeddingResponse = embeddingModel.embedForResponse(List.of(message));
        Map<String, EmbeddingResponse> embedding = Map.of("embedding", embeddingResponse);
        return embeddingResponse.getResult().getOutput();
    }

    /**
     * 多模态 - 视觉
     */
    @GetMapping("/multiModal/vision")
    public String multiModalVision() {
        ClassPathResource imageResource = new ClassPathResource("/multimodal.test.png");

        OpenAiApi openAiApi = new OpenAiApi("https://open.bigmodel.cn", zhiPuApiKey,
                "/api/paas/v4/chat/completions", "/api/paas/v4/embeddings",
                RestClient.builder(), WebClient.builder(), RetryUtils.DEFAULT_RESPONSE_ERROR_HANDLER);

        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model("GLM-4V-Flash")
                .build();
        OpenAiChatModel chatModel = new OpenAiChatModel(openAiApi, options);

        UserMessage userMessage = new UserMessage("Explain what do you see on this picture?",
                new Media(MimeTypeUtils.IMAGE_PNG, imageResource));
        Prompt prompt = new Prompt(userMessage);
        ChatResponse response = chatModel.call(prompt);
        response.getResult().getOutput().getMedia().stream().forEach(media -> {
            System.out.println(media.getMimeType());
            System.out.println(media.getData());
        });
        System.out.println(response);
        return response.getResult().getOutput().getContent();

    }

    /**
     * 多模态 - 音频转文本
     */
    @GetMapping("/multiModal/audio")
    public String multiModalAudio() throws IOException {
        ClassPathResource audioResource = new ClassPathResource("speech1.mp3");

        UserMessage userMessage = new UserMessage("语音转文本",
                new Media(MimeTypeUtils.parseMimeType("audio/mp3"), audioResource));

        OpenAiApi openAiApi = new OpenAiApi("https://open.bigmodel.cn", zhiPuApiKey,
                "/api/paas/v4/chat/completions", "/api/paas/v4/embeddings",
                RestClient.builder(), WebClient.builder(), RetryUtils.DEFAULT_RESPONSE_ERROR_HANDLER);

        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model("GLM-4-Voice")
                .outputAudio(new AudioParameters(AudioParameters.Voice.ALLOY, AudioParameters.AudioResponseFormat.WAV))
                .build();
        OpenAiChatModel chatModel = new OpenAiChatModel(openAiApi, options);

        ChatResponse response = chatModel.call(new Prompt(userMessage));

        response.getResult().getOutput().getMedia().stream().forEach(media -> {
            System.out.println(media.getMimeType());
            System.out.println(media.getData());
        });

        // 语音内容写到文件
        byte[] audioData = response.getResult().getOutput().getMedia().get(0).getDataAsByteArray();
        Files.write(Paths.get("C:\\Users\\Admin\\Desktop\\output.wav"), audioData);

        return response.getResult().getOutput().getContent();
    }

    /**
     * 多模态 - 音频输出 TTS
     * 智谱这个模型好像不支持 TTS，下面是会报错的
     * 智谱的音频模型调用必须要传入音频文件，该模型应该是主要用来语音对话的，不支持 TTS
     */
    @GetMapping(value = "/multiModal/audioOutput", produces = "audio/wav")
    public ResponseEntity<byte[]> multiModalAudioOutput() {
        ClassPathResource audioResource = new ClassPathResource("speech1.mp3");
        UserMessage userMessage = new UserMessage("讲个笑话，字数不要超过100");
        OpenAiApi openAiApi = new OpenAiApi("https://open.bigmodel.cn", zhiPuApiKey,
                "/api/paas/v4/chat/completions", "/api/paas/v4/embeddings",
                RestClient.builder(), WebClient.builder(), RetryUtils.DEFAULT_RESPONSE_ERROR_HANDLER);

        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model("GLM-4-Voice")
                .build();
        OpenAiChatModel chatModel = new OpenAiChatModel(openAiApi, options);

        ChatResponse response = chatModel.call(new Prompt(List.of(userMessage),
                OpenAiChatOptions.builder()
                        .outputModalities(List.of("text", "audio"))
                        .outputAudio(new AudioParameters(AudioParameters.Voice.ALLOY, AudioParameters.AudioResponseFormat.WAV))
                        .build()));

        String text = response.getResult().getOutput().getContent(); // audio transcript
        System.out.println(text);

        byte[] waveAudio = response.getResult().getOutput().getMedia().get(0).getDataAsByteArray(); // audio data

        // 设置响应头，告诉浏览器这是一个音频文件
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=output.wav");
        headers.setContentType(MediaType.valueOf("audio/wav"));

        // 返回音频文件
        return ResponseEntity.ok()
                .headers(headers)
                .body(waveAudio);
    }

    /**
     * 测试浏览器播放音频
     */
    @GetMapping("/playAudio")
    public ResponseEntity<Resource> testChromePlayAudio() {
        // 加载音频文件
        Resource audioResource = new ClassPathResource("m_set_104.wav");

        // 检查文件是否存在
        if (!audioResource.exists()) {
            return ResponseEntity.notFound().build();
        }

        // 设置响应头，告诉浏览器这是一个音频文件
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=output.wav");
        headers.setContentType(MediaType.valueOf("audio/wav"));

        // 返回音频文件
        return ResponseEntity.ok()
                .headers(headers)
                .body(audioResource);
    }

    /**
     * 测试浏览器播放音频 - 边播边下载
     * 可以结合 playAudio.html 页面测试，直接调接口浏览器也行
     */
    @GetMapping("/stream/playAudio")
    public ResponseEntity<ResourceRegion> streamPlayAudio(@RequestHeader HttpHeaders headers) throws IOException {
        // 加载音频文件
        Resource audioResource = new ClassPathResource("m_set_104.wav");

        // 检查文件是否存在
        if (!audioResource.exists()) {
            return ResponseEntity.notFound().build();
        }

        // 获取文件长度
        long contentLength = audioResource.contentLength();

        // 解析 Range 请求头
        ResourceRegion region = resourceRegion(audioResource, headers);

        // 设置响应头
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.valueOf("audio/wav"));
        responseHeaders.setContentLength(contentLength);
        responseHeaders.set("Accept-Ranges", "bytes");

        // 返回部分内容（206状态码）
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .headers(responseHeaders)
                .body(region);
    }

    private ResourceRegion resourceRegion(Resource resource, HttpHeaders headers) throws IOException {
        // 默认返回整个文件
        long contentLength = resource.contentLength();
        long start = 0;
        long end = contentLength - 1;

        // 解析 Range 请求头
        String rangeHeader = headers.getFirst(HttpHeaders.RANGE);
        if (rangeHeader != null) {
            String[] ranges = rangeHeader.substring("bytes=".length()).split("-");
            start = Long.parseLong(ranges[0]);
            if (ranges.length > 1) {
                end = Long.parseLong(ranges[1]);
            } else {
                end = contentLength - 1;
            }
        }

        // 计算返回的区域长度
        long rangeLength = Math.min(1024 * 1024, end - start + 1); // 每次返回 1MB 数据
        return new ResourceRegion(resource, start, rangeLength);
    }


}
