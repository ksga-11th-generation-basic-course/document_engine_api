package kh.com.kshrd.docengine.services;

import kh.com.kshrd.docengine.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;


public interface UserService {
    User closeAccount();

    User enableAccount(UUID userId);

    User changeUsername(String username);

    User changePassword(String currentPassword, String newPassword, String confirmNewPassword);

    User changeProfileImage(MultipartFile image);

    String getProfileImage();
}
