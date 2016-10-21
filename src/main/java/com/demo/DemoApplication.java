package com.demo;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.freemarker.SpringTemplateLoader;

import java.io.InputStream;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = {
        "com.demo.service",
        "com.demo.service.impl",
        "com.demo.controller",
        "com.demo.configuration",
        "com.demo.filter",
        "com.demo.pojo"})
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
