package io.github.yaojiqunaer.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/")
public class HealthCheckController {


    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of("status", "UP");
    }

    @PostMapping("/post")
    public Map<String, String> pingPost() {
        return Map.of("post", "ok");
    }

    @GetMapping("/get")
    public Map<String, String> pingGet() {
        return Map.of("get", "ok");
    }

}
