package kh.com.kshrd.docengine.controller;

import kh.com.kshrd.docengine.configuration.jwt.JwtTokenUtil;
import kh.com.kshrd.docengine.entity.User;
import kh.com.kshrd.docengine.entity.request.UserRequest;
import kh.com.kshrd.docengine.services.UserServices;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/v1/user")
public class UserController {


    private final UserServices services;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    @GetMapping(path = "/get")
    public ResponseEntity<?> get(){
        return ResponseEntity.ok().body("Helllo");
    }


    @PostMapping(path = "/login")
    public ResponseEntity<?> login(@RequestBody UserRequest request) throws Exception {

        login(request.getEmail(), request.getPassword());
        final UserDetails userDetails = services
                .loadUserByUsername(request.getEmail());

        final String token = jwtTokenUtil.generateToken(userDetails);

        User user = services.getById(request.getEmail());

        return ResponseEntity.ok().body(token);
    }

    private void login(String email, String password) throws Exception {

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}
