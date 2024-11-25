package com.ptit.coffee_shop.service;

import com.ptit.coffee_shop.utils.MailBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendSimpleMail(MailBody mailbody) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(mailbody.to());
        message.setFrom("tamnp312@gmail.com");
        message.setSubject(mailbody.subject());
        message.setText(mailbody.body());

        mailSender.send(message);

    }
}
