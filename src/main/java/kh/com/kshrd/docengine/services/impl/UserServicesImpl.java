package kh.com.kshrd.docengine.services.impl;

import kh.com.kshrd.docengine.model.entity.UserAuth;
import kh.com.kshrd.docengine.repository.UserRepository;
import kh.com.kshrd.docengine.services.UserServices;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServicesImpl implements UserServices {

    private final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.getUserByEmail(email);
    }

    @Override
    public UserAuth getByEmail(String email) {
        return userRepository.getUserByEmail(email);
//        return userRepository.getUserId(email);
    }
}
