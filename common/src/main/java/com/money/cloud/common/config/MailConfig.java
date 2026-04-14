package com.money.cloud.common.config;

import com.money.cloud.common.util.MailUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
public class MailConfig {

    @Bean
    @ConditionalOnBean(JavaMailSender.class)
    @ConditionalOnProperty(prefix = "spring.mail", name = "username")
    public MailUtil mailUtil(JavaMailSender mailSender,
                             @Value("${spring.mail.username}") String from) {
        return new MailUtil(mailSender, from);
    }
}
