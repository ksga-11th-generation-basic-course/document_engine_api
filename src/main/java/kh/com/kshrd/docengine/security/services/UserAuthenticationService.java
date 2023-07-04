package kh.com.kshrd.docengine.security.services;

import jakarta.mail.MessagingException;
import kh.com.kshrd.docengine.security.model.entity.UserAuthentication;
import kh.com.kshrd.docengine.security.model.request.UserAuthenticationRegisterRequest;
import kh.com.kshrd.docengine.security.model.request.UserAuthenticationRequestWithGoogleAndFacebook;
import kh.com.kshrd.docengine.security.model.request.UserAuthenticationResetPasswordRequest;

import java.util.UUID;

public interface UserAuthenticationService {

    /* method get authentication by email*/
    UserAuthentication getByEmail(String email);

    /* method for register user*/
    UserAuthentication register(UserAuthenticationRegisterRequest userAuthenticationRegisterRequest) throws MessagingException;


    UserAuthentication verify(String code);

    UserAuthentication forgotPassword(String email) throws MessagingException;

    UserAuthentication resendCode(String email) throws MessagingException;

    UserAuthentication resetPassword(UserAuthenticationResetPasswordRequest userAuthenticationResetPasswordRequest, String email);

    UUID getUserIdOfCurrentUser();

    Boolean checkIsVerify(String email);

    UserAuthentication inputEmailToEnableAccount(String email) throws MessagingException;

    UserAuthentication verifyForEnableAccount(String optCode);

    UserAuthentication signUpWithGoogleAndFacebook(UserAuthenticationRequestWithGoogleAndFacebook userAuthenticationRequestWithGoogleAndFacebook);

    String getEmail(String email);

}
