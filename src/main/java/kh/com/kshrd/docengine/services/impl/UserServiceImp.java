package kh.com.kshrd.docengine.services.impl;

import kh.com.kshrd.docengine.configuration.Encoder;
import kh.com.kshrd.docengine.exceptions.BadRequestException;
import kh.com.kshrd.docengine.exceptions.NotFoundException;
import kh.com.kshrd.docengine.model.entity.User;
import kh.com.kshrd.docengine.model.request.UserEditRequest;
import kh.com.kshrd.docengine.repository.UserRepository;
import kh.com.kshrd.docengine.security.services.UserAuthenticationService;
import kh.com.kshrd.docengine.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final UserAuthenticationService userAuthenticationService;
    private final Encoder encoder;

    @Override
    public User closeAccount() {
        User user = userRepository.getUserByUserId(userAuthenticationService.getUserIdOfCurrentUser());
        if (user == null) {
            throw new NotFoundException("User doesn't exist");
        } else {
            return userRepository.closeAccount(userAuthenticationService.getUserIdOfCurrentUser());
        }
    }

    @Override
    public User changePassword(String currentPassword, String newPassword, String confirmNewPassword) {
        if (currentPassword == null) {
            throw new BadRequestException("Current password cannot be null");
        } else if (newPassword == null) {
            throw new BadRequestException("New password cannot be null");
        } else if (confirmNewPassword == null) {
            throw new BadRequestException("Confirm password cannot be null");
        } else if (currentPassword.isBlank()) {
            throw new BadRequestException("Current password cannot be blank or empty");
        } else if (newPassword.isBlank()) {
            throw new BadRequestException("New password cannot be blank or empty");
        } else if (confirmNewPassword.isBlank()) {
            throw new BadRequestException("Confirm password cannot be blank or empty");
        }
        User user = userRepository.getUserByUserId(userAuthenticationService.getUserIdOfCurrentUser());
        if (user == null) {
            throw new NotFoundException("User doesn't exist");
        } else {
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
    }

    @Override
    public String getProfileImage() {
        User user = userRepository.getUserByUserId(userAuthenticationService.getUserIdOfCurrentUser());
        if (user == null) {
            throw new NotFoundException("User doesn't exist");
        } else {
            return userRepository.getProfileImage(userAuthenticationService.getUserIdOfCurrentUser());
        }
    }

    @Override
    public List<User> getAllUser() {
        List<User> users = userRepository.getAllUser();
        if (users.isEmpty()) {
            throw new NotFoundException("Empty user");
        }
        return users;
    }

    @Override
    public User deleteProfileImage() {
        User user = userRepository.getUserByUserId(userAuthenticationService.getUserIdOfCurrentUser());
        if (user == null) {
            throw new NotFoundException("User doesn't exist");
        } else {
            return userRepository.deleteProfileImage(userAuthenticationService.getUserIdOfCurrentUser());
        }
    }

    @Override
    public User getCurrentUser() {
        User user = userRepository.getUserByUserId(userAuthenticationService.getUserIdOfCurrentUser());
        if (user == null) {
            throw new NotFoundException("User doesn't exist");
        } else {
            return userRepository.getCurrentUser(userAuthenticationService.getUserIdOfCurrentUser());
        }
    }

    final String pattern = "^[A-Za-z_][A-Za-z0-9_\\s]{3,39}$";

    @Override
    public User editProfileInformation(UserEditRequest userEditRequest) {
        User user = userRepository.getUserByUserId(userAuthenticationService.getUserIdOfCurrentUser());
        if (user == null) {
            throw new NotFoundException("User doesn't exist");
        } else if(!userEditRequest.getUsername().matches(pattern)){
            throw new BadRequestException("Your username must be have around 4 to 40 character");
        }
        else {
            if (userEditRequest.getUsername() == null || userEditRequest.getUsername().isBlank()) {
                return userRepository.editProfileInformation(userAuthenticationService.getUserIdOfCurrentUser(), user.getUserName(), userEditRequest.getProfileImage());
            } else if (userEditRequest.getProfileImage() == null || userEditRequest.getProfileImage().isBlank()) {
                return userRepository.editProfileInformation(userAuthenticationService.getUserIdOfCurrentUser(), userEditRequest.getUsername(), user.getProfileImage());
            } else {
                return userRepository.editProfileInformation(userAuthenticationService.getUserIdOfCurrentUser(), userEditRequest.getUsername(), userEditRequest.getProfileImage());
            }
        }
    }
}