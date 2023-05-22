package kh.com.kshrd.docengine.services;

import kh.com.kshrd.docengine.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    User closeAccount();

    User changeUsername(String username);

    User changePassword(String currentPassword, String newPassword, String confirmNewPassword);

    User changeProfileImage(String image);

    String getProfileImage();

    List<User> getAllUser();

    void deleteProfileImage();

    User getCurrentUser();
}
