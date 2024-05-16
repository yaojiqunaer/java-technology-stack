package io.github.yaojiqunaer.entity;

import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    void test() {
        User user = new User()
                .setUsername("yaojiqunaer")
                .setAge(17)
                .setAddress("chengdu");
        System.out.println(user);
    }

}