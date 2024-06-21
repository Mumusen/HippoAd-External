package com.transmartx.hippo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: letxig
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderInvokeRecordDto {

    /**
     * id
     */
    private int id;

    /**
     * 业务类型
     */
    private String bizType;

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 手机号
     */
    private String phoneNumber;

    /**
     * 地区代码
     */
    private String bizAreaCode;

    /**
     * 渠道ID
     */
    private String channelId;

    /**
     * 商品编码
     */
    private String productId;

    /**
     * 返回码
     */
    private int rspCode;

    /**
     * 返回信息
     */
    private String rspMsg;

    /**
     * 异常信息
     */
    private String exception;

    /**
     * 短信验证码
     */
    private String smsCode;

    /**
     * 客户端IP
     */
    private String clientIp;

    /**
     * 客户端UA
     */
    private String clientUA;

}
