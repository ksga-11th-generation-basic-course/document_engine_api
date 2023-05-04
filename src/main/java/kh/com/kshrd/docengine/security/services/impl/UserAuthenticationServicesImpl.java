package kh.com.kshrd.docengine.security.services.impl;

import jakarta.mail.internet.MimeMessage;
import kh.com.kshrd.docengine.configuration.Encoder;
import kh.com.kshrd.docengine.exceptions.BadRequestException;
import kh.com.kshrd.docengine.exceptions.NotFoundException;
import kh.com.kshrd.docengine.security.model.entity.OptCode;
import kh.com.kshrd.docengine.security.model.entity.UserAuthentication;
import kh.com.kshrd.docengine.security.model.request.UserAuthenticationForgotRequest;
import kh.com.kshrd.docengine.security.model.request.UserAuthenticationRegisterRequest;
import kh.com.kshrd.docengine.security.repository.UserAuthenticationRepository;
import kh.com.kshrd.docengine.security.services.EmailServices;
import kh.com.kshrd.docengine.security.services.UserAuthenticationServices;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserAuthenticationServicesImpl implements UserAuthenticationServices {

    private final UserAuthenticationRepository userRepository;
    private final Encoder encoder;
    private final EmailServices emailServices;


    /* method get authentication by email*/
    @Override
    public UserAuthentication getByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }


    //register
    @Override
    public UserAuthentication register(UserAuthenticationRegisterRequest userAuthenticationRegisterRequest) {


        userAuthenticationRegisterRequest.setPassword(encoder.PasswordEncoder().encode(userAuthenticationRegisterRequest.getPassword()));


        UserAuthentication userAuthentication = userRepository.register(userAuthenticationRegisterRequest);

        OptCode optCode = new OptCode();

        optCode.setUserId(userAuthentication.getUserId());
        optCode.setCreatedDate(LocalDateTime.now());
        optCode.setExpiredDate(LocalDateTime.now().plusMinutes(1));
        optCode.setDigitCode(generateOptCode());

        userRepository.insertVerify(optCode);

        emailServices.sendMail(userAuthentication, optCode.getDigitCode());

        return userAuthentication;
    }


    //    verify code of user
    @Override
    public UserAuthentication verify(Integer code) {

        OptCode optCode = userRepository.getOtpCode(code);

        if (optCode == null) {
            throw new NotFoundException("Code : " + code + " Not Found");
        }

        if (!Objects.equals(optCode.getDigitCode(), code)) {

            throw new NotFoundException("Code : " + code + " Invalid");

        }

        if (LocalDateTime.now().isAfter(optCode.getExpiredDate())) {
            throw new NotFoundException("Code : " + code + " Expired");
        }

        userRepository.verifyCode(optCode.getDigitCode());

        UserAuthentication userAuthentication = userRepository.updateUser(optCode.getUserId());

        //userRepository.deleteCode(code);

        return userAuthentication;
    }

    //forgot password
    @Override
    public UserAuthentication forgotPassword(UserAuthenticationForgotRequest userAuthenticationForgotRequest) {

        UserAuthentication userAuthentication = getByEmail(userAuthenticationForgotRequest.getEmail());

        if (userAuthentication == null) {
            throw new NotFoundException("Users Not Fund ");
        }

        return null;
    }


    //resend code
    @Override
    public UserAuthentication resendCode(String email) {

        UserAuthentication userAuthentication = getByEmail(email);


        if (userAuthentication == null) {
            throw new NotFoundException("User Not Found");
        }

        OptCode optCode = userRepository.getOptCodeByMailId(userAuthentication.getUserId());

        if (LocalDateTime.now().isBefore(optCode.getExpiredDate())) {

            throw new BadRequestException("Code is Not expired please login again ");

        }

        Integer code = generateOptCode();

        optCode.setDigitCode(code);
        optCode.setCreatedDate(LocalDateTime.now());
        optCode.setExpiredDate(LocalDateTime.now().plusMinutes(2));

        userRepository.updateOptCode(optCode);

        emailServices.sendMail(userAuthentication, code);

        return userAuthentication;
    }


    //generate opt code
    static Integer generateOptCode() {

        return Integer.valueOf(new DecimalFormat("000000").format(new Random().nextInt(999999)));
    }

}
