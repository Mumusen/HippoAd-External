package com.transmartx.hippo.config;

import com.transmartx.hippo.model.BizRuntimeException;
import com.transmartx.hippo.model.ErrorCodeConstant;
import com.transmartx.hippo.model.SmartResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: letxig
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String UNKNOWN_EXCEPTION_MESSAGE = "系统繁忙，请稍后再试。";

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public SmartResult<Void> handleGlobalException(HttpServletRequest request, Exception exception) {
        logExceptionMessage(request, exception);
        return SmartResult.fail(UNKNOWN_EXCEPTION_MESSAGE);
    }

    @ExceptionHandler({BizRuntimeException.class})
    @ResponseBody
    public SmartResult<Void> handleBizRuntimeException(HttpServletRequest request, Exception exception) {
        logExceptionMessage(request, exception);
        return getBizResponse(exception);
    }

    private SmartResult<Void> getBizResponse(Exception exception) {
        if (exception instanceof BizRuntimeException) {
            return SmartResult.custom(((BizRuntimeException) exception).getErrorCode(), ((BizRuntimeException) exception).getErrorMessage());
        }
        return SmartResult.custom(ErrorCodeConstant.CODE_STATUS_SYSTEM_ERROR, exception.getMessage());
    }

    private void logExceptionMessage(HttpServletRequest request, Exception exception) {
        String requestURI = request == null || request.getRequestURI() == null ? "request URI is null." : request.getRequestURI();
        String method = request == null || request.getMethod() == null ? "request method is null." : request.getMethod();
        String exceptionMessage = exception.getMessage();
        if (exceptionMessage == null) {
            Throwable throwable = exception.getCause();
            if (throwable != null) {
                exceptionMessage = throwable.getMessage();
            }
            if (exceptionMessage == null) {
                exceptionMessage = "exception message is null";
            }
        }
        String message = requestURI + ":" + method + ":" + exceptionMessage;
        log.error(message, exception);
    }

}
