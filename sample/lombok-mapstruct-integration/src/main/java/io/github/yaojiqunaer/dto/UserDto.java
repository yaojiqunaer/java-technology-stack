package io.github.yaojiqunaer.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class UserDto {

    private String nickName;
    private Integer age;
    private String address;
    private String email;
    private String phone;

    public void logTest() {
        log.info("this is lombok log test.");
    }

}
