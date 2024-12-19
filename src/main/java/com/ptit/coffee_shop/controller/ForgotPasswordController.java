package com.ptit.coffee_shop.controller;

import com.ptit.coffee_shop.model.ForgotPassword;
import com.ptit.coffee_shop.model.User;
import com.ptit.coffee_shop.payload.response.RespMessage;
import com.ptit.coffee_shop.repository.ForgotPasswordRepository;
import com.ptit.coffee_shop.repository.UserRepository;
import com.ptit.coffee_shop.service.EmailService;
import com.ptit.coffee_shop.utils.ChangePassword;
import com.ptit.coffee_shop.utils.MailBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api/forgotPassword")
@RequiredArgsConstructor
public class ForgotPasswordController {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ForgotPasswordRepository forgotPasswordRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/verifyEmail/{email}")
    public ResponseEntity<RespMessage> verifyEmail(@PathVariable String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Email Invalid"));

        // Kiểm tra xem OTP có tồn tại và còn hiệu lực không
        forgotPasswordRepository.findByUser(user)
                .filter(forgotPassword -> forgotPassword.getExpirationTime().after(Date.from(Instant.now())))
                .ifPresent(forgotPassword -> {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP đã được gửi và còn hiệu lực.");
                });

        int otp = generateOtpForUser();
        ForgotPassword forgotPassword = ForgotPassword.builder()
                .otp(otp)
                .expirationTime(new Date(System.currentTimeMillis() +600 *1000))
                .user(user)
                .build();

        MailBody mailBody =MailBody.builder()
                .to(email)
                .subject("OTP for Forgot Password request")
                .body("This is the OTP for your Forgot Password request: " + otp)
                .build();

        emailService.sendSimpleMail(mailBody);
        forgotPasswordRepository.save(forgotPassword);

        return ResponseEntity.ok(RespMessage.builder()
                .respCode("000")
                .respDesc("OTP for Forgot Password verified!")
                .build());
    }


    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<RespMessage> verifyOtp(@PathVariable Integer otp ,@PathVariable String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(RespMessage.builder()
                    .respCode("103")
                    .respDesc("email Invalid")
                    .build() );
        }

        User user = optionalUser.get();
        ForgotPassword forgotPassword = forgotPasswordRepository.findByOtpAndUser(otp, user)
                .orElse(null);

        if (forgotPassword == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(RespMessage.builder()
                    .respCode("104")
                    .respDesc("OTP không hợp lệ")
                    .build() );
        }

        if (forgotPassword.getExpirationTime().before(Date.from(Instant.now()))) {
            forgotPasswordRepository.deleteById(forgotPassword.getId());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body(RespMessage.builder()
                            .respCode("404")
                            .respDesc("Otp has expired")
                            .build());
        }

//        return ResponseEntity.ok().body("OTP has been verified!");
        return ResponseEntity.ok(RespMessage.builder()
                .respCode("000")
                .respDesc("OTP has been verified!")
                .build());
    }


    @PostMapping("/changePassword/{email}")

    public ResponseEntity<RespMessage> changePasswordHandle(@RequestBody ChangePassword changePassword , @PathVariable String email) {
        if (!changePassword.password().equals(changePassword.repeatPassword())) {
            return new ResponseEntity<>( HttpStatus.EXPECTATION_FAILED );
        }
        String encodedPassword = passwordEncoder.encode(changePassword.password());
        userRepository.updatePassword(email, encodedPassword);

//        return ResponseEntity.ok("Password has been changed!");
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            forgotPasswordRepository.deleteByUser(user);
        }
        return ResponseEntity.ok(RespMessage.builder()
                .respCode("000")
                .respDesc("Password has been changed!")
                .build());
    }

    private int generateOtpForUser() {
        return (int) (Math.random() * 9000 + 1000);
    }


}
