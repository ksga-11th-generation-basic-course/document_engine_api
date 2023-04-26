package kh.com.kshrd.docengine.services;

import kh.com.kshrd.docengine.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserServices extends UserDetailsService {

    User getById(String email);
}
