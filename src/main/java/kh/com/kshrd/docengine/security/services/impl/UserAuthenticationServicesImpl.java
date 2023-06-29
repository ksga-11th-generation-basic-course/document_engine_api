package kh.com.kshrd.docengine.security.services.impl;

import jakarta.mail.MessagingException;
import kh.com.kshrd.docengine.configuration.Encoder;
import kh.com.kshrd.docengine.exceptions.*;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

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

        List<User> users = userRepository.getAllUser();

        for (User user : users) {
            if (userAuthenticationRegisterRequest.getEmail().equals(user.getEmail())) {
                throw new NotDuplicateException("This email has already exist");
            }
        }

        if (!isValidEmail(userAuthenticationRegisterRequest.getEmail())) {
            throw new BadRequestException("Invalid Email");
        } else if (!isValidPassword(userAuthenticationRegisterRequest.getPassword())){
            throw new BadRequestException("Invalid Password");
        }

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

    private static final String GMAIL_PATTERN =
            "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@gmail.com$";

    private static final String YAHOO_PATTERN =
            "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@yahoo.com$";

    private static final String HOTMAIL_PATTERN =
            "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@hotmail.com$";

    private static final Pattern gmailPattern = Pattern.compile(GMAIL_PATTERN);
    private static final Pattern yahooPattern = Pattern.compile(YAHOO_PATTERN);
    private static final Pattern hotmailPattern = Pattern.compile(HOTMAIL_PATTERN);

    public static boolean isValidEmail(String email) {
        return gmailPattern.matcher(email).matches() ||
                yahooPattern.matcher(email).matches() ||
                hotmailPattern.matcher(email).matches();
    }

    private static final String PASSWORD_PATTERN =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    public static boolean isValidPassword(String password) {
        return pattern.matcher(password).matches();
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

        if(userAuthenticationResetPasswordRequest.getNewPassword() == null){
            throw new BadRequestException("New password cannot be null");
        } else if(userAuthenticationResetPasswordRequest.getNewConfirmPassword() == null){
            throw new BadRequestException("Confirm new password cannot be null");
        } else if (userAuthenticationResetPasswordRequest.getNewPassword().isBlank()) {
            throw new BadRequestException("New password cannot be blank or empty");
        } else if (userAuthenticationResetPasswordRequest.getNewConfirmPassword().isBlank()) {
            throw new BadRequestException("Confirm new password cannot be blank or empty");
        }

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

        OptCode OPTCode = userAuthenticationRepository.getOtpCode(optCode);

        if (OPTCode == null) {
            throw new NotFoundException("Code : " + optCode + " Not Found");
        }

        if (!Objects.equals(OPTCode.getDigitCode(), optCode)) {

            throw new NotFoundException("Code : " + optCode + " Invalid");

        }

        if (LocalDateTime.now().isAfter(OPTCode.getExpiredDate())) {
            throw new NotFoundException("Code : " + optCode + " Expired");
        }

        OptCode oc = userAuthenticationRepository.verifyForEnableAccount(OPTCode.getDigitCode());

        return userAuthenticationRepository.enableAccount(oc.getUserId());
    }

    @Override
    public UserAuthentication signUpWithGoogleAndFacebook(UserAuthenticationRequestWithGoogleAndFacebook userAuthenticationRequestWithGoogleAndFacebook) {
        List<User> users = userRepository.getAllUser();

        for (User user : users) {
            if (userAuthenticationRequestWithGoogleAndFacebook.getEmail().equals(user.getEmail())) {
                throw new NotDuplicateException("This email has already exist");
            }
        }
        userAuthenticationRequestWithGoogleAndFacebook.setPassword(encoder.PasswordEncoder().encode(userAuthenticationRequestWithGoogleAndFacebook.getPassword()));
        return userAuthenticationRepository.signUpWithGoogleAndFacebook(userAuthenticationRequestWithGoogleAndFacebook);
    }

    //generate opt code
    static String generateOptCode() {
        Random random = new Random();
        int digitCode = random.nextInt(999999);
        return String.format("%06d", digitCode);
    }

    @Scheduled(fixedDelay = 600000)
    public void removeUserIfNotVerify() {
        List<UserAuthentication> userAuthentications = userAuthenticationRepository.getAllUser();
        for(UserAuthentication userAuthentication : userAuthentications){
            Boolean isVerify = userAuthenticationRepository.checkIsVerify(userAuthentication.getEmail());
            if(isVerify == null || !isVerify){
                userAuthenticationRepository.removeUserIfNotVerify(userAuthentication.getEmail());
            }
        }
    }

}