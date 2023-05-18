package kh.com.kshrd.docengine.services.impl;

import jakarta.mail.MessagingException;
import kh.com.kshrd.docengine.model.request.ContactRequest;
import kh.com.kshrd.docengine.security.services.EmailService;
import kh.com.kshrd.docengine.services.ContactService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ContactServiceImp implements ContactService {

    private final EmailService emailService;

    @Override
    public void contactUs(ContactRequest contactRequest) throws MessagingException {
        emailService.contactUs(contactRequest);
    }
}
