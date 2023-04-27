package kh.com.kshrd.docengine.controller;

import kh.com.kshrd.docengine.dto.UserAuthDTO;
import kh.com.kshrd.docengine.model.entity.UserAuth;
import kh.com.kshrd.docengine.model.response.Response;
import kh.com.kshrd.docengine.security.jwt.JwtTokenUtil;
import kh.com.kshrd.docengine.model.request.UserRequest;
import kh.com.kshrd.docengine.services.UserServices;
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
public class UserAuthController {


    private final UserServices services;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;



    @PostMapping(path = "/login")
    public ResponseEntity<?> login(@RequestBody UserRequest request) throws Exception {
        login(request.getEmail(), request.getPassword());
        final UserDetails userDetails = services.loadUserByUsername(request.getEmail());
        final String token = jwtTokenUtil.generateToken(userDetails);

//        UserAuth userAuth = services.getByEmail(request.getEmail());
//        System.out.println("test"+ userAuth);
   /*     System.out.println(userAuth);*/

//        Response<UserAuthDTO> response = Response.<UserAuthDTO>builder()
//                .status(HttpStatus.OK)
//                .payload(new UserAuthDTO(userAuth.getUsername(), userAuth.getEmail(), token, userAuth.getProfileImage(), userAuth.isEnable()))
//                .dateTime(LocalDateTime.now())
//                .message("User has been login successful")
//                .build();
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
