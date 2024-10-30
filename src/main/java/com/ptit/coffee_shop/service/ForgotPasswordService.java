package com.ptit.coffee_shop.service;

import com.ptit.coffee_shop.repository.ForgotPasswordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ForgotPasswordService {

    @Autowired
    private ForgotPasswordRepository forgotPasswordRepository;

    // Tác vụ sẽ chạy mỗi 10 phút (600000 ms)
    @Scheduled(fixedRate = 600000)
    public void deleteExpiredForgotPasswords() {
        Date now = new Date();
        forgotPasswordRepository.deleteByExpirationTimeBefore(now);
    }
}
