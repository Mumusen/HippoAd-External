package com.transmartx.hippo.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: letxig
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class SubmitResponseVO {

    private static final Integer RSP_SUCCESS_CODE = 1;

    private int errorCode;

    private String errorMsg;

    @ApiModelProperty("上游运营商的订单编码")
    private String eventOrderSeq;

    public static SubmitResponseVO success(String eventOrderSeq) {
        return SubmitResponseVO.of(RSP_SUCCESS_CODE, "", eventOrderSeq);
    }

    public boolean isSuccess() {
        return this.getErrorCode() == RSP_SUCCESS_CODE;
    }

}
