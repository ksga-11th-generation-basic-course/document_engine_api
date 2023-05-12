package kh.com.kshrd.docengine.security.services.impl;

import jakarta.mail.internet.MimeMessage;
import kh.com.kshrd.docengine.security.model.entity.UserAuthentication;
import kh.com.kshrd.docengine.security.services.EmailService;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailServicesImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendMail(UserAuthentication authentication, Integer code) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setSubject("Welcome " + authentication.getUserName());

            String html = "<!doctype html>\n" +
                    "<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\"\n" +
                    "      xmlns:th=\"http://www.thymeleaf.org\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\"\n" +
                    "          content=\"width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0\">\n" +
                    "    <meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\">\n" +
                    "    <title>Email</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<div> <h1>" + authentication.getEmail() + "</h1></div>\n" +
                    "\n" +
                    "<div> <p>" + code + "</p></div>\n" +
                    "\n" +
                    "<div>" + authentication.getUserName() + "</div>\n" +
                    "</body>\n" +
                    "</html>\n";
            helper.setText(html, true);

            helper.setTo(authentication.getEmail());

            mailSender.send(message);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
