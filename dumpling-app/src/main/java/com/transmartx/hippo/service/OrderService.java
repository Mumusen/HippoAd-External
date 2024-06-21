package com.transmartx.hippo.service;

import com.transmartx.hippo.model.SubmitResponseVO;
import com.transmartx.hippo.model.request.OrderSubmitModel;
import com.transmartx.hippo.model.request.SendSmsCodeModel;
import com.transmartx.hippo.model.response.SendSmsResponseModel;

/**
 * @author: letxig
 */
public interface OrderService {

    /**
     * 发送确认信息
     * @param model
     * @return
     */
    SendSmsResponseModel sendConfirmSms(SendSmsCodeModel model);

    /**
     * 提交订单
     * @param model
     */
    SubmitResponseVO submit(OrderSubmitModel model);

}
