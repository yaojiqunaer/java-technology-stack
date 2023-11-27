package io.github.yaojiqunaer.commons.lang;

import lombok.Getter;

import static org.junit.jupiter.api.Assertions.*;

class EnumCacheTest {

    @Getter
    static enum StatusEnum {

        SUCCESS("success", 200),
        FAIL("fail", 500),
        DEFAULT("default", 404),
        ;

        private String name;
        private int code;

        StatusEnum(String name, int code) {
            this.code = code;
            this.name = name;
        }

        static {
            EnumCache.registerByName(StatusEnum.class);
//            EnumCache.registerByName(StatusEnum.class, StatusEnum.values());
//            EnumCache.registerByValue(StatusEnum.class, StatusEnum::getName);
            EnumCache.registerByValue(StatusEnum.class, StatusEnum::getCode);
            //EnumCache.registerByValue(StatusEnum.class, StatusEnum.values(), StatusEnum::getName);
//            EnumCache.registerByValue(StatusEnum.class, StatusEnum.values(), StatusEnum::getCode);
        }
    }


    @org.junit.jupiter.api.Test
    void get() {
        StatusEnum name = EnumCache.findByName(StatusEnum.class, StatusEnum.SUCCESS.toString(), StatusEnum.DEFAULT);
        StatusEnum value = EnumCache.findByValue(StatusEnum.class, "success", StatusEnum.DEFAULT);
        StatusEnum code = EnumCache.findByValue(StatusEnum.class, 500, StatusEnum.DEFAULT);
        System.out.println(name);
    }
}