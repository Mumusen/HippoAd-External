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
public class BizRequestReportExcelVO {

    @ExcelProperty("日期")
    private String dateTime;

    @ExcelProperty("发送短信请求总数")
    private long sendSmsTotalCount;

    @ExcelProperty("发送短信请求成功数")
    private long sendSmsSuccessCount;

    @ExcelProperty("发送短信请求失败数")
    private long sendSmsFailCount;

    @ExcelProperty("业务办理请求总数")
    private long bizSubmitTotalCount;

    @ExcelProperty("业务办理请求成功数")
    private long bizSubmitSuccessCount;

    @ExcelProperty("业务办理请求失败数")
    private long bizSubmitFailCount;

}
