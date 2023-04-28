package kh.com.kshrd.docengine.security.services.impl;

import kh.com.kshrd.docengine.security.model.entity.Authentication;
import kh.com.kshrd.docengine.security.repository.AuthenticationRepository;
import kh.com.kshrd.docengine.security.services.AuthenticationServices;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthenticationServicesImpl implements AuthenticationServices {

    private final AuthenticationRepository userRepository;

    /* method get authentication by email*/
    @Override
    public Authentication getByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

}
