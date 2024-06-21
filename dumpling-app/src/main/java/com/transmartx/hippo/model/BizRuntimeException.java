package com.transmartx.hippo.model;

import org.apache.commons.lang3.StringUtils;

/**
 * @author: letxig
 */
public class BizRuntimeException extends RuntimeException {

    private final int errorCode;

    private final String errorMessage;

    private final Object data;

    public BizRuntimeException() {
        this.errorCode = -1;
        this.errorMessage = StringUtils.EMPTY;
        this.data = null;
    }

    public BizRuntimeException(String errorMessage) {
        this(-1, errorMessage, errorMessage);
    }

    public BizRuntimeException(int errorCode, String errorMessage) {
        this(errorCode, errorMessage, errorMessage);
    }

    public BizRuntimeException(int errorCode, String errorMessage, String detailMessage) {
        this(errorCode, errorMessage, detailMessage, null);
    }

    public BizRuntimeException(int errorCode, String errorMessage, String detailMessage, Object data) {
        super(detailMessage);
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
        this.data = data;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Object getData() {
        return data;
    }
}
