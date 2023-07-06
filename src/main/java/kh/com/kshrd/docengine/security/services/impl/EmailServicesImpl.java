package kh.com.kshrd.docengine.security.services.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import kh.com.kshrd.docengine.model.entity.Workspace;
import kh.com.kshrd.docengine.model.request.ContactRequest;
import kh.com.kshrd.docengine.security.model.entity.UserAuthentication;
import kh.com.kshrd.docengine.security.services.EmailService;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;


@Service
@AllArgsConstructor
public class EmailServicesImpl implements EmailService {

    private final JavaMailSender emailSender;
    private final SpringTemplateEngine templateEngine;

    @Override
    public void sendMail(UserAuthentication authentication, String code) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        Context context = new Context();
        context.setVariable("code", code);
        context.setVariable("authentication", authentication);
        helper.setTo(authentication.getEmail());
        helper.setSubject(authentication.getUserName());
        String html = templateEngine.process("sendMail", context);
        helper.setText(html, true);
        emailSender.send(message);
    }

    @Override
    public void contactUs(ContactRequest contactRequest) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        Context context = new Context();
        context.setVariable("contactRequest", contactRequest);
        helper.setFrom(contactRequest.getEmail());
        helper.setTo("sovannak.khengg@gmail.com");
        helper.setSubject(contactRequest.getMessage());
        String html = templateEngine.process("contactUs", context);
        helper.setText(html, true);
        emailSender.send(message);
    }

    @Override
    public void inviteMemberByEmail(Workspace workspace, UserAuthentication userAuthentication) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        Context context = new Context();
        context.setVariable("workspace", workspace);
        context.setVariable("userAuthentication", userAuthentication);
        helper.setTo(userAuthentication.getEmail());
        helper.setSubject(workspace.getWorkspaceName());
        String html = templateEngine.process("inviteMemberByEmail", context);
        helper.setText(html, true);
        emailSender.send(message);
    }
}
