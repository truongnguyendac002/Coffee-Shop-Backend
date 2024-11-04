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
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if(optionalUser.isEmpty()){
//            return  ResponseEntity.badRequest().body("Please provide a valid email!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(RespMessage.builder()
                    .respCode("103")
                    .respDesc("email Invalid")
                    .build() );
        }

        User user = optionalUser.get();

        Optional<ForgotPassword> existingOtpRecord =forgotPasswordRepository.findByUser(user);
        if (existingOtpRecord.isPresent()) {
            ForgotPassword forgotPassword = existingOtpRecord.get();
            Date currentDate = new Date();

            // Kiểm tra thời hạn của OTP
            if (forgotPassword.getExpirationTime().after(currentDate)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(RespMessage.builder()
                        .respCode("104")
                        .respDesc("OTP has already been sent and is still valid.")
                        .build());
            } else {
                // Xóa OTP đã hết hạn
                forgotPasswordRepository.deleteById(forgotPassword.getId());
            }
        }

        int otp = generateOtpForUser();

        MailBody mailBody =MailBody.builder()
                .to(email)
                .subject("OTP for Forgot Password request")
                .body("This is the OTP for your Forgot Password request: " + otp)
                .build();


        ForgotPassword forgotPassword = ForgotPassword.builder()
                .otp(otp)
                .expirationTime(new Date(System.currentTimeMillis() +600 *1000))
                .user(user)
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
        ForgotPassword forgotPassword = forgotPasswordRepository.findByOtpAndUser(otp,user)
                .orElseThrow(() -> new RuntimeException("Invalid OTP"));

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
        return ResponseEntity.ok(RespMessage.builder()
                .respCode("000")
                .respDesc("Password has been changed!")
                .build());
    }

    private int generateOtpForUser() {
        return (int) (Math.random() * 9000 + 1000);
    }


}
