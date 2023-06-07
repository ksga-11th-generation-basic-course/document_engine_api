package kh.com.kshrd.docengine.repository;

import kh.com.kshrd.docengine.model.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.UUID;

@Mapper
public interface UserRepository {

   /* close account*/
    @Results(id = "userMap", value = {
            @Result(property = "userId", column = "user_id"),
            @Result(property = "userName", column = "username"),
            @Result(property = "profileImage", column = "profile_image"),
            @Result(property = "isEnable", column = "is_enabled"),
    })
    @Select("UPDATE users SET is_enabled = false WHERE user_id = #{userId} RETURNING *;")
    User closeAccount(UUID userIdOfCurrentUser);

    /*change username*/
    @ResultMap("userMap")
    @Select("UPDATE users SET username = #{username} WHERE user_id = #{userIdOfCurrentUser} RETURNING *;")
    User changeUsername(UUID userIdOfCurrentUser, String username);

  /*  change password*/
    @ResultMap("userMap")
    @Select("UPDATE users SET password = #{newPassword} WHERE user_id = #{userIdOfCurrentUser} RETURNING *;")
    User changePassword(UUID userIdOfCurrentUser, String newPassword);

    /*get user by userId*/
    @ResultMap("userMap")
    @Select("SELECT * FROM users WHERE user_id = #{userId};")
    User getUserByUserId(UUID userId);

    /*change profile image*/
    @ResultMap("userMap")
    @Select("UPDATE users SET profile_image = #{image} WHERE user_id = #{userIdOfCurrentUser} RETURNING *;")
    User changeProfileImage(UUID userIdOfCurrentUser, String image);

   /* get profile image*/
    @Select("SELECT profile_image FROM users WHERE user_id = #{userIdOfCurrentUser};")
    String getProfileImage(UUID userIdOfCurrentUser);

   /* get all user*/
    @ResultMap("userMap")
    @Select("SELECT * FROM users;")
    List<User> getAllUser();

    /*delete profile image*/
    @Update("UPDATE users SET profile_image = null WHERE user_id = #{userIdOfCurrentUser};")
    void deleteProfileImage(UUID userIdOfCurrentUser);

    /*get current user*/
    @ResultMap("userMap")
    @Select("SELECT * FROM users WHERE user_id = #{userIdOfCurrentUser};")
    User getCurrentUser(UUID userIdOfCurrentUser);


    @ResultMap("userMap")
    @Select("UPDATE users SET username = #{username}, profile_image = #{profileImage} WHERE user_id = #{userIdOfCurrentUser} RETURNING *;")
    User editProfileInformation(UUID userIdOfCurrentUser, String username, String profileImage);


   /* get username by userId*/
    @Select("SELECT username FROM users WHERE user_id = #{userID}")
    String getUserNameByUserId(UUID userID);

    @ResultMap("userMap")
    @Select("SELECT ud.user_id, username, email, profile_image, is_enabled FROM users INNER JOIN user_document ud on users.user_id = ud.user_id WHERE document_id = #{documentId} AND ud.user_id = #{userId};")
    User getUserByUserIdAndDocumentId(UUID userId, UUID documentId);
}
