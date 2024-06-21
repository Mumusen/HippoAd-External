package com.transmartx.hippo.model.excel.order;

import com.alibaba.excel.annotation.ExcelProperty;
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
public class OrderDetailReportExcelVO {

    @ExcelProperty("代理ID")
    private String agentId;

    @ExcelProperty("订单ID")
    private String orderId;

    @ExcelProperty("创建时间")
    private String createTime;

    @ExcelProperty("上游业务订单ID")
    private String upstreamOrderId;

    @ExcelProperty("商品ID")
    private String productId;

    @ExcelProperty("手机号")
    private String phoneNumber;

    @ExcelProperty("客户端UA")
    private String clientUA;

    @ExcelProperty("客户端IP")
    private String clientIp;

    @ExcelProperty("订单确认时间")
    private String bizOrderTime;

}
