/*
 * Copyright (c) 2017 Pxene Corporation. All rights reserved.
 * Created on 2017-12-19.
 */

package com.transmartx.hippo.constant;


import com.transmartx.hippo.utils.EnumFindHelper;
import com.transmartx.hippo.utils.EnumKeyGetter;

/**
 * 结果常量枚举
 *
 * @author letxig
 */
public enum ResultEnum {

    SUCCESS(0, "Success"),
    FAIL(-1, "Fail"),
    BAD_REQ(400, "Bad Request"),

    ERROR_UNKNOWN(-100000, "Unknown error"),
    ERROR_TIMEOUT(-100001, "Timeout"),
    ERROR_PARAM_ERROR(-100002, "Parameter error"),
    ERROR_NOT_LOGIN_ERROR(-100003, "Not logged in"),
    LOGIN_ERROR(-100004, "Login abnormal"),
    DSP_ENTITY_STATE_PROCESS_ERROR(-100020, "错误状态"),
    AUTH_ERROR(-100021, "authorization error"),

    //通用
    SIGN_CHECK_ERROR(-100005, "签名不正确"),
    ERROR_EMPTY_DATA(-100006, "No Data"),
    FILE_EMPTY(-100007, "File is empty"),
    FILE_ERROR(-100007, "File error"),
    FILE_EXCEED_1_MB_ERROR(-100007, "File cannot be larger than 1 MB"),
    STATE_PROCESSOR_NOT_FOUND(-100308, "状态处理异常"),
    STATE_ENTITY_NULL_ERROR(-100309, "状态实体为空"),

    // 订单
    ORDER_SMS_CODE_SEND_ERROR(-110000, "验证码获取失败"),
    ORDER_CONFIRM_SUBMIT_ERROR(-110001, "业务办理失败"),
    ORDER_CONFIRM_SUCCESSED_ERROR(-110002, "业务已办理"),
    ORDER_SMS_CODE_SEND_BIZ_ERROR(-110003, "验证码获取失败"),
    ORDER_PRODUCT_NOT_FOUND_ERROR(-110004, "办理的商品不存在"),
    ;

    public int code;
    public String msg;

    ResultEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private static final EnumFindHelper<ResultEnum, Integer> codeHelper = new EnumFindHelper<>(ResultEnum.class, new EnumKeyGetter<ResultEnum, Integer>() {

        @Override
        public Integer getKey(ResultEnum enumValue) {
            return enumValue.code;
        }
    });

    private static final EnumFindHelper<ResultEnum, String> msgHelper = new EnumFindHelper<>(ResultEnum.class, new EnumKeyGetter<ResultEnum, String>() {

        @Override
        public String getKey(ResultEnum enumValue) {
            return enumValue.msg;
        }
    });

    public static ResultEnum findByCode(int code) {
        return codeHelper.findByKey(code, ResultEnum.ERROR_UNKNOWN);
    }

    public static ResultEnum findByMsg(String msg) {
        return msgHelper.findByKey(msg, ResultEnum.ERROR_UNKNOWN);
    }
}
