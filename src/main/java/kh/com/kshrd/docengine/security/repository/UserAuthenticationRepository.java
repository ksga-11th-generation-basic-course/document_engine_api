package kh.com.kshrd.docengine.security.repository;


import kh.com.kshrd.docengine.security.model.entity.OptCode;
import kh.com.kshrd.docengine.security.model.entity.UserAuthentication;
import kh.com.kshrd.docengine.security.model.request.UserAuthenticationRegisterRequest;
import kh.com.kshrd.docengine.security.model.request.UserAuthenticationRequestWithGoogleAndFacebook;
import kh.com.kshrd.docengine.security.model.request.UserAuthenticationResetPasswordRequest;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.UUID;

@Mapper
public interface UserAuthenticationRepository {


    //get user by email
    @Select("SELECT * FROM users WHERE email = #{email}")
    @Results(id = "userAuthMap", value = {
            @Result(property = "userId", column = "user_id"),
            @Result(property = "username", column = "username"),
            @Result(property = "email", column = "email"),
            @Result(property = "password", column = "password"),
            @Result(property = "profileImage", column = "profile_image"),
            @Result(property = "isEnable", column = "is_enabled"),
    })
    UserAuthentication getUserByEmail(String email);

    //register
    @Select("INSERT INTO users(username, email,password) VALUES(#{u.username},#{u.email},#{u.password}) RETURNING *")
    @ResultMap("userAuthMap")
    UserAuthentication register(@Param("u") UserAuthenticationRegisterRequest userAuthenticationRegisterRequest);

    //insert verify code
    @Insert("INSERT INTO opt_codes(digit_code, created_date,expired_date, user_id) VALUES(#{o.digitCode},#{o.createdDate},#{o.expiredDate},#{o.userId})")
    void insertVerify(@Param("o") OptCode optCode);


    //    get opt code using digit code
    @Select("SELECT * FROM opt_codes WHERE digit_code = #{code}")
    @Results(id = "codeMap", value = {
            @Result(property = "optId", column = "opt_id"),
            @Result(property = "digitCode", column = "digit_code"),
            @Result(property = "createdDate", column = "created_date"),
            @Result(property = "expiredDate", column = "expired_date"),
            @Result(property = "hasVerified", column = "has_verified"),
            @Result(property = "userId", column = "user_id")
    })
    OptCode getOtpCode(String code);

    //update opt code
    @Update("UPDATE opt_codes SET digit_code = #{o.digitCode}, created_date = #{o.createdDate} , expired_date = #{o.expiredDate}, has_verified = false WHERE user_id = #{o.userId}")
    void updateOptCode(@Param("o") OptCode optCode);

    //get opt code by user id
    @Select("SELECT * FROM opt_codes WHERE user_id = #{id}")
    @ResultMap("codeMap")
    OptCode getOptCodeByMailId(UUID id);

    //    update users using user id
    @Select("UPDATE users SET is_enabled = true WHERE user_id = #{userId} RETURNING *;")
    @ResultMap("userAuthMap")
    UserAuthentication updateUser(UUID userId);

    //update status
    @Update("UPDATE opt_codes SET has_verified = true WHERE digit_code = #{code}")
    void verifyCode(String code);

    //reset password
    @Update("UPDATE users SET password = #{u.newPassword} WHERE user_id = #{id}")
    void resetPassword(@Param("u") UserAuthenticationResetPasswordRequest userAuthenticationResetPasswordRequest, UUID id);

    @Select("SELECT has_verified FROM opt_codes INNER JOIN users u ON u.user_id = opt_codes.user_id WHERE email = #{email};")
    Boolean checkIsVerify(String email);

    @Select("UPDATE users SET is_enabled = true WHERE user_id = #{userId} RETURNING *;")
    @ResultMap("userAuthMap")
    UserAuthentication enableAccount(UUID userId);

    @Select("UPDATE opt_codes SET has_verified = true WHERE digit_code = #{optCode} RETURNING *;")
    @ResultMap("codeMap")
    OptCode verifyForEnableAccount(String optCode);

    @Select("INSERT INTO users(username, email,password,profile_image) VALUES(#{u.username}, #{u.email}, #{u.password}, #{u.profileImage}) RETURNING *")
    @ResultMap("userAuthMap")
    UserAuthentication signUpWithGoogleAndFacebook(@Param("u") UserAuthenticationRequestWithGoogleAndFacebook userAuthenticationRequestWithGoogleAndFacebook);

    @ResultMap("userAuthMap")
    @Select("SELECT * FROM users")
    List<UserAuthentication> getAllUser();

    @Delete("DELETE FROM users WHERE email = #{email}")
    void removeUserIfNotVerify(String email);
}