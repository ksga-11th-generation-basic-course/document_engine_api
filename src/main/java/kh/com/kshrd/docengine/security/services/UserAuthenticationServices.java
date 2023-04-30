package kh.com.kshrd.docengine.security.services;

import kh.com.kshrd.docengine.security.model.entity.UserAuthentication;
import kh.com.kshrd.docengine.security.model.request.UserAuthenticationRegisterRequest;

public interface UserAuthenticationServices {

    /* method get authentication by email*/
    UserAuthentication getByEmail(String email);

    /* method for register user*/
    UserAuthentication register(UserAuthenticationRegisterRequest userAuthenticationRegisterRequest);


    UserAuthentication verifycation(Integer code);


}
