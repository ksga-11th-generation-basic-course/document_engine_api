package kh.com.kshrd.docengine.security.services;

import kh.com.kshrd.docengine.security.model.entity.UserAuthentication;
import kh.com.kshrd.docengine.security.model.request.UserAuthenticationForgotRequest;
import kh.com.kshrd.docengine.security.model.request.UserAuthenticationRegisterRequest;
import kh.com.kshrd.docengine.security.model.request.UserAuthenticationResetPasswordRequest;

import java.util.UUID;

public interface UserAuthenticationServices {

    /* method get authentication by email*/
    UserAuthentication getByEmail(String email);

    /* method for register user*/
    UserAuthentication register(UserAuthenticationRegisterRequest userAuthenticationRegisterRequest);


    UserAuthentication verify(Integer code);

    UserAuthentication forgotPassword(String email);

    UserAuthentication resendCode(String email);

    UserAuthentication resetPassword(UserAuthenticationResetPasswordRequest userAuthenticationResetPasswordRequest, String email);

}
