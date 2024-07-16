package io.github.yaojiqunaer.controller;


import io.github.yaojiqunaer.entity.GitRepositoryState;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GitController {

    @Resource
    private GitRepositoryState gitRepositoryState;

    @GetMapping("/git")
    public GitRepositoryState git() {
        return gitRepositoryState;
    }


}
