package com.money.cloud.common.util;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class MailUtil {

    private final JavaMailSender mailSender;
    private final String from;

    public MailUtil(JavaMailSender mailSender, String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    public void sendTextMail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
