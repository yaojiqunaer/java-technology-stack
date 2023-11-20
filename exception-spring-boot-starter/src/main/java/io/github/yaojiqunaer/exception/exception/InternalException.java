package io.github.yaojiqunaer.exception.exception;

import java.io.Serializable;

public interface InternalException extends Serializable {

    Integer getCode();

    String getMessage();

}
