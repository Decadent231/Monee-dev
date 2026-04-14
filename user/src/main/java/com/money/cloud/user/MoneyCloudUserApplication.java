package com.money.cloud.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.money.cloud.user.mapper")
@SpringBootApplication(scanBasePackages = "com.money.cloud")
public class MoneyCloudUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoneyCloudUserApplication.class, args);
    }
}
