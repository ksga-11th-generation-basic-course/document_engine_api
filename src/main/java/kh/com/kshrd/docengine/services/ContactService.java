package kh.com.kshrd.docengine.services;

import jakarta.mail.MessagingException;
import kh.com.kshrd.docengine.model.request.ContactRequest;

public interface ContactService {
    void contactUs(ContactRequest contactRequest) throws MessagingException;
}
