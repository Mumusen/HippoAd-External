package com.transmartx.hippo.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: letxig
 */
@Data
public class ClientUserRetentionModel {

    @ApiModelProperty("物料包id")
    private String mid;

    @ApiModelProperty("项目id")
    private String pid;

    @ApiModelProperty("广告主id")
    private String aid;

    @ApiModelProperty("活动id")
    private String cid;

    @ApiModelProperty("三方链接参数")
    private String url;

    @ApiModelProperty("随机数")
    private String nonce;

    @ApiModelProperty("时间戳(秒)")
    private String ts;

    @ApiModelProperty("签名内容")
    private String sign;

    @ApiModelProperty("留资json")
    private String ext;

}
