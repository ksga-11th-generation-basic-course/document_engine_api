package kh.com.kshrd.docengine.repository;


import kh.com.kshrd.docengine.model.entity.UserAuth;
import kh.com.kshrd.docengine.configuration.UuidTypeHandler;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserRepository {

    @Select("SELECT * FROM users WHERE email = #{email}")

    @Results(id = "userMap", value = {

            @Result(property = "userId", column = "user_id", typeHandler = UuidTypeHandler.class),
            @Result(property = "username", column = "username"),
            @Result(property = "email", column = "email"),
            @Result(property = "profileImage", column = "profile_image"),
            @Result(property = "verifyCode", column = "verify_code"),
            @Result(property = "isEnable", column = "is_enable")
    })
    UserAuth getUserByEmail(String email);


   /* @Select("SELECT * FROM users WHERE email = #{email}")
    @Result(property = "userId", column = "user_id", typeHandler = UuidTypeHandler.class)
    @Result(property = "username", column = "username")
    @Result(property = "email", column = "email")
    @Result(property = "profileImage", column = "profile_image")
    @Result(property = "verifyCode", column = "verify_code")
    @Result(property = "isEnable", column = "is_enable")
    UserAuth getUserEmail(String email);*/

}
