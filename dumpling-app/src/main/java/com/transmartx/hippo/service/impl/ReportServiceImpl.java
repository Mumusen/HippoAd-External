package com.transmartx.hippo.service.impl;

import com.transmartx.hippo.dto.BizRequestReportDto;
import com.transmartx.hippo.dto.OrderDetailReportDto;
import com.transmartx.hippo.mapper.ReportMapper;
import com.transmartx.hippo.model.BizRuntimeException;
import com.transmartx.hippo.model.excel.SheetNoNameData;
import com.transmartx.hippo.model.excel.order.BizRequestReportExcelVO;
import com.transmartx.hippo.model.excel.order.OrderDetailReportExcelVO;
import com.transmartx.hippo.service.MailService;
import com.transmartx.hippo.service.ReportService;
import com.transmartx.hippo.utils.EasyExcelUtil;
import com.transmartx.hippo.utils.IPHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: letxig
 */
@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportMapper reportMapper;

    @Autowired
    private HttpServletResponse httpServletResponse;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private MailService mailService;

    @Value("${dumpling.app.report.download.enabled:false}")
    private boolean downloadEnabled;

    @Value("${dumpling.app.report.ipWhiteList}")
    private String ipWhiteList;

    @Override
    public void getAccessCode(String email) {
        List<String> accessEmails = reportMapper.getAccessEmails();
        if (!accessEmails.contains(email)) {
            return;
        }
        String randomCode = RandomStringUtils.randomNumeric(6);
        String content = "验证码: " + randomCode;
        mailService.send(email, "获取业务办理订单报表验证码", content);
    }

    @Override
    public void downloadOrderReport(String agentId, String from, String to) {
        if (!downloadEnabled) {
            return;
        }
        String clientIp = IPHelper.getIp(httpServletRequest);
        if (!ipWhiteList.contains(clientIp)) {
            throw new BizRuntimeException("无权限访问");
        }

        String fileName = "订单数据导出_" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        List<OrderDetailReportDto> orderDetailReportDtos = reportMapper.listOrderDetailReports(from, to, agentId);
        List<BizRequestReportDto> bizRequestReportDtos = reportMapper.listBizRequestReport(from, to, agentId);
        List<BizRequestReportDto> dateRequestReportDtos = reportMapper.listBizRequestReportGroupByDate(from, to, agentId);

        List<OrderDetailReportExcelVO> orderDetailExcelVos = new ArrayList<>();
        List<BizRequestReportExcelVO> bizRequestExcelVOs = new ArrayList<>();
        List<BizRequestReportExcelVO> dateRequestExcelVOs = new ArrayList<>();

        for (OrderDetailReportDto dto : orderDetailReportDtos) {
            OrderDetailReportExcelVO vo = new OrderDetailReportExcelVO();
            BeanUtils.copyProperties(dto, vo);
            orderDetailExcelVos.add(vo);
        }
        for (BizRequestReportDto dto : bizRequestReportDtos) {
            BizRequestReportExcelVO vo = new BizRequestReportExcelVO();
            BeanUtils.copyProperties(dto, vo);
            bizRequestExcelVOs.add(vo);
        }
        for (BizRequestReportDto dto : dateRequestReportDtos) {
            BizRequestReportExcelVO vo = new BizRequestReportExcelVO();
            BeanUtils.copyProperties(dto, vo);
            dateRequestExcelVOs.add(vo);
        }

        if (orderDetailExcelVos.size() == 0) {
            orderDetailExcelVos.add(OrderDetailReportExcelVO.builder().agentId("暂无数据").build());
        }
        if (bizRequestExcelVOs.size() == 0) {
            bizRequestExcelVOs.add(BizRequestReportExcelVO.builder().dateTime("暂无数据").build());
        }
        if (dateRequestExcelVOs.size() == 0) {
            dateRequestExcelVOs.add(BizRequestReportExcelVO.builder().dateTime("暂无数据").build());
        }

        List<SheetNoNameData> sheetNoNameDataList = new ArrayList<>();
        sheetNoNameDataList.add(SheetNoNameData.of(0, "订单明细", OrderDetailReportExcelVO.class, orderDetailExcelVos));
        sheetNoNameDataList.add(SheetNoNameData.of(1, "请求统计", BizRequestReportExcelVO.class, bizRequestExcelVOs));
        sheetNoNameDataList.add(SheetNoNameData.of(2, "请求统计(按天分组)", BizRequestReportExcelVO.class, dateRequestExcelVOs));
        EasyExcelUtil.write2Response(httpServletResponse, fileName, sheetNoNameDataList);
    }


}
