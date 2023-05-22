package kh.com.kshrd.docengine.mapper;


import kh.com.kshrd.docengine.security.model.entity.UserAuthentication;
import kh.com.kshrd.docengine.security.model.response.UserAuthenticationLoginResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserAuthMapper {

    UserAuthMapper INSTANCE = Mappers.getMapper(UserAuthMapper.class);

    UserAuthenticationLoginResponse toDto(UserAuthentication entity);

    UserAuthentication toEntity(UserAuthenticationLoginResponse dto);

}
