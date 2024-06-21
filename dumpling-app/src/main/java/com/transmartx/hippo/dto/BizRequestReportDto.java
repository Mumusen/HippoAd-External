package com.transmartx.hippo.dto;

import lombok.Data;

/**
 * @author: letxig
 */
@Data
public class BizRequestReportDto {

    /**
     * 日期
     */
    private String dateTime;

    /**
     * 发送短信请求总数
     */
    private long sendSmsTotalCount;

    /**
     * 发送短信请求成功数
     */
    private long sendSmsSuccessCount;

    /**
     * 发送短信请求失败数
     */
    private long sendSmsFailCount;

    /**
     * 业务办理请求总数
     */
    private long bizSubmitTotalCount;

    /**
     * 业务办理请求成功数
     */
    private long bizSubmitSuccessCount;

    /**
     * 业务办理请求失败数
     */
    private long bizSubmitFailCount;

}
