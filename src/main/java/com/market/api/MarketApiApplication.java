package com.market.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableWebMvc
@SpringBootApplication
public class MarketApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketApiApplication.class, args);
    }

}
