package kh.com.kshrd.docengine.security.repository;


import kh.com.kshrd.docengine.security.model.entity.OptCode;
import kh.com.kshrd.docengine.security.model.entity.UserAuthentication;
import kh.com.kshrd.docengine.configuration.UuidTypeHandler;
import kh.com.kshrd.docengine.security.model.request.UserAuthenticationRegisterRequest;
import kh.com.kshrd.docengine.security.model.response.UserAuthenticationRegisterResponse;
import org.apache.ibatis.annotations.*;
import org.springframework.security.core.parameters.P;

import java.util.UUID;

@Mapper
public interface UserAuthenticationRepository {

    @Select("SELECT * FROM users WHERE email = #{email}")

    @Results(id = "userMap", value = {

            @Result(property = "userId", column = "user_id", typeHandler = UuidTypeHandler.class),
            @Result(property = "username", column = "username"),
            @Result(property = "email", column = "email"),
            @Result(property = "password", column = "password"),
            @Result(property = "profileImage", column = "profile_image"),
            @Result(property = "isEnable", column = "is_enabled"),
    })
    UserAuthentication getUserByEmail(String email);

    @Select("INSERT INTO users(username, email,password) VALUES(#{u.username},#{u.email},#{u.password}) RETURNING *")
    @ResultMap("userMap")
    UserAuthentication register(@Param("u") UserAuthenticationRegisterRequest userAuthenticationRegisterRequest);


    @Insert("INSERT INTO opt_codes(digit_code, create_date,expired_date, user_id) VALUES(#{o.digitCode},#{o.createdDate},#{o.expiredDate},#{o.userId})")
//    @Results(id = "codeMap", value = {
//            @Result(property = "optId", column = "opt_id", typeHandler = UuidTypeHandler.class),
//            @Result(property = "digitCode", column = "digit_code"),
//            @Result(property = "createdDate", column = "created_date"),
//            @Result(property = "expiredDate", column = "expired_date"),
//            @Result(property = "hasVerified", column = "has_verified"),
//            @Result(property = "userId", column = "user_id")
//    })
    void verify(@Param("o") OptCode optCode);


    @Select("SELECT * FROM opt_codes WHERE digit_code = #{code}")
    @Result(property = "optId", column = "opt_id", typeHandler = UuidTypeHandler.class)
    @Result(property = "digitCode", column = "digit_code")
    @Result(property = "createdDate", column = "created_date")
    @Result(property = "expiredDate", column = "expired_date")
    @Result(property = "hasVerified", column = "has_verified")
    @Result(property = "userId", column = "user_id", typeHandler = UuidTypeHandler.class)
    OptCode getOtpCode(Integer code);

    @Select("UPDATE users SET is_enabled = true WHERE user_id = #{userId} RETURNING *;")
//    @ResultMap("userMap")
    UserAuthentication updateUser(UUID userId);

}
