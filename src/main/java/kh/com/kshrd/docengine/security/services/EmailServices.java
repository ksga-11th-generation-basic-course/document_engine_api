package kh.com.kshrd.docengine.security.services;

import kh.com.kshrd.docengine.security.model.entity.UserAuthentication;

public interface EmailServices {

    void sendMail(UserAuthentication authentication, Integer code);
}
