package kh.com.kshrd.docengine.security.controller;

import jakarta.validation.Valid;
import kh.com.kshrd.docengine.security.model.entity.Authentication;
import kh.com.kshrd.docengine.security.model.request.AuthenticationLoginRequest;
import kh.com.kshrd.docengine.security.model.response.AuthenticationLoginResponse;
import kh.com.kshrd.docengine.model.response.Response;
import kh.com.kshrd.docengine.security.jwt.JwtTokenUtil;
import kh.com.kshrd.docengine.security.services.AuthenticationServices;
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
public class AuthenticationController {


    private final JwtAuthenticationServices jwtAuthenticationServices;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationServices authenticationServices;


    @PostMapping(path = "/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthenticationLoginRequest authenticationLoginRequest) throws Exception {
        login(authenticationLoginRequest.getEmail(), authenticationLoginRequest.getPassword());
        final UserDetails userDetails = jwtAuthenticationServices.loadUserByUsername(authenticationLoginRequest.getEmail());
        final String token = jwtTokenUtil.generateToken(userDetails);

        Authentication authentication = authenticationServices.getByEmail(authenticationLoginRequest.getEmail());

        Response<AuthenticationLoginResponse> response = Response.<AuthenticationLoginResponse>builder()
                .message("Authentication successful")
                .status(HttpStatus.OK)
                .payload(new AuthenticationLoginResponse(authentication.getUserName(), authentication.getEmail(), token, authentication.getProfileImage(), authentication.isEnable()))
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
