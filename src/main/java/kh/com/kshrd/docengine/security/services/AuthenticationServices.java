package kh.com.kshrd.docengine.security.services;

import kh.com.kshrd.docengine.security.model.entity.Authentication;

public interface AuthenticationServices {

    /* method get authentication by email*/
    Authentication getByEmail(String email);
}
