package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

/**
 * ClassName:App
 * Package:org.example.rag_native
 * Description:
 *
 * @Date:2025/1/26 9:40
 * @Author:qs@1.com
 */
@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    public CommandLineRunner commandRunner() {
        return args -> {
            // 在这里编写启动时需要执行的代码
            System.out.println("Application started with command-line arguments: " + Arrays.toString(args));
        };
    }

}