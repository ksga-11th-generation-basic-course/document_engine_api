package kh.com.kshrd.docengine.services.impl;

import kh.com.kshrd.docengine.services.UserServices;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserServicesImpl implements UserServices {


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
