package com.staccato;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StaccatoApplication {

    public static void main(String[] args) {
        SpringApplication.run(StaccatoApplication.class, args);
    }

}
