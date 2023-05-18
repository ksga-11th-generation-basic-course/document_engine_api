package kh.com.kshrd.docengine.repository;

import kh.com.kshrd.docengine.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.UUID;

@Mapper
public interface UserRepository {

    @Results(id = "userMap", value = {
            @Result(property = "userId", column = "user_id"),
            @Result(property = "userName", column = "username"),
            @Result(property = "profileImage", column = "profile_image"),
            @Result(property = "isEnable", column = "is_enabled"),
    })
    @Select("UPDATE users SET is_enabled = false WHERE user_id = #{userId} RETURNING *;")
    User closeAccount(UUID userIdOfCurrentUser);

    @ResultMap("userMap")
    @Select("UPDATE users SET username = #{username} WHERE user_id = #{userIdOfCurrentUser} RETURNING *;")
    User changeUsername(UUID userIdOfCurrentUser, String username);

    @ResultMap("userMap")
    @Select("UPDATE users SET password = #{newPassword} WHERE user_id = #{userIdOfCurrentUser} RETURNING *;")
    User changePassword(UUID userIdOfCurrentUser, String newPassword);

    @ResultMap("userMap")
    @Select("SELECT * FROM users WHERE user_id = #{userId};")
    User getUserByUserId(UUID userId);

    @ResultMap("userMap")
    @Select("UPDATE users SET profile_image = #{image} WHERE user_id = #{userIdOfCurrentUser} RETURNING *;")
    User changeProfileImage(UUID userIdOfCurrentUser, String image);

    @Select("SELECT profile_image FROM users WHERE user_id = #{userIdOfCurrentUser};")
    String getProfileImage(UUID userIdOfCurrentUser);

    @ResultMap("userMap")
    @Select("SELECT * FROM users;")
    List<User> getAllUser();

    @Update("UPDATE users SET profile_image = null WHERE user_id = #{userIdOfCurrentUser};")
    void deleteProfileImage(UUID userIdOfCurrentUser);

    @ResultMap("userMap")
    @Select("SELECT * FROM users WHERE user_id = #{userIdOfCurrentUser};")
    User getCurrentUser(UUID userIdOfCurrentUser);
}
