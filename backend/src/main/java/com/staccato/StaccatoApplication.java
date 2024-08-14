package com.staccato;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;

@SpringBootApplication
public class StaccatoApplication {

    public static void main(String[] args) {
        SpringApplication.run(StaccatoApplication.class, args);
    }

}
