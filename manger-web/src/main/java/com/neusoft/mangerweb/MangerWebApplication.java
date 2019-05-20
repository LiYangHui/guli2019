package com.neusoft.mangerweb;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})//阻止spring boot自动注入dataSource bean
public class MangerWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(MangerWebApplication.class, args);
    }

}
