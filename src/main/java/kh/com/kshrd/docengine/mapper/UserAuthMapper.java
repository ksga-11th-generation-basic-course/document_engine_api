package kh.com.kshrd.docengine.mapper;


import kh.com.kshrd.docengine.dto.UserAuthDTO;
import kh.com.kshrd.docengine.model.entity.UserAuth;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserAuthMapper {

    UserAuthMapper INSTANCE = Mappers.getMapper(UserAuthMapper.class);

    UserAuthDTO toDto(UserAuth entity);

    UserAuth toEntity(UserAuthDTO dto);

}
