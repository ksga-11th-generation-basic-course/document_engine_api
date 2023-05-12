package kh.com.kshrd.docengine.security.services.impl;

import kh.com.kshrd.docengine.configuration.Encoder;
import kh.com.kshrd.docengine.exceptions.BadRequestException;
import kh.com.kshrd.docengine.exceptions.NotFoundException;
import kh.com.kshrd.docengine.exceptions.NotVerifyException;
import kh.com.kshrd.docengine.exceptions.ValueNotEqualException;
import kh.com.kshrd.docengine.security.model.entity.OptCode;
import kh.com.kshrd.docengine.security.model.entity.UserAuthentication;
import kh.com.kshrd.docengine.security.model.request.UserAuthenticationRegisterRequest;
import kh.com.kshrd.docengine.security.model.request.UserAuthenticationResetPasswordRequest;
import kh.com.kshrd.docengine.security.repository.UserAuthenticationRepository;
import kh.com.kshrd.docengine.security.services.EmailService;
import kh.com.kshrd.docengine.security.services.UserAuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserAuthenticationServicesImpl implements UserAuthenticationService {

    private final UserAuthenticationRepository userRepository;
    private final Encoder encoder;
    private final EmailService emailServices;


    /* method get authentication by email*/
    @Override
    public UserAuthentication getByEmail(String email) {

        if (userRepository.getUserByEmail(email) == null) {

            throw new NotFoundException("User Not Found");
        }

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
        optCode.setExpiredDate(LocalDateTime.now().plusMinutes(3));
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

        //userRepository.deleteCode(code);

        return userRepository.updateUser(optCode.getUserId());
    }

    //forgot password
    @Override
    public UserAuthentication forgotPassword(String email) {

        UserAuthentication userAuthentication = getByEmail(email);

        return resendCode(email);
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

    //reset password
    @Override
    public UserAuthentication resetPassword(UserAuthenticationResetPasswordRequest userAuthenticationResetPasswordRequest, String email) {

        UserAuthentication userAuthentication = getByEmail(email);

        if (!Objects.equals(userAuthenticationResetPasswordRequest.getNewPassword(), userAuthenticationResetPasswordRequest.getConfirmedPassword())) {

            throw new ValueNotEqualException("Your password is not equal !!!");
        }

        userAuthenticationResetPasswordRequest.setNewPassword(encoder.PasswordEncoder().encode(userAuthenticationResetPasswordRequest.getNewPassword()));

        OptCode optCode = userRepository.getOptCodeByMailId(userAuthentication.getUserId());

        if (!optCode.getHasVerified()) {

            throw new NotVerifyException("You need to verify your account !!!");

        }

        userRepository.resetPassword(userAuthenticationResetPasswordRequest, optCode.getUserId());

        return userAuthentication;
    }

    @Override
    public UUID getUserIdOfCurrentUser() {
        UserAuthentication currentUser = (UserAuthentication) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentUser.getUserId();
    }


    //generate opt code
    static Integer generateOptCode() {

        return Integer.valueOf(new DecimalFormat("000000").format(new Random().nextInt(999999)));
    }

}
