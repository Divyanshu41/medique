package com.example.medique;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.example.medique.config.ApplicationProperties;

@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableCaching
@EnableTransactionManagement
@EnableConfigurationProperties(ApplicationProperties.class)
public class Medique {

    public static void main(String[] args) {
        SpringApplication.run(Medique.class, args);
    }

}
