package com.neusoft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@MapperScan("com.neusoft.mangerservice.dao")  //@MapperScan（"maaper的路径"）
@SpringBootApplication
public class MangerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MangerServiceApplication.class, args);
    }

}
