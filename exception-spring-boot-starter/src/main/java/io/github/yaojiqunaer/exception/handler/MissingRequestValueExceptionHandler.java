package io.github.yaojiqunaer.exception.handler;

import io.github.yaojiqunaer.exception.ApiResponse;
import io.github.yaojiqunaer.exception.BaseApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @Title 参数丢失异常处理器
 * @Description: MissingRequestValueExceptionHandler
 * @Create Date: 2024/05/09 14:58
 * @Author xiaodongzhang
 */
public interface MissingRequestValueExceptionHandler {

    /**
     * 处理缺少请求参数的异常。
     * 当客户端请求中缺少必要的查询参数时，此异常会被抛出。
     *
     * @param req HttpServletRequest对象，代表客户端的HTTP请求。
     * @param e   MissingServletRequestParameterException异常实例，包含缺失的参数信息。
     * @return 返回一个表示错误的ApiResponse对象，其中包含关于缺失参数的错误信息。
     */
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    default ApiResponse<?> missingParameterExceptionHandler(HttpServletRequest req,
                                                            MissingServletRequestParameterException e) {
        return BaseApiResponse.error("lost query parameter: " + e.getParameterName());
    }

    /**
     * 处理缺少请求头异常的控制器方法。
     * 当客户端请求缺少必要的请求头时，此方法将被调用，返回一个包含错误信息的ApiResponse对象。
     *
     * @param req HttpServletRequest对象，代表当前的HTTP请求。
     * @param e   MissingRequestHeaderException实例，包含缺少的请求头信息。
     * @return ApiResponse<?> 返回一个表示错误信息的ApiResponse对象。
     */
    @ExceptionHandler(value = MissingRequestHeaderException.class)
    default ApiResponse<?> missingParameterExceptionHandler(HttpServletRequest req,
                                                            MissingRequestHeaderException e) {
        return BaseApiResponse.error("lost header parameter: " + e.getHeaderName());
    }

    /**
     * 处理缺失的路径变量异常。
     * 当请求路径中缺少必须的路径变量时，此异常会被抛出。
     *
     * @param req HttpServletRequest对象，代表当前的HTTP请求。
     * @param e   MissingPathVariableException异常实例redis，包含缺失的路径变量信息。
     * @return 返回一个表示错误的ApiResponse对象，其中包含关于缺失参数的错误信息。
     */
    @ExceptionHandler(value = MissingPathVariableException.class)
    default ApiResponse<?> missingParameterExceptionHandler(HttpServletRequest req,
                                                            MissingPathVariableException e) {
        return BaseApiResponse.error("lost path parameter: " + e.getVariableName());
    }

    /**
     * 处理缺失的矩阵参数异常的处理器。
     * 当请求中缺少定义的矩阵变量时，此处理器将捕获异常并返回一个错误响应。
     *
     * @param req HttpServletRequest 对象，代表当前的HTTP请求。
     * @param e   MissingMatrixVariableException 异常实例，包含缺失的矩阵变量信息。
     * @return ApiResponse<?> 返回一个错误响应，指示缺少的矩阵参数。
     */
    @ExceptionHandler(value = MissingMatrixVariableException.class)
    default ApiResponse<?> missingParameterExceptionHandler(HttpServletRequest req,
                                                            MissingMatrixVariableException e) {
        return BaseApiResponse.error("lost matrix parameter: " + e.getVariableName());
    }

    /**
     * 处理缺失的请求Cookie异常的处理器。
     * 当客户端请求中缺少指定的Cookie时，此处理器将捕获异常并返回一个错误响应。
     *
     * @param req HttpServletRequest对象，代表客户端的HTTP请求。
     * @param e   MissingRequestCookieException异常实例，包含缺失的Cookie名称。
     * @return 返回一个包含错误信息的ApiResponse对象，指示客户端缺少必要的Cookie参数。
     */
    @ExceptionHandler(value = MissingRequestCookieException.class)
    default ApiResponse<?> missingParameterExceptionHandler(HttpServletRequest req,
                                                            MissingRequestCookieException e) {
        return BaseApiResponse.error("lost cookie parameter: " + e.getCookieName());
    }


}
