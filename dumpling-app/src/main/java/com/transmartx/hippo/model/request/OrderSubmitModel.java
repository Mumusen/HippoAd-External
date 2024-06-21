package com.transmartx.hippo.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: letxig
 */
@Data
public class OrderSubmitModel {

    @ApiModelProperty("订单ID")
    private String orderId;

    @ApiModelProperty("短信验证码")
    private String smsCode;

    @ApiModelProperty("Mock")
    private String rspJson;

}
