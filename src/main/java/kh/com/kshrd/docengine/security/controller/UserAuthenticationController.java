package kh.com.kshrd.docengine.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import kh.com.kshrd.docengine.security.model.entity.UserAuthentication;
import kh.com.kshrd.docengine.security.model.request.UserAuthenticationLoginRequest;
import kh.com.kshrd.docengine.security.model.request.UserAuthenticationRegisterRequest;
import kh.com.kshrd.docengine.security.model.request.UserAuthenticationRequestWithGoogleAndFacebook;
import kh.com.kshrd.docengine.security.model.request.UserAuthenticationResetPasswordRequest;
import kh.com.kshrd.docengine.security.model.response.UserAuthenticationLoginResponse;
import kh.com.kshrd.docengine.model.response.Response;
import kh.com.kshrd.docengine.security.jwt.JwtTokenUtil;
import kh.com.kshrd.docengine.security.model.response.UserAuthenticationRegisterResponse;
import kh.com.kshrd.docengine.security.services.UserAuthenticationService;
import kh.com.kshrd.docengine.security.services.JwtAuthenticationService;
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
@CrossOrigin
@RequestMapping(path = "/api/v1/")
public class UserAuthenticationController {


    private final JwtAuthenticationService jwtAuthenticationServices;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserAuthenticationService userAuthenticationServices;

    /*    sample test in postman register
    url :  http://localhost:8080/api/v1/user/register
     {
          "username":"menglotkheng",
          "email":"menglotdeveloper@gmail.com",
          "password":"12345"
     }*/
    @PostMapping(path = "authentications/signup")
    @Operation(summary = "signup")
    public ResponseEntity<?> register(@RequestBody @Valid UserAuthenticationRegisterRequest userAuthenticationRegisterRequest) throws MessagingException {

        UserAuthentication user = userAuthenticationServices.register(userAuthenticationRegisterRequest);

        Response<UserAuthenticationRegisterResponse> response = Response.<UserAuthenticationRegisterResponse>builder()
                .message("Authentication successful")
                .status(HttpStatus.OK)
                .payload(new UserAuthenticationRegisterResponse(user.getUserName(), user.getEmail(), user.getProfileImage(), user.getIsEnable()))
                .dateTime(LocalDateTime.now())

                .build();
        return ResponseEntity.ok().body(response);
    }

    /*    sample test in postman verify account
        {
         url :  http://localhost:8080/api/v1/user/verify?code=754167
        }*/
    @PutMapping(path = "authentications/verify")
    @Operation(summary = "Verify")
    public ResponseEntity<?> verify(@RequestParam String code) {

        UserAuthentication user = userAuthenticationServices.verify(code);

        Response<UserAuthenticationRegisterResponse> response = Response.<UserAuthenticationRegisterResponse>builder()
                .message("Verify successful")
                .status(HttpStatus.OK)
                .payload(new UserAuthenticationRegisterResponse(user.getUserName(), user.getEmail(), user.getProfileImage(), user.getIsEnable()))
                .dateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok().body(response);
    }

    /*    sample test in postman forgot password
      url :  http://localhost:8080/api/v1/user/resend?email=menglotdeveloper@gmail.com
     */
    @PutMapping(path = "authentications/forgot/password")
    @Operation(summary = "Forgot Password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) throws MessagingException {

        UserAuthentication user = userAuthenticationServices.forgotPassword(email);

        Response<UserAuthenticationRegisterResponse> response = Response.<UserAuthenticationRegisterResponse>builder()
                .message("Let's check your email")
                .status(HttpStatus.OK)
                .payload(new UserAuthenticationRegisterResponse(user.getUserName(), user.getEmail(), user.getProfileImage(), user.getIsEnable()))
                .dateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok().body(response);

    }

    /*    sample test in postman reset password
   url :  http://localhost:8080/api/v1/user/resend?email=menglotdeveloper@gmail.com

   {
       "newPassword":"12344",
       "confirmedPassword":"12344"
    }
  */
    @PutMapping(path = "authentications/reset/password")
    @Operation(summary = "Reset Password")
    public ResponseEntity<?> resetPassword(@RequestBody UserAuthenticationResetPasswordRequest userAuthenticationResetPasswordRequest, @RequestParam String email) {

        UserAuthentication user = userAuthenticationServices.resetPassword(userAuthenticationResetPasswordRequest, email);

        Response<UserAuthenticationRegisterResponse> response = Response.<UserAuthenticationRegisterResponse>builder()
                .message("Password reset successful")
                .status(HttpStatus.OK)
                .payload(new UserAuthenticationRegisterResponse(user.getUserName(), user.getEmail(), user.getProfileImage(), user.getIsEnable()))
                .dateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok().body(response);

    }

    /*    sample test in postman resend code
     url :  http://localhost:8080/api/v1/user/resend?email=menglotdeveloper@gmail.com
    */
    @PutMapping(path = "authentications/resend")
    @Operation(summary = "Resend Verify Code")
    public ResponseEntity<?> resendCode(@RequestParam String email) throws MessagingException {

        UserAuthentication userAuthentication = userAuthenticationServices.resendCode(email);

        Response<UserAuthenticationRegisterResponse> response = Response.<UserAuthenticationRegisterResponse>builder()
                .message("Resend code successful")
                .status(HttpStatus.OK)
                .payload(new UserAuthenticationRegisterResponse(userAuthentication.getUserName(), userAuthentication.getEmail(), userAuthentication.getProfileImage(), userAuthentication.getIsEnable()))
                .dateTime(LocalDateTime.now())

                .build();
        return ResponseEntity.ok().body(response);
    }

    /*    sample test in postman login
    url : http://localhost:8080/api/v1/user/login
    {
        "email":"menglot@gmail",
        "password":"12345"
    }*/
    @PostMapping(path = "authentications/login")
    @Operation(summary = "Login")
    public ResponseEntity<?> login(@Valid @RequestBody UserAuthenticationLoginRequest authenticationLoginRequest) throws Exception {

        Boolean isVerify = userAuthenticationServices.checkIsVerify(authenticationLoginRequest.getEmail());
        if (isVerify) {
            return getResponseEntity(authenticationLoginRequest);
        }
        return ResponseEntity.notFound().build();
    }

    private ResponseEntity<?> getResponseEntity(@RequestBody @Valid UserAuthenticationLoginRequest authenticationLoginRequest) throws Exception {
        login(authenticationLoginRequest.getEmail(), authenticationLoginRequest.getPassword());

        final UserDetails userDetails = jwtAuthenticationServices.loadUserByUsername(authenticationLoginRequest.getEmail());
        final String token = jwtTokenUtil.generateToken(userDetails);

        UserAuthentication authentication = userAuthenticationServices.getByEmail(authenticationLoginRequest.getEmail());

        Response<UserAuthenticationLoginResponse> response = Response.<UserAuthenticationLoginResponse>builder()
                .message("Authentication successful")
                .status(HttpStatus.OK)
                .payload(new UserAuthenticationLoginResponse(authentication.getUserName(), authentication.getEmail(), token, authentication.getProfileImage(), authentication.getIsEnable()))
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

    @PutMapping("authentications/input/email/to/enable/account")
    @Operation(summary = "Input Email To Enable Account")
    public ResponseEntity<?> inputEmailForEnableAccount(@RequestParam String email) throws MessagingException {
        UserAuthentication userAuthentication = userAuthenticationServices.inputEmailToEnableAccount(email);

        Response<UserAuthenticationRegisterResponse> response = Response.<UserAuthenticationRegisterResponse>builder()
                .message("Resend code successful")
                .status(HttpStatus.OK)
                .payload(new UserAuthenticationRegisterResponse(userAuthentication.getUserName(), userAuthentication.getEmail(), userAuthentication.getProfileImage(), userAuthentication.getIsEnable()))
                .dateTime(LocalDateTime.now())

                .build();
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("authentications/verify/enable/account")
    @Operation(summary = "Verify For Enable Account")
    public ResponseEntity<?> verifyForEnableAccount(@RequestParam String optCode) {
        UserAuthentication userAuthentication = userAuthenticationServices.verifyForEnableAccount(optCode);

        Response<UserAuthenticationRegisterResponse> response = Response.<UserAuthenticationRegisterResponse>builder()
                .message("Resend code successful")
                .status(HttpStatus.OK)
                .payload(new UserAuthenticationRegisterResponse(userAuthentication.getUserName(), userAuthentication.getEmail(), userAuthentication.getProfileImage(), userAuthentication.getIsEnable()))
                .dateTime(LocalDateTime.now())

                .build();
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("authentications/signup/with/google/and/facebook")
    @Operation(summary = "Sign Up With Google And Facebook")
    public ResponseEntity<?> signUpWithGoogleAndFacebook(@RequestBody UserAuthenticationRequestWithGoogleAndFacebook userAuthenticationRequestWithGoogleAndFacebook) {
        UserAuthentication userAuthentication = userAuthenticationServices.signUpWithGoogleAndFacebook(userAuthenticationRequestWithGoogleAndFacebook);
        Response<UserAuthenticationRegisterResponse> response = Response.<UserAuthenticationRegisterResponse>builder()
                .message("Sign up with Google and Facebook successful")
                .status(HttpStatus.OK)
                .payload(new UserAuthenticationRegisterResponse(userAuthentication.getUserName(), userAuthentication.getEmail(), userAuthentication.getProfileImage(), userAuthentication.getIsEnable()))
                .dateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("authentications/login/with/google/and/facebook")
    @Operation(summary = "login With Google And Facebook")
    public ResponseEntity<?> signInWithGoogleAndFacebook(@RequestBody UserAuthenticationLoginRequest authenticationLoginRequest) throws Exception {
        return getResponseEntity(authenticationLoginRequest);
    }
}
