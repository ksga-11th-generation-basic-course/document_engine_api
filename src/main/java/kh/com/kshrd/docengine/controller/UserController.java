package kh.com.kshrd.docengine.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import kh.com.kshrd.docengine.model.entity.User;
import kh.com.kshrd.docengine.model.response.Response;
import kh.com.kshrd.docengine.model.response.UserResponse;
import kh.com.kshrd.docengine.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@SecurityRequirement(name = "bearerAuth")
@AllArgsConstructor
//@CrossOrigin
@RequestMapping("/api/v1/")
public class UserController {
    private final UserService userService;

    @PutMapping("users/close/account")
    @Operation(summary = "Close Account")
    public ResponseEntity<?> closeAccount() {
        User user = userService.closeAccount();
        Response<UserResponse> response = Response.<UserResponse>builder()
                .message("Close Account Successful")
                .payload(new UserResponse(user.getUserId(), user.getUserName(), user.getEmail(), user.getProfileImage(), user.getIsEnable()))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("users")
    @Operation(summary = "Get All User *")
    public ResponseEntity<?> getAllUser() {
        Response<List<User>> response = Response.<List<User>>builder()
                .message("Get All User Successful")
                .payload(userService.getAllUser())
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("users/profile")
    @Operation(summary = "Delete Profile Image")
    public ResponseEntity<?> deleteProfileImage() {
        userService.deleteProfileImage();
        Response<User> response = Response.<User>builder()
                .message("Delete Profile Image Successful")
                .payload(null)
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("users/current/user")
    @Operation(summary = "Get Current User")
    public ResponseEntity<?> getCurrentUser() {
        User user = userService.getCurrentUser();
        Response<UserResponse> response = Response.<UserResponse>builder()
                .message("Get Current User Successful")
                .payload(new UserResponse(user.getUserId(), user.getUserName(), user.getEmail(), user.getProfileImage(), user.getIsEnable()))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("users")
    @Operation(summary = "Edit Profile Information")
    public ResponseEntity<?> editProfileInformation(@RequestParam(required = false) String username, @RequestParam(required = false) String profileImage) {
        User user = userService.editProfileInformation(username, profileImage);
        Response<UserResponse> response = Response.<UserResponse>builder()
                .message("Get Current User Successful")
                .payload(new UserResponse(user.getUserId(), user.getUserName(), user.getEmail(), user.getProfileImage(), user.getIsEnable()))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }
}
