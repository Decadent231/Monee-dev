package com.money.cloud.monee;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.money.cloud.monee.mapper")
@SpringBootApplication(scanBasePackages = "com.money.cloud")
public class MoneyCloudMoneeApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoneyCloudMoneeApplication.class, args);
    }
}
