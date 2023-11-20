package io.github.yaojiqunaer.exception;

import java.io.Serializable;

public interface ApiResponse<T> extends Serializable {

    Boolean getSuccess();

    Integer getCode();

    String getMessage();

    T getData();

}
