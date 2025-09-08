package com.junkard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan; 

@SpringBootApplication
@ComponentScan(basePackages = "com.junkard")
public class JunkardApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(JunkardApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(JunkardApplication.class)
                        .initializers(new ProfileInitializer());
    }
}