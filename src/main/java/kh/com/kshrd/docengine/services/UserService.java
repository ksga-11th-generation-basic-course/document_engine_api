package kh.com.kshrd.docengine.services;

import kh.com.kshrd.docengine.model.entity.User;
import kh.com.kshrd.docengine.model.request.UserEditRequest;

import java.util.List;

public interface UserService {
    User closeAccount();
    User changePassword(String currentPassword, String newPassword, String confirmNewPassword);

    String getProfileImage();

    List<User> getAllUser();

    User deleteProfileImage();

    User getCurrentUser();

    User editProfileInformation(UserEditRequest userEditRequest);
}