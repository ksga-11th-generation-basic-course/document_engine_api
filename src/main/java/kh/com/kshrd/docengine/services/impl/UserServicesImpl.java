package kh.com.kshrd.docengine.services.impl;

import kh.com.kshrd.docengine.entity.User;
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
    public User getById(String email) {
//        System.out.println(email);
        return userRepository.getUserId(email);
    }
}
