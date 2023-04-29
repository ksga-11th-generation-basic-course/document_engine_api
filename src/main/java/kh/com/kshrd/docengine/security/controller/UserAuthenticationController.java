package kh.com.kshrd.docengine.security.controller;

import jakarta.validation.Valid;
import kh.com.kshrd.docengine.security.model.entity.UserAuthentication;
import kh.com.kshrd.docengine.security.model.request.UserAuthenticationLoginRequest;
import kh.com.kshrd.docengine.security.model.request.UserAuthenticationRegisterRequest;
import kh.com.kshrd.docengine.security.model.response.UserAuthenticationLoginResponse;
import kh.com.kshrd.docengine.model.response.Response;
import kh.com.kshrd.docengine.security.jwt.JwtTokenUtil;
import kh.com.kshrd.docengine.security.services.UserAuthenticationServices;
import kh.com.kshrd.docengine.security.services.JwtAuthenticationServices;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/v1/user")
public class UserAuthenticationController {


    private final JwtAuthenticationServices jwtAuthenticationServices;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserAuthenticationServices userAuthenticationServices;


    @PostMapping(path = "/register")
    public ResponseEntity<?> register(@RequestBody UserAuthenticationRegisterRequest userAuthenticationRegisterRequest){

        userAuthenticationServices.register(userAuthenticationRegisterRequest);

        return ResponseEntity.ok().body("Insert completed");
    }


    @PostMapping(path = "/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserAuthenticationLoginRequest authenticationLoginRequest) throws Exception {
        login(authenticationLoginRequest.getEmail(), authenticationLoginRequest.getPassword());
        final UserDetails userDetails = jwtAuthenticationServices.loadUserByUsername(authenticationLoginRequest.getEmail());
        final String token = jwtTokenUtil.generateToken(userDetails);

        UserAuthentication authentication = userAuthenticationServices.getByEmail(authenticationLoginRequest.getEmail());

        Response<UserAuthenticationLoginResponse> response = Response.<UserAuthenticationLoginResponse>builder()
                .message("Authentication successful")
                .status(HttpStatus.OK)
                .payload(new UserAuthenticationLoginResponse(authentication.getUserName(), authentication.getEmail(), token, authentication.getProfileImage(), authentication.isEnable()))
                .dateTime(LocalDateTime.now())

                .build();

        return ResponseEntity.ok().body(response);
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
