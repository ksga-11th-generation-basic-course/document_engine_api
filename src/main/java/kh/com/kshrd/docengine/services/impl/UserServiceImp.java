package kh.com.kshrd.docengine.services.impl;

import kh.com.kshrd.docengine.configuration.Encoder;
import kh.com.kshrd.docengine.exceptions.NotFoundException;
import kh.com.kshrd.docengine.model.User;
import kh.com.kshrd.docengine.repository.UserRepository;
import kh.com.kshrd.docengine.security.services.UserAuthenticationService;
import kh.com.kshrd.docengine.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final UserAuthenticationService userAuthenticationService;
    private final Encoder encoder;

    @Override
    public User closeAccount() {
        return userRepository.closeAccount(userAuthenticationService.getUserIdOfCurrentUser());
    }

    @Override
    public User enableAccount(UUID userId) {
        return userRepository.enableAccount(userId);
    }

    @Override
    public User changeUsername(String username) {
        return userRepository.changeUsername(userAuthenticationService.getUserIdOfCurrentUser(), username);
    }

    @Override
    public User changePassword(String currentPassword, String newPassword, String confirmNewPassword) {
        User user = userRepository.getUserByUserId(userAuthenticationService.getUserIdOfCurrentUser());
        boolean isCurrentPasswordMatch = encoder.PasswordEncoder().matches(currentPassword, user.getPassword());
        if (isCurrentPasswordMatch) {
            if (newPassword.equals(confirmNewPassword)) {
                return userRepository.changePassword(userAuthenticationService.getUserIdOfCurrentUser(), encoder.PasswordEncoder().encode(newPassword));
            } else {
                throw new NotFoundException("Password not match");
            }
        } else {
            throw new NotFoundException("Wrong current password");
        }
    }

    @Override
    public User changeProfileImage(MultipartFile fileImage) {
        Path path = Paths.get("src/main/resources/images");

        String image = fileImage.getOriginalFilename();
        UUID uuid = UUID.randomUUID();

        image = uuid + image;

        Path resolvePath = path;

        if (!image.isEmpty()) {
            resolvePath = path.resolve(image);
        }

        try {
            Files.copy(fileImage.getInputStream(), resolvePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            System.out.println("Error message {} " + e.getMessage());
        }
        return userRepository.changeProfileImage(userAuthenticationService.getUserIdOfCurrentUser(), image);
    }

    @Override
    public String getProfileImage() {
        return userRepository.getProfileImage(userAuthenticationService.getUserIdOfCurrentUser());
    }
}
