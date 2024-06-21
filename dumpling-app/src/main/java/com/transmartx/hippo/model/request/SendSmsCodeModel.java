package com.transmartx.hippo.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * @author: letxig
 */
@Data
public class SendSmsCodeModel {

    @ApiModelProperty("手机号码")
    private String phoneNumber;

    @ApiModelProperty("商品编码")
    private String productCode;

    @ApiModelProperty("地区代码")
    private String areaCode;

    @ApiModelProperty("扩展字段")
    private Map<String, Object> ext;

    @ApiModelProperty("mock")
    private String rspJson;

}
