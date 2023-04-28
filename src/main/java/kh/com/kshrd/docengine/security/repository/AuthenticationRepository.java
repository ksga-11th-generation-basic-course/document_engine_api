package kh.com.kshrd.docengine.security.repository;


import kh.com.kshrd.docengine.security.model.entity.Authentication;
import kh.com.kshrd.docengine.configuration.UuidTypeHandler;
import org.apache.ibatis.annotations.*;

@Mapper
public interface AuthenticationRepository {

    @Select("SELECT * FROM users WHERE email = #{email}")

    @Results(id = "userMap", value = {

            @Result(property = "userId", column = "user_id", typeHandler = UuidTypeHandler.class),
            @Result(property = "username", column = "username"),
            @Result(property = "email", column = "email"),
            @Result(property = "password", column = "password"),
            @Result(property = "profileImage", column = "profile_image"),
            @Result(property = "isEnable", column = "is_enabled"),
            @Result(property = "verifyCode", column = "verify_code"),
    })
    Authentication getUserByEmail(String email);

}
