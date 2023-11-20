package io.github.yaojiqunaer.exception;

import io.github.yaojiqunaer.exception.exception.InternalException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseApiResponse<T> implements ApiResponse<T> {
    @Serial
    private static final long serialVersionUID = 5373761465753388599L;
    protected Integer code;
    protected String message;
    protected Boolean success;
    protected T data;

    public static <T> BaseApiResponse<T> ok(T data) {
        return new BaseApiResponse<>(200, "ok", true, data);
    }

    public static <T> BaseApiResponse<T> ok() {
        return ok(null);
    }

    public static <T> BaseApiResponse<T> error(String error) {
        return error(500, error);
    }

    public static <T> BaseApiResponse<T> error(Integer code, String error) {
        return new BaseApiResponse<>(code, error, false, null);
    }

    public static <E extends InternalException> BaseApiResponse error(E exception) {
        return error(exception.getCode(), exception.getMessage());
    }

}
