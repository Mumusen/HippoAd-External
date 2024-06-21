package com.transmartx.hippo.mapper;

import com.transmartx.hippo.dto.BizRequestReportDto;
import com.transmartx.hippo.dto.OrderDetailReportDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: letxig
 */
public interface ReportMapper {

    List<String> getAccessEmails();

    List<OrderDetailReportDto> listOrderDetailReports(@Param("from") String from, @Param("to") String to, @Param("agentId") String agentId);

    List<BizRequestReportDto> listBizRequestReport(@Param("from") String from, @Param("to") String to, @Param("agentId") String agentId);

    List<BizRequestReportDto> listBizRequestReportGroupByDate(@Param("from") String from, @Param("to") String to, @Param("agentId") String agentId);

}
