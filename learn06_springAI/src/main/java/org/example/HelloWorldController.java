package org.example;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.model.Media;
import org.springframework.ai.openai.*;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.openai.audio.speech.SpeechPrompt;
import org.springframework.ai.openai.audio.speech.SpeechResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

/**
 * ClassName:HelloWorldController
 * Package:org.example.controller
 * Description:
 *
 * @Date:2024/11/12 9:53
 * @Author:qs@1.com
 */
@RestController
public class HelloWorldController {
    @Autowired
    private ChatClient chatClient;
    @Autowired
    private OpenAiImageModel openAiImageModel;
    @Autowired
    private OpenAiAudioTranscriptionModel openAiAudioTranscriptionModel;
    @Autowired
    private OpenAiAudioSpeechModel openAiAudioSpeechModel;
    @Autowired
    private EmbeddingModel embeddingModel;
    @Autowired
    private OpenAiChatModel openAiChatModel;

    @Value("classpath:/jfk.flac")
    private Resource audioFile;

  /*
    // 测试下原型 bean
    @Autowired
    private ChatClient chatClient1;
    @Autowired
    private AProtoType aProtoType;
    @Autowired
    private AProtoType aProtoType1;*/

    @GetMapping("/chat")
    public String completion(@RequestParam(value = "message", defaultValue = "给我讲个笑话") String message) {
        String content = chatClient.prompt().user(message).call().content();
        return content;
    }

    @GetMapping(value = "/stream/chat", produces = "text/html;charset=UTF-8")
    public Flux<String> generateStream(@RequestParam(value = "message", defaultValue = "给我讲个笑话") String message) {
        return chatClient.prompt().user(message).stream().content();
    }

    @GetMapping("/image")
    public List<String> image(@RequestParam(value = "message", required = false) String message) {
        message = StringUtils.isBlank(message) ? "A light cream colored mini golden doodle dog sitting on a hardwood floor" : message;
        ImageResponse imageResponse = openAiImageModel.call(new ImagePrompt(message,
                OpenAiImageOptions.builder()
                        .quality("hd") // 图片质量，不同的 Model Provider 对应的参数值可能不一样
                        .N(4) // 图片生成数量
                        .height(1024)
                        .width(1024).build())
        );

        System.out.println(imageResponse);
        // 如果设置了 N=1，直接获取第一个图片的 url 即可
        String firstImageUrl = imageResponse.getResult().getOutput().getUrl();
        System.out.println("第一个图片的 url：" + firstImageUrl);

        return imageResponse.getResults().stream().map(result -> result.getOutput().getUrl()).toList();
    }


    /**
     * 参考官方文档：
     * @see https://docs.spring.io/spring-ai/reference/api/audio/transcriptions.html#api/audio
     * 因为我没有 openai 的 api key，所以这里只是演示下如何调用，没法测试
     * spring ai 的 Audio Model Transcription Api 暂时支持两个 Model Provider：Azure OpenAI 和 OpenAI
     */
    @GetMapping("/audio2Text")
    public String audio2Text() {
        OpenAiAudioTranscriptionOptions options = OpenAiAudioTranscriptionOptions.builder()
                .responseFormat(OpenAiAudioApi.TranscriptResponseFormat.TEXT)
                .temperature(0f)
                .build();

        AudioTranscriptionPrompt audioTranscriptionPrompt = new AudioTranscriptionPrompt(audioFile, options);
        AudioTranscriptionResponse response = openAiAudioTranscriptionModel.call(audioTranscriptionPrompt);
        return response.getResult().getOutput();
    }

    /**
     * 参考官方文档：
     * @see https://docs.spring.io/spring-ai/reference/api/audio/speech/openai-speech.html
     * 因为我没有 openai 的 api key，所以这里只是演示下如何调用，没法测试
     * spring ai 的 Audio Model Text-to-Speech (TTS) Api 暂时支持 OpenAI
     */
    @GetMapping("/text2Audio")
    public void text2Audio() {
        // 自定义配置用来覆盖掉 yaml 中的配置，一般也不太需要用到
        OpenAiAudioSpeechOptions speechOptions = OpenAiAudioSpeechOptions.builder()
                .voice(OpenAiAudioApi.SpeechRequest.Voice.ALLOY)
                .speed(1.0f)
                .responseFormat(OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3)
                .build();

        SpeechPrompt speechPrompt = new SpeechPrompt("Today is a wonderful day to build something people love!", speechOptions);

        // byte[] call = openAiAudioSpeechModel.call("Today is a wonderful day to build something people love!");
         SpeechResponse response = openAiAudioSpeechModel.call(speechPrompt);
        // Flux<byte[]> stream = openAiAudioSpeechModel.stream("Today is a wonderful day to build something people love!");
        // Flux<SpeechResponse> responseStream = openAiAudioSpeechModel.stream(speechPrompt);
        // return responseStream;
    }

    @GetMapping("/ai/embedding")
    public float[] embed(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        /**
         * 本质还是调用 {@link EmbeddingModel#call(EmbeddingRequest)} 方法
         */
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

        UserMessage userMessage = new UserMessage("Explain what do you see on this picture?",
                new Media(MimeTypeUtils.IMAGE_PNG, imageResource));

        ChatResponse response = openAiChatModel.call(new Prompt(userMessage));
        response.getResult().getOutput().getMedia().stream().forEach(media -> {
            System.out.println(media.getMimeType());
            System.out.println(media.getData());
        });

        System.out.println(response);
        return response.getResult().getOutput().getContent();
    }

    /**
     * 多模态 - 音频
     */
    @GetMapping("/multiModal/audio")
    public String multiModalAudio() {
        ClassPathResource audioResource = new ClassPathResource("speech1.mp3");

        UserMessage userMessage = new UserMessage("What is this recording about?",
                new Media(MimeTypeUtils.parseMimeType("audio/mp3"), audioResource));

        ChatResponse response = openAiChatModel.call(new Prompt(userMessage));
        response.getResult().getOutput().getMedia().stream().forEach(media -> {
            System.out.println(media.getMimeType());
            System.out.println(media.getData());
        });

        System.out.println(response);
        return response.getResult().getOutput().getContent();
    }

    /**
     * 多模态 - 音频输出
     */
    @GetMapping("/multiModal/audioOutput")
    public void multiModalAudioOutput() {
        UserMessage userMessage = new UserMessage("Tell me joke about Spring Framework");

        ChatResponse response = openAiChatModel.call(new Prompt(List.of(userMessage),
                OpenAiChatOptions.builder()
                        .outputModalities(List.of("text", "audio"))
                        .outputAudio(new OpenAiApi.ChatCompletionRequest.AudioParameters(OpenAiApi.ChatCompletionRequest.AudioParameters.Voice.ALLOY, OpenAiApi.ChatCompletionRequest.AudioParameters.AudioResponseFormat.WAV))
                        .build()));

        String text = response.getResult().getOutput().getContent(); // audio transcript

        // byte[] waveAudio = response.getResult().getOutput().getMedia().get(0).getDataAsByteArray(); // audio data
    }


    /**
     * 测试下原型 bean
     * 原型bean 每次 getBean 都会创建一个新的实例
     */
    /*@PostConstruct
    public void init() {
        System.out.println(aProtoType);
        System.out.println(aProtoType1);
        System.out.println(chatClient);
        System.out.println(chatClient1);
    }*/

}
