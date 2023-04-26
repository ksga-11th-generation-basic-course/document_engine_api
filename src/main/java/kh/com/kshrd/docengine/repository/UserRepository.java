package kh.com.kshrd.docengine.repository;


import kh.com.kshrd.docengine.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserRepository {

    @Select("SELECT * FROM users WHERE email = #{email}")
    User getUserByEmail(String email);


    @Select("SELECT id FROM users WHERE email = #{email}")
    User getUserId(String email);

}
