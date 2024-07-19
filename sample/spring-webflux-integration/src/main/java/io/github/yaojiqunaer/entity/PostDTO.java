package io.github.yaojiqunaer.entity;

import lombok.Data;

@Data
public class PostDTO {

    private int userId;
    private int id;
    private String title;
    private String body;
}