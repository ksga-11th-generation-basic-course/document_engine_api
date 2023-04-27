package kh.com.kshrd.docengine.services;

import kh.com.kshrd.docengine.model.entity.UserAuth;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserServices extends UserDetailsService {

    UserAuth getByEmail(String email);
}
