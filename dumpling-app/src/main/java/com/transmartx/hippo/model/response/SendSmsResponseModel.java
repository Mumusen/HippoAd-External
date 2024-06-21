package com.transmartx.hippo.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: letxig
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendSmsResponseModel {

    @ApiModelProperty("订单ID")
    private String orderId;

}
