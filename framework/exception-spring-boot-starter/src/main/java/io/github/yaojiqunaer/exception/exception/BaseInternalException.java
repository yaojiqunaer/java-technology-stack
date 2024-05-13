package io.github.yaojiqunaer.exception.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@Data
public class BaseInternalException extends RuntimeException implements InternalException {

    @Serial
    private static final long serialVersionUID = -5483482156289697837L;

    protected Integer code;

    protected String message;

    public BaseInternalException(String message) {
        this(500, message);
    }

    public BaseInternalException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BaseInternalException(Throwable throwable) {
        this(500, throwable.getMessage());
    }

}
