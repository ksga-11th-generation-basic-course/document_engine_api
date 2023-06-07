package kh.com.kshrd.docengine.services;

import kh.com.kshrd.docengine.model.entity.User;

import java.util.List;

public interface UserService {
    User closeAccount();
    User changePassword(String currentPassword, String newPassword, String confirmNewPassword);

    String getProfileImage();

    List<User> getAllUser();

    void deleteProfileImage();

    User getCurrentUser();

    User editProfileInformation(String username, String profileImage);
}
