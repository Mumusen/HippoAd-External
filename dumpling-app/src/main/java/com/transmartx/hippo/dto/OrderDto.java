package com.transmartx.hippo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author: letxig
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {

    /**
     *
     */
    private int id;

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 手机号码
     */
    private String phoneNumber;

    /**
     * 业务地区代码
     */
    private String bizAreaCode;

    /**
     * 渠道ID
     */
    private String channelId;

    /**
     * 订单状态
     */
    private int orderStatus;

    /**
     * 商品ID
     */
    private String productId;

    /**
     * 上游订单ID
     */
    private String upstreamOrderId;

    /**
     * 异常
     */
    private String exception;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 客户端ip
     */
    private String clientIp;

    /**
     * 客户端ua(user-agent)
     */
    private String clientUA;

}
