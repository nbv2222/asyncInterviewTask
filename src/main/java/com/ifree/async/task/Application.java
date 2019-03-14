package com.ifree.async.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableAsync;

@Slf4j
@EnableAsync
@EntityScan(basePackages = "com.ifree.async.task")
//@EnableJpaRepositories(basePackages = "com.ifree.async.task")
@SpringBootApplication(scanBasePackages = "com.ifree.async.task")
public class Application {

    public static void main(String[] args) {
        new SpringApplication(Application.class).run(args);
    }
}
