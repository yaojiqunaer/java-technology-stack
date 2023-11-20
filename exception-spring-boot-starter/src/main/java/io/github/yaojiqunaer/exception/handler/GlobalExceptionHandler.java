package io.github.yaojiqunaer.exception.handler;

import io.github.yaojiqunaer.exception.BaseApiResponse;
import io.github.yaojiqunaer.exception.exception.BaseInternalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(annotations = {RestController.class, Controller.class})
@Configuration
public class GlobalExceptionHandler implements BaseExceptionHandler{

    @ExceptionHandler(value = {BaseInternalException.class})
    @ResponseStatus(HttpStatus.OK)
    public BaseApiResponse<?> handle(BaseInternalException e) {
        log.error("global exception handle", e);
        return BaseApiResponse.error(e.getMessage());
    }

    @ExceptionHandler(value = {Exception.class})
    public BaseApiResponse<?> handle(Exception e) {
        log.error("global exception handle", e);
        return BaseApiResponse.error(e.getMessage());
    }

}
