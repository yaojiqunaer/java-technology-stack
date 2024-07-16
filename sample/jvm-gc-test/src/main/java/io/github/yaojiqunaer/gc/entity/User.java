package io.github.yaojiqunaer.gc.entity;


import lombok.Data;

import java.util.Random;
import java.util.UUID;

@Data
public class User {
    private String id;
    private String username;
    private String password;
    private Integer age;
    private String address;
    private String role;

    public static User random() {
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername(UUID.randomUUID().toString());
        user.setPassword(UUID.randomUUID().toString());
        user.setAge(new Random().nextInt());
        user.setAddress(UUID.randomUUID().toString());
        user.setRole(UUID.randomUUID().toString());
        return user;
    }

}
