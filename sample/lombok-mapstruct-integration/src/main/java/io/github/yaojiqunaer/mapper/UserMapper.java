package io.github.yaojiqunaer.mapper;

import io.github.yaojiqunaer.dto.UserDto;
import io.github.yaojiqunaer.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UserMapper {

    /**
     * 属性相同的属性会自动映射
     * 这里只需指定映射关系 {@link User#username} -> {@link UserDto#nickName}
     */
    @Mapping(target = "nickName", source = "username")
    UserDto toDto(User user);

    @Mapping(target = "username", source = "nickName")
    User toEntity(UserDto userDto);

}
