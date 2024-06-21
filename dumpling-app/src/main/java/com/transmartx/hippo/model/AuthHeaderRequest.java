package com.transmartx.hippo.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: letxig
 */
@Data
public class AuthHeaderRequest {

    @ApiModelProperty("渠道ID")
    private String channelId;

    @ApiModelProperty("随机数")
    private String nonce;

    @ApiModelProperty("时间戳(秒)")
    private String ts;

    @ApiModelProperty("签名内容")
    private String sign;

    @ApiModelProperty("子渠道ID")
    private String unitId;

    @ApiModelProperty("客户端IP")
    private String clientIp;

    @ApiModelProperty("客户端UA")
    private String clientUA;

}
