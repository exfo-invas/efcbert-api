package com.invas.enhanced.fc.bert;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EnhancedFcBertApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnhancedFcBertApplication.class, args);
    }

}
