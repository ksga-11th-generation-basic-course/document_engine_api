package kh.com.kshrd.docengine.security.services;

import jakarta.mail.MessagingException;
import kh.com.kshrd.docengine.model.request.ContactRequest;
import kh.com.kshrd.docengine.security.model.entity.UserAuthentication;

public interface EmailService {

    void sendMail(UserAuthentication authentication, String code) throws MessagingException;

    void contactUs(ContactRequest contactRequest) throws MessagingException;
}
