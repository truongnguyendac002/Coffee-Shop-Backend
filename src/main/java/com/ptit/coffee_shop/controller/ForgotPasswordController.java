package com.ptit.coffee_shop.controller;

import com.ptit.coffee_shop.common.Constant;
import com.ptit.coffee_shop.common.GsonUtil;
import com.ptit.coffee_shop.config.MessageBuilder;
import com.ptit.coffee_shop.exception.CoffeeShopException;
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
    private final MessageBuilder messageBuilder;

    @PostMapping("/verifyEmail/{email}")
    public ResponseEntity<String> verifyEmail(@PathVariable String email) {
        try{
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new CoffeeShopException(Constant.FIELD_NOT_VALID, new Object[]{"Email"} ,"Email không tồn tại"));

            // Kiểm tra xem OTP có tồn tại và còn hiệu lực không
            forgotPasswordRepository.findByUser(user)
                    .filter(forgotPassword -> forgotPassword.getExpirationTime().after(Date.from(Instant.now())))
                    .ifPresent(forgotPassword -> {
//                        throw new CoffeeShopException(Constant.FIELD_NOT_VALID, new Object[]{"OTP"} ,"OTP đã được gửi tới email của bạn");
                        forgotPasswordRepository.deleteByUser(user);
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

            RespMessage respMessage = messageBuilder.buildSuccessMessage("OTP has been sent to your email");
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.OK);
        }
        catch (CoffeeShopException e) {
            RespMessage respMessage = messageBuilder.buildFailureMessage(e.getCode(), e.getObjects(), e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            RespMessage respMessage = messageBuilder.buildFailureMessage(Constant.UNDEFINED, null, e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<String> verifyOtp(@PathVariable Integer otp ,@PathVariable String email) {
        try {
            Optional<User> optionalUser = userRepository.findByEmail(email);
            if(optionalUser.isEmpty()){
                throw new CoffeeShopException(Constant.FIELD_NOT_FOUND, new Object[]{"Email"} ,"Email không tồn tại");
            }

            User user = optionalUser.get();
            ForgotPassword forgotPassword = forgotPasswordRepository.findByOtpAndUser(otp, user)
                    .orElse(null);

            if (forgotPassword == null) {
                throw new CoffeeShopException(Constant.FIELD_NOT_VALID, new Object[]{"OTP"} ,"OTP không hợp lệ");
            }

            if (forgotPassword.getExpirationTime().before(Date.from(Instant.now()))) {
                forgotPasswordRepository.deleteById(forgotPassword.getId());
                throw new CoffeeShopException(Constant.FIELD_EXPIRED, new Object[]{"OTP"} ,"OTP đã hết hạn");
            }

            RespMessage respMessage = messageBuilder.buildSuccessMessage("OTP has been verified");
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.OK);
        }
        catch (CoffeeShopException e) {
            RespMessage respMessage = messageBuilder.buildFailureMessage(e.getCode(), e.getObjects(), e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            RespMessage respMessage = messageBuilder.buildFailureMessage(Constant.UNDEFINED, null, e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


    @PostMapping("/changePassword/{email}")
    public ResponseEntity<String> changePasswordHandle(@RequestBody ChangePassword changePassword , @PathVariable String email) {
        try{
            if (!changePassword.password().equals(changePassword.repeatPassword())) {
                throw new CoffeeShopException(Constant.FIELD_NOT_VALID, new Object[]{"Repeat Password"} ,"Password không trùng khớp");
            }
            String encodedPassword = passwordEncoder.encode(changePassword.password());
            userRepository.updatePassword(email, encodedPassword);

            Optional<User> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                forgotPasswordRepository.deleteByUser(user);
            }
            RespMessage respMessage = messageBuilder.buildSuccessMessage("Password has been changed!");
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.OK);
        }
        catch (CoffeeShopException e) {
            RespMessage respMessage = messageBuilder.buildFailureMessage(e.getCode(), e.getObjects(), e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            RespMessage respMessage = messageBuilder.buildFailureMessage(Constant.UNDEFINED, null, e.getMessage());
            return new ResponseEntity<>(GsonUtil.getInstance().toJson(respMessage), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    private int generateOtpForUser() {
        return (int) (Math.random() * 9000 + 1000);
    }


}
