package com.money.cloud.note;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.money.cloud.note.mapper")
@EnableScheduling
@SpringBootApplication(scanBasePackages = "com.money.cloud")
public class MoneyCloudNoteApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoneyCloudNoteApplication.class, args);
    }
}
