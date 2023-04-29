package kh.com.kshrd.docengine.security.services.impl;

import kh.com.kshrd.docengine.security.model.entity.OptCode;
import kh.com.kshrd.docengine.security.model.entity.UserAuthentication;
import kh.com.kshrd.docengine.security.model.request.UserAuthenticationRegisterRequest;
import kh.com.kshrd.docengine.security.repository.UserAuthenticationRepository;
import kh.com.kshrd.docengine.security.services.UserAuthenticationServices;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserAuthenticationServicesImpl implements UserAuthenticationServices {

    private final UserAuthenticationRepository userRepository;

    /* method get authentication by email*/
    @Override
    public UserAuthentication getByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

    @Override
    public void register(UserAuthenticationRegisterRequest userAuthenticationRegisterRequest) {


        UUID userId = userRepository.register(userAuthenticationRegisterRequest);


        OptCode optCode = new OptCode();

        Integer otp= Integer.valueOf(new DecimalFormat("000000").format(new Random().nextInt(999999)));

        System.out.println(otp);

        optCode.setUserId(userId);
        optCode.setCreatedDate(LocalDateTime.now());
        optCode.setExpiredDate(LocalDateTime.now());
        optCode.setDigitCode(otp);


    }

}
