package kh.com.kshrd.docengine.security.services.impl;

import jakarta.mail.MessagingException;
import kh.com.kshrd.docengine.configuration.Encoder;
import kh.com.kshrd.docengine.exceptions.BadRequestException;
import kh.com.kshrd.docengine.exceptions.NotFoundException;
import kh.com.kshrd.docengine.exceptions.NotVerifyException;
import kh.com.kshrd.docengine.exceptions.ValueNotEqualException;
import kh.com.kshrd.docengine.model.entity.User;
import kh.com.kshrd.docengine.repository.UserRepository;
import kh.com.kshrd.docengine.security.model.entity.OptCode;
import kh.com.kshrd.docengine.security.model.entity.UserAuthentication;
import kh.com.kshrd.docengine.security.model.request.UserAuthenticationRegisterRequest;
import kh.com.kshrd.docengine.security.model.request.UserAuthenticationRequestWithGoogleAndFacebook;
import kh.com.kshrd.docengine.security.model.request.UserAuthenticationResetPasswordRequest;
import kh.com.kshrd.docengine.security.repository.UserAuthenticationRepository;
import kh.com.kshrd.docengine.security.services.EmailService;
import kh.com.kshrd.docengine.security.services.UserAuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserAuthenticationServicesImpl implements UserAuthenticationService {

    private final UserAuthenticationRepository userAuthenticationRepository;
    private final Encoder encoder;
    private final EmailService emailServices;
    private final UserRepository userRepository;


    /* method get authentication by email*/
    @Override
    public UserAuthentication getByEmail(String email) {

        if (userAuthenticationRepository.getUserByEmail(email) == null) {

            throw new NotFoundException("User Not Found");
        }

        return userAuthenticationRepository.getUserByEmail(email);

    }


    //register
    @Override
    public UserAuthentication register(UserAuthenticationRegisterRequest userAuthenticationRegisterRequest) throws MessagingException {

        List<User> user = userRepository.getAllUser();

        System.out.println(user);

        userAuthenticationRegisterRequest.setPassword(encoder.PasswordEncoder().encode(userAuthenticationRegisterRequest.getPassword()));


        UserAuthentication userAuthentication = userAuthenticationRepository.register(userAuthenticationRegisterRequest);

        OptCode optCode = new OptCode();

        optCode.setUserId(userAuthentication.getUserId());
        optCode.setCreatedDate(LocalDateTime.now());
        optCode.setExpiredDate(LocalDateTime.now().plusMinutes(1).plusSeconds(10));
        optCode.setDigitCode(generateOptCode());

        userAuthenticationRepository.insertVerify(optCode);

        emailServices.sendMail(userAuthentication, optCode.getDigitCode());

        return userAuthentication;
    }


    //    verify code of user
    @Override
    public UserAuthentication verify(String code) {

        OptCode optCode = userAuthenticationRepository.getOtpCode(code);

        if (optCode == null) {
            throw new NotFoundException("Code : " + code + " Not Found");
        }

        if (!Objects.equals(optCode.getDigitCode(), code)) {

            throw new NotFoundException("Code : " + code + " Invalid");

        }

        if (LocalDateTime.now().isAfter(optCode.getExpiredDate())) {
            throw new NotFoundException("Code : " + code + " Expired");
        }

        userAuthenticationRepository.verifyCode(optCode.getDigitCode());

        //userRepository.deleteCode(code);

        return userAuthenticationRepository.updateUser(optCode.getUserId());
    }

    //forgot password
    @Override
    public UserAuthentication forgotPassword(String email) throws MessagingException {

        getByEmail(email);

        return resendCode(email);
    }


    //resend code
    @Override
    public UserAuthentication resendCode(String email) throws MessagingException {

        UserAuthentication userAuthentication = getByEmail(email);


        if (userAuthentication == null) {
            throw new NotFoundException("User Not Found");
        }

        OptCode optCode = userAuthenticationRepository.getOptCodeByMailId(userAuthentication.getUserId());

        if (LocalDateTime.now().isBefore(optCode.getExpiredDate())) {

            throw new BadRequestException("Code is Not expired please login again ");

        }

        String code = generateOptCode();

        optCode.setDigitCode(code);
        optCode.setCreatedDate(LocalDateTime.now());
        optCode.setExpiredDate(LocalDateTime.now().plusMinutes(1).plusSeconds(10));

        userAuthenticationRepository.updateOptCode(optCode);

        emailServices.sendMail(userAuthentication, code);

        return userAuthentication;
    }

    //reset password
    @Override
    public UserAuthentication resetPassword(UserAuthenticationResetPasswordRequest userAuthenticationResetPasswordRequest, String email) {

        UserAuthentication userAuthentication = getByEmail(email);

        if (!Objects.equals(userAuthenticationResetPasswordRequest.getNewPassword(), userAuthenticationResetPasswordRequest.getNewConfirmPassword())) {

            throw new ValueNotEqualException("Your password is not equal !!!");
        }

        userAuthenticationResetPasswordRequest.setNewPassword(encoder.PasswordEncoder().encode(userAuthenticationResetPasswordRequest.getNewPassword()));

        OptCode optCode = userAuthenticationRepository.getOptCodeByMailId(userAuthentication.getUserId());

        if (!optCode.getHasVerified()) {

            throw new NotVerifyException("You need to verify your account !!!");

        }

        userAuthenticationRepository.resetPassword(userAuthenticationResetPasswordRequest, optCode.getUserId());

        return userAuthentication;
    }

    @Override
    public UUID getUserIdOfCurrentUser() {
        UserAuthentication currentUser = (UserAuthentication) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentUser.getUserId();
    }

    @Override
    public Boolean checkIsVerify(String email) {
        return userAuthenticationRepository.checkIsVerify(email);
    }

    @Override
    public UserAuthentication inputEmailToEnableAccount(String email) throws MessagingException {
        getByEmail(email);
        return resendCode(email);
    }

    @Override
    public UserAuthentication verifyForEnableAccount(String optCode) {

        OptCode oc = userAuthenticationRepository.verifyForEnableAccount(optCode);

        return userAuthenticationRepository.enableAccount(oc.getUserId());
    }

    @Override
    public UserAuthentication signUpWithGoogleAndFacebook(UserAuthenticationRequestWithGoogleAndFacebook userAuthenticationRequestWithGoogleAndFacebook) {
        userAuthenticationRequestWithGoogleAndFacebook.setPassword(encoder.PasswordEncoder().encode(userAuthenticationRequestWithGoogleAndFacebook.getPassword()));
        return userAuthenticationRepository.signUpWithGoogleAndFacebook(userAuthenticationRequestWithGoogleAndFacebook);
    }

    //generate opt code
    static String generateOptCode() {
        Random random = new Random();
        int digitCode = random.nextInt(999999);
        return String.format("%06d", digitCode);
    }

}
