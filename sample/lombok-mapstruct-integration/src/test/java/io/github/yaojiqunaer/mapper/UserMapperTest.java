package io.github.yaojiqunaer.mapper;

import io.github.yaojiqunaer.dto.UserDto;
import io.github.yaojiqunaer.entity.User;
import org.junit.jupiter.api.Test;


class UserMapperTest {

    @Test
    void userToUserDto() {
        User user = new User();
        user.setUsername("username");
        user.setAge(18);
        user.setAddress("China");
        user.setEmail("yaoki@github.com");
        user.setPhone("+852 1199");
        UserMapper userMapper = new UserMapperImpl();
        UserDto dto = userMapper.toDto(user);
        System.out.println(dto);
    }

    @Test
    void userDtoToUser() {
        UserDto userDto = new UserDto();
        userDto.setAge(18);
        userDto.setAddress("China");
        userDto.setEmail("yaoki@github.com");
        userDto.setPhone("+852 1199");
        userDto.setNickName("yaoki");
        UserMapper userMapper = new UserMapperImpl();
        User user = userMapper.toEntity(userDto);
        System.out.println(user);
    }

}