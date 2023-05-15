package kh.com.kshrd.docengine.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import kh.com.kshrd.docengine.model.User;
import kh.com.kshrd.docengine.model.response.Response;
import kh.com.kshrd.docengine.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@SecurityRequirement(name = "bearerAuth")
@AllArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/")
public class UserController {
    private final UserService userService;

    @PutMapping("users/close/account")
    @Operation(summary = "Close Account")
    public ResponseEntity<Response<User>> closeAccount() {
        Response<User> response = Response.<User>builder()
                .message("Close Account Successful")
                .payload(userService.closeAccount())
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("users/enable/account/{userId}")
    @Operation(summary = "Enable Account")
    public ResponseEntity<Response<User>> enableAccount(@PathVariable UUID userId) {
        Response<User> response = Response.<User>builder()
                .message("Enable Account Successful")
                .payload(userService.enableAccount(userId))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("users/change/username")
    @Operation(summary = "Change Username")
    public ResponseEntity<Response<User>> changeUsername(@RequestParam String username) {
        Response<User> response = Response.<User>builder()
                .message("Change Username Successful")
                .payload(userService.changeUsername(username))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("users/change/password")
    @Operation(summary = "Change Password")
    public ResponseEntity<Response<User>> changePassword(@RequestParam String currentPassword, @RequestParam String newPassword, @RequestParam String confirmNewPassword) {
        Response<User> response = Response.<User>builder()
                .message("Change Password Successful")
                .payload(userService.changePassword(currentPassword, newPassword, confirmNewPassword))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @PutMapping(path = "users/change/profile/image", consumes = {"multipart/form-data"})
    @Operation(summary = "Change Profile Image")
    public ResponseEntity<Response<User>> changeProfileImage(@RequestParam("file") MultipartFile fileImage) {
        Response<User> response = Response.<User>builder()
                .message("Change Profile Image Successful")
                .payload(userService.changeProfileImage(fileImage))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("users/profile/image")
    @Operation(summary = "Get Profile Image")
    public ResponseEntity<Resource> getProfileImage() {
        String image = userService.getProfileImage();
        Path path = Paths.get("src/main/resources/images/" + image);
        try {
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(new InputStreamResource(resource.getInputStream()));
        } catch (Exception e) {
            System.out.println("Error message {} " + e.getMessage());
        }
        return ResponseEntity.notFound().build();
    }
}
