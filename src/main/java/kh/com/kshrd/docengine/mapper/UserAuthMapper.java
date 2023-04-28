package kh.com.kshrd.docengine.mapper;


import kh.com.kshrd.docengine.security.model.entity.Authentication;
import kh.com.kshrd.docengine.security.model.response.AuthenticationLoginResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserAuthMapper {

    UserAuthMapper INSTANCE = Mappers.getMapper(UserAuthMapper.class);

    AuthenticationLoginResponse toDto(Authentication entity);

    Authentication toEntity(AuthenticationLoginResponse dto);

}
