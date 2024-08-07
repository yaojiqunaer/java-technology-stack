package io.github.yaojiqunaer.exception.handler;

import io.github.yaojiqunaer.exception.ApiResponse;
import io.github.yaojiqunaer.exception.BaseApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.lang.model.element.ElementKind;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public interface BaseExceptionHandler extends MissingRequestValueExceptionHandler {

    /**
     * 处理HTTP请求头部异常
     */
    @ExceptionHandler(value = {HttpMediaTypeException.class})
    default ApiResponse<?> httpHeaderExceptionHandler(HttpServletRequest req, Exception e) {
        if (e instanceof HttpMediaTypeNotAcceptableException) {
            return BaseApiResponse.badRequest("Client accept header not supported");
        } else if (e instanceof HttpMediaTypeNotSupportedException) {
            return BaseApiResponse.badRequest("Request content-type not supported");
        } else {
            return BaseApiResponse.badRequest("Unknown http header exception: " + e.getMessage());
        }
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class, BindException.class})
    default ApiResponse<?> argumentNotValidExceptionHandler(HttpServletRequest req, Exception e) {
        //key：参数名 value：参数校验信心
        Map<String, String> errorMap = new HashMap<>(16);
        List<ObjectError> allErrors;
        if (e instanceof BindException ex) {
            //处理Get请求中 使用@Valid 验证路径中请求实体校验失败后抛出的异常BindException
            allErrors = ex.getBindingResult().getAllErrors();
        } else {
            //处理请求参数格式错误 @RequestBody上validate失败后抛出的异常是MethodArgumentNotValidException异常
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException) e;
            allErrors = ex.getBindingResult().getAllErrors();
        }
        for (ObjectError objectError : allErrors) {
            String msg = objectError.getDefaultMessage();
            if (objectError instanceof FieldError) {
                errorMap.put(((FieldError) objectError).getField(), msg);
            } else {
                errorMap.put(objectError.getObjectName(), msg);
            }
        }
        StringJoiner stringJoiner = new StringJoiner("，");
        errorMap.forEach((key, value) -> stringJoiner.add(value));
        String enMsg = String.format("Validation of form parameters failed, details:[%s]", stringJoiner);
        //各参数校验结果返回
        return BaseApiResponse.badRequest(enMsg);
    }

    /**
     * 请求参数类型校验 Long String类型转换校验
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    default ApiResponse<?> parameterErrorExceptionHandler(HttpServletRequest req,
                                                          MethodArgumentTypeMismatchException e) {
        String message = e.getName() + ":" + e.getMessage();
        return BaseApiResponse.badRequest(message);
    }

    /**
     * MVC参数使用{@link RequestParam}、{@link Validated}注解进行参数校验，抛出的异常是{@link ConstraintViolationException}
     */
    @ExceptionHandler(ConstraintViolationException.class)
    default ApiResponse<?> parameterErrorExceptionHandler(HttpServletRequest req, ConstraintViolationException e) {
        //key：参数名 value：参数校验信息
        Map<String, String> errorMap = new HashMap<>(8);
        //方法参数校验异常 mvc参数
        for (ConstraintViolation<?> constraintViolation : e.getConstraintViolations()) {
            for (Path.Node last : constraintViolation.getPropertyPath()) {
                if (!ElementKind.METHOD.name().equals(last.getKind().name())) {
                    errorMap.put(last.getName(), constraintViolation.getMessage());
                }
            }
        }
        return BaseApiResponse.badRequest("method parameter valid error: " + errorMap);
    }


}
