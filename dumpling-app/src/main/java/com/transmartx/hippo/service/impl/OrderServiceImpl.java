package com.transmartx.hippo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.transmartx.hippo.constant.OrderStatusEnum;
import com.transmartx.hippo.constant.ResultEnum;
import com.transmartx.hippo.dto.ChannelDto;
import com.transmartx.hippo.dto.OrderDto;
import com.transmartx.hippo.dto.ProductDto;
import com.transmartx.hippo.mapper.OrderMapper;
import com.transmartx.hippo.mapper.ProductMapper;
import com.transmartx.hippo.model.AuthHeaderRequest;
import com.transmartx.hippo.model.BizRuntimeException;
import com.transmartx.hippo.model.SubmitResponseVO;
import com.transmartx.hippo.model.request.OrderSubmitModel;
import com.transmartx.hippo.model.request.SendSmsCodeModel;
import com.transmartx.hippo.model.response.SendSmsResponseModel;
import com.transmartx.hippo.service.BaseAuthService;
import com.transmartx.hippo.service.ChannelService;
import com.transmartx.hippo.service.OrderService;
import com.transmartx.hippo.service.upstream.SubmitResponse;
import com.transmartx.hippo.service.upstream.UpstreamMobileApiService;
import com.transmartx.hippo.utils.IPHelper;
import com.transmartx.hippo.utils.OrderIdCreator;
import com.transmartx.hippo.utils.SecretUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;



/**
 * 业务订单服务
 *
 * @author: letxig
 */
@Service
@Slf4j
public class OrderServiceImpl extends BaseAuthService implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private HttpServletRequest request;

    private OrderIdCreator orderIdCreator = new OrderIdCreator();

    @Autowired
    private UpstreamMobileApiService upstreamMobileApiService;

    @Value("${dumpling.app.product.aesEncryptKey}")
    private String productAesEncryptKey;

    @Value("${dumpling.app.auth.verifyDisabled:false}")
    private boolean verifyDisabled;

    @Value("${dumpling.app.order.upstream.sendSmsRspCodeWhiteConf}")
    private String sendSmsRspCodeWhiteConf;

    @Value("${dumpling.app.order.upstream.submitOrderRspCodeWhiteConf}")
    private String submitOrderRspCodeWhiteConf;

    private List<String> sendSmsRspCodeWhiteList;

    private List<String> submitOrderRspCodeWhiteList;

    @PostConstruct
    public void init() {
        // 上游渠道接口返回错误码白名单配置
        // 短信下发
        sendSmsRspCodeWhiteList = StringUtils.isNotBlank(sendSmsRspCodeWhiteConf) ? Lists.newArrayList(sendSmsRspCodeWhiteConf.split(";")) : Lists.newArrayList();
        // 订单提交
        submitOrderRspCodeWhiteList = StringUtils.isNotBlank(submitOrderRspCodeWhiteConf) ? Lists.newArrayList(submitOrderRspCodeWhiteConf.split(";")) : Lists.newArrayList();
    }

    @Override
    public SendSmsResponseModel sendConfirmSms(SendSmsCodeModel model) {
        SendSmsResponseModel response;
        AuthHeaderRequest authHeaderRequest = getAuthModelFromHead(request);
        ChannelDto channelDto = channelService.getByChannelId(authHeaderRequest.getChannelId());
        if (channelDto == null) {
            throw new BizRuntimeException("渠道不存在");
        }
        if (verifyData(model, authHeaderRequest, channelDto)) {
            // 校验商品
            String productId = model.getProductCode();
            // TODO: 改本地缓存
            ProductDto productDto = productMapper.selectByProductId(productId);
            if (productDto == null) {
                throw new BizRuntimeException(ResultEnum.ORDER_PRODUCT_NOT_FOUND_ERROR.code, ResultEnum.ORDER_PRODUCT_NOT_FOUND_ERROR.msg);
            }
            // 创建订单
            String orderId = orderIdCreator.createOrderId(RandomStringUtils.randomNumeric(9), channelDto.getId() + "");
            OrderDto orderDto = OrderDto.builder()
                    .orderId(orderId)
                    .phoneNumber(model.getPhoneNumber())
                    .bizAreaCode(model.getAreaCode())
                    .channelId(authHeaderRequest.getChannelId())
                    .orderStatus(OrderStatusEnum.INIT.value)
                    .productId(productId)
                    .upstreamOrderId(StringUtils.EMPTY)
                    .clientIp(authHeaderRequest.getClientIp())
                    .clientUA(authHeaderRequest.getClientUA())
                    .build();
            orderMapper.insertOne(orderDto);
            // 调用上游接口
            JSONObject rspJson = upstreamMobileApiService.sendOrderConfirmSmsV2(orderId, model.getPhoneNumber(), orderDto.getProductId());

            if (rspJson != null) {
                String respCode = rspJson.getString("respcode");
                boolean success = "0".equals(respCode);
                if (!success) {
                    if (sendSmsRspCodeWhiteList.contains("*") || sendSmsRspCodeWhiteList.contains(respCode)) {
                        // 白名单中的错误码，直接返回
                        String errorMsg = rspJson.getString("respdesc");
                        orderMapper.updateOrderStatusWithException(orderId, OrderStatusEnum.FAIL.value, errorMsg);
                        throw new BizRuntimeException(ResultEnum.ORDER_SMS_CODE_SEND_BIZ_ERROR.code, errorMsg);
                    } else {
                        orderMapper.updateOrderStatusWithException(orderId, OrderStatusEnum.FAIL.value, ResultEnum.ORDER_SMS_CODE_SEND_ERROR.msg);
                        throw new BizRuntimeException(ResultEnum.ORDER_SMS_CODE_SEND_ERROR.code, ResultEnum.ORDER_SMS_CODE_SEND_ERROR.msg);
                    }
                }
            } else {
                orderMapper.updateOrderStatusWithException(orderId, OrderStatusEnum.FAIL.value, ResultEnum.ORDER_SMS_CODE_SEND_ERROR.msg);
                throw new BizRuntimeException(ResultEnum.ORDER_SMS_CODE_SEND_ERROR.code, ResultEnum.ORDER_SMS_CODE_SEND_ERROR.msg);
            }
            response = new SendSmsResponseModel(orderId);
        } else {
            log.error("[sendConfirmSms] clientIp:[{}] data: [{}] auth: [{}] 签名校验不正确  ", IPHelper.getIp(request), model, authHeaderRequest);
            throw new BizRuntimeException(ResultEnum.SIGN_CHECK_ERROR.code, ResultEnum.SIGN_CHECK_ERROR.msg);
        }
        return response;
    }

    @Override
    public SubmitResponseVO submit(OrderSubmitModel model) {
        AuthHeaderRequest authHeaderRequest = getAuthModelFromHead(request);
        String responseOrderSeq = null;
        if (verifyData(model, authHeaderRequest)) {
            // 获取订单信息
            String orderId = model.getOrderId();
            // TODO: 改本地缓存
            OrderDto orderDto = orderMapper.selectByOrderId(orderId);
            if (orderDto == null) {
                throw new BizRuntimeException("订单不存在");
            }
            if (orderDto.getOrderStatus() == OrderStatusEnum.FINISH.value) {
                // 业务已办理完成
                log.warn("业务已办理完成，请求:[{}]-[{}], 完成订单:[{}]", authHeaderRequest, model, orderDto);
                return SubmitResponseVO.of(ResultEnum.ORDER_CONFIRM_SUCCESSED_ERROR.code, ResultEnum.ORDER_CONFIRM_SUCCESSED_ERROR.msg, responseOrderSeq);
            }
            String clientIp = authHeaderRequest.getClientIp();
            String clientUA = authHeaderRequest.getClientUA();
            // 更新订单信息
            orderMapper.updateOrderStatus(orderId, OrderStatusEnum.SUBMIT.value, clientIp, clientUA);
            String phoneNumber = orderDto.getPhoneNumber();
            String productId = orderDto.getProductId();
            String smsCode = model.getSmsCode();

            int orderStatus = OrderStatusEnum.FAIL.value;
            responseOrderSeq = StringUtils.EMPTY;
            int errorCode = ResultEnum.ORDER_CONFIRM_SUBMIT_ERROR.code;
            String errorMsg = ResultEnum.ORDER_CONFIRM_SUBMIT_ERROR.msg;
            String recordErrorMsg = errorMsg;
            // 调用上游接口
            JSONObject rspJson = upstreamMobileApiService.submitV2(orderId, phoneNumber, productId, smsCode);
            if (rspJson != null) {
                String respCode = rspJson.getString("respcode");
                if ("0".equals(respCode)) {
                    JSONObject resultJson = rspJson.getJSONObject("result");
                    SubmitResponse submitResponse = JSONObject.parseObject(resultJson.toJSONString(), SubmitResponse.class);
                    if (submitResponse != null) {
                        // 业务办理成功
                        orderStatus = OrderStatusEnum.FINISH.value;
                        responseOrderSeq = submitResponse.getOrderinfo().getOrderseq();
                    }
                } else {
                    JSONObject resultJson = rspJson.getJSONObject("result");
                    recordErrorMsg = rspJson.getString("respdesc");
                    if (resultJson != null) {
                        JSONObject orderinfoJson = resultJson.getJSONObject("orderinfo");
                        if (orderinfoJson != null && orderinfoJson.containsKey("orderseq")) {
                            responseOrderSeq = orderinfoJson.getString("orderseq");
                        }
                    }
                    if (submitOrderRspCodeWhiteList.contains("*") || submitOrderRspCodeWhiteList.contains(respCode)) {
                        // 白名单中的错误码，直接返回
                        errorMsg = rspJson.getString("respdesc");
                    }
                }
            }
            if (orderStatus == OrderStatusEnum.FAIL.value) {
                // 失败更新
                orderMapper.updateUpstreamOrderId(orderId, responseOrderSeq, orderStatus, recordErrorMsg);
                return SubmitResponseVO.of(errorCode, errorMsg, responseOrderSeq);
            }
            // 订单完成更新
            orderMapper.updateUpstreamOrderId(orderId, responseOrderSeq, orderStatus, "");
        } else {
            log.error("[sendConfirmSms] clientIp:[{}] data: [{}] auth: [{}] 签名校验不正确  ", IPHelper.getIp(request), model, authHeaderRequest);
            throw new BizRuntimeException(ResultEnum.SIGN_CHECK_ERROR.code, ResultEnum.SIGN_CHECK_ERROR.msg);
        }
        return SubmitResponseVO.success(responseOrderSeq);
    }

    /**
     * 对接口数据进行签名校验
     * @param model
     * @return
     */
    private boolean verifyData(SendSmsCodeModel model, AuthHeaderRequest authHeaderRequest, ChannelDto channelDto) {
        if (verifyDisabled) {
            return true;
        }
        if ((StringUtils.isEmpty(authHeaderRequest.getSign()) || StringUtils.isEmpty(authHeaderRequest.getTs()) || StringUtils.isEmpty(authHeaderRequest.getNonce()))
                || isTimeExpire(authHeaderRequest.getTs())) {
            return false;
        }
        if (channelDto == null) {
            channelDto = channelService.getByChannelId(authHeaderRequest.getChannelId());
        }
        if (channelDto == null) {
            throw new BizRuntimeException("渠道不存在");
        }
        String secretKey = channelDto.getChannelSecretKey();
        Map<String, String> map = new TreeMap<>();
        if (authHeaderRequest.getUnitId() != null) {
            map.put("unitId", authHeaderRequest.getUnitId());
        }
        map.put("agentId", authHeaderRequest.getChannelId());
        map.put("ts", authHeaderRequest.getTs());
        map.put("nonce", authHeaderRequest.getNonce());
        map.put("phoneNumber", model.getPhoneNumber());
        map.put("productCode", model.getProductCode());
        String areaCode = model.getAreaCode();
        if (areaCode != null) {
            map.put("areaCode", areaCode);
        }
        Map<String, Object> ext = model.getExt();
        if (ext != null) {
            for (Map.Entry<String, Object> entry : ext.entrySet()) {
                if (entry.getKey() != null) {
                    map.put(entry.getKey(), (String) entry.getValue());
                }
            }
        }
        List<String> toSignList = new ArrayList<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            toSignList.add(key + "=" + value);
        }
        // 最后添加secretKey值
        toSignList.add(secretKey);
        String toSignStr = StringUtils.join(toSignList, "&");
        log.info("String to Sign: [{}]", toSignStr);
        String checkSign = SecretUtil.Md5Encrypt(toSignStr).toLowerCase();
        String requestSign = authHeaderRequest.getSign();
        return checkSign.equals(requestSign);
    }

    /**
     * 对接口数据进行签名校验
     * @param model
     * @return
     */
    private boolean verifyData(OrderSubmitModel model, AuthHeaderRequest authHeaderRequest) {
        if (verifyDisabled) {
            return true;
        }
        if ((StringUtils.isEmpty(authHeaderRequest.getSign()) || StringUtils.isEmpty(authHeaderRequest.getTs()) || StringUtils.isEmpty(authHeaderRequest.getNonce()))
                || isTimeExpire(authHeaderRequest.getTs())) {
            return false;
        }
        ChannelDto channelDto = channelService.getByChannelId(authHeaderRequest.getChannelId());
        if (channelDto == null) {
            throw new BizRuntimeException("渠道不存在");
        }
        String secretKey = channelDto.getChannelSecretKey();

        Map<String, String> map = new TreeMap<>();
        if (authHeaderRequest.getUnitId() != null) {
            map.put("unitId", authHeaderRequest.getUnitId());
        }
        map.put("agentId", authHeaderRequest.getChannelId());
        map.put("ts", authHeaderRequest.getTs());
        map.put("nonce", authHeaderRequest.getNonce());
        map.put("orderId", model.getOrderId());
        map.put("smsCode", model.getSmsCode());
        List<String> toSignList = new ArrayList<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            toSignList.add(key + "=" + value);
        }
        // 最后添加secretKey值
        toSignList.add(secretKey);
        String toSignStr = StringUtils.join(toSignList, "&");
        log.info("String to Sign: [{}]", toSignStr);
        String checkSign = SecretUtil.Md5Encrypt(toSignStr).toLowerCase();
        String requestSign = authHeaderRequest.getSign();
        return checkSign.equals(requestSign);
    }

    private boolean isTimeExpire(String ts) {
        long interval = 600000;
        Date expireDate = new Date(Long.parseLong(ts) + interval);
        return new Date().after(expireDate);
    }

    /**
     * 获取原始运营商的商品编码
     * @param encryptedProductId
     * @return
     */
    // 已废除，后续商品编码不加密
//    private String getSourceProductCode(String encryptedProductId) {
//        String productCode = SecurityHelper.aesDecryptHex(productAesEncryptKey, encryptedProductId);
//        if (productCode == null) {
//            throw new BizRuntimeException("商品编码不正确");
//        }
//        return productCode;
//    }

}
