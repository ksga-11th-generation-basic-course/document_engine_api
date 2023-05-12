package kh.com.kshrd.docengine.security.services;

import kh.com.kshrd.docengine.security.model.entity.UserAuthentication;

public interface EmailService {

    void sendMail(UserAuthentication authentication, Integer code);
}
