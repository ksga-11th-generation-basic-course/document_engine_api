package kh.com.kshrd.docengine.repository;


import kh.com.kshrd.docengine.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserRepository {

    @Select("SELECT * FROM users WHERE email = #{email}")
//    @Result(property = "userId", column = "user_id")
    @Result(property = "userName", column = "username")
    @Result(property = "profileImage", column = "profile_image")
    @Result(property = "verifyCode", column = "verify_code")
    @Result(property = "isEnable", column = "is_enable")
    User getUserByEmail(String email);


    @Select("SELECT id FROM users WHERE email = #{email}")
    User getUserId(String email);

}
