package kh.com.kshrd.docengine.security.services.impl;

import jakarta.mail.internet.MimeMessage;
import kh.com.kshrd.docengine.configuration.Encoder;
import kh.com.kshrd.docengine.exceptions.NotFoundException;
import kh.com.kshrd.docengine.security.model.entity.OptCode;
import kh.com.kshrd.docengine.security.model.entity.UserAuthentication;
import kh.com.kshrd.docengine.security.model.request.UserAuthenticationRegisterRequest;
import kh.com.kshrd.docengine.security.repository.UserAuthenticationRepository;
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
    private final JavaMailSender mailSender;


    /* method get authentication by email*/
    @Override
    public UserAuthentication getByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

    @Override
    public UserAuthentication register(UserAuthenticationRegisterRequest userAuthenticationRegisterRequest) {


        userAuthenticationRegisterRequest.setPassword(encoder.PasswordEncoder().encode(userAuthenticationRegisterRequest.getPassword()));


        UserAuthentication userAuthentication = userRepository.register(userAuthenticationRegisterRequest);

        OptCode optCode = new OptCode();

        Integer otp = Integer.valueOf(new DecimalFormat("000000").format(new Random().nextInt(999999)));


        optCode.setUserId(userAuthentication.getUserId());
        optCode.setCreatedDate(LocalDateTime.now());
        optCode.setExpiredDate(LocalDateTime.now());
        optCode.setDigitCode(otp);

        userRepository.insertVerify(optCode);

        sendMail(userAuthentication, optCode.getDigitCode());

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

        UserAuthentication userAuthentication = userRepository.updateUser(optCode.getUserId());

        //userRepository.deleteCode(code);

        return userAuthentication;
    }

    //    send opt code using mail
    @Override
    public void sendMail(UserAuthentication authentication, Integer code) {


        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setSubject("Welcome " + authentication.getUserName());

            String html = "<!doctype html>\n" +
                    "<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\"\n" +
                    "      xmlns:th=\"http://www.thymeleaf.org\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\"\n" +
                    "          content=\"width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0\">\n" +
                    "    <meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\">\n" +
                    "    <title>Email</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<div> <h1>" + authentication.getEmail() + "</h1></div>\n" +
                    "\n" +
                    "<div> <p>" + code + "</p></div>\n" +
                    "\n" +
                    "<div>" + authentication.getUserName() + "</div>\n" +
                    "</body>\n" +
                    "</html>\n";
            helper.setText(html, true);


            helper.setTo(authentication.getEmail());

            mailSender.send(message);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
