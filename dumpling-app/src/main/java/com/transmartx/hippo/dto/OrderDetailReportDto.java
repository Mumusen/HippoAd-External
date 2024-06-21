package com.transmartx.hippo.dto;

import lombok.Data;

/**
 * @author: letxig
 */
@Data
public class OrderDetailReportDto {

    /**
     * 代理ID
     */
    private String agentId;

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 上游业务订单ID
     */
    private String upstreamOrderId;

    /**
     * 商品ID
     */
    private String productId;

    /**
     * 手机号
     */
    private String phoneNumber;

    /**
     * 客户端UA
     */
    private String clientUA;

    /**
     * 客户端IP
     */
    private String clientIp;

    /**
     * 订单确认时间
     */
    private String bizOrderTime;

}
