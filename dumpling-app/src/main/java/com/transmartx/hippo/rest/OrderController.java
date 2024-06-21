package com.transmartx.hippo.rest;

import com.transmartx.hippo.model.SmartResult;
import com.transmartx.hippo.model.SubmitResponseVO;
import com.transmartx.hippo.model.request.OrderSubmitModel;
import com.transmartx.hippo.model.request.SendSmsCodeModel;
import com.transmartx.hippo.model.response.SendSmsResponseModel;
import com.transmartx.hippo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: letxig
 */
@RestController
@RequestMapping("/api/v1/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/sendConfirmSms")
    public SmartResult<SendSmsResponseModel> sendConfirmSms(@RequestBody SendSmsCodeModel model) {
        return SmartResult.success(orderService.sendConfirmSms(model));
    }

    @PostMapping("/submit")
    public SmartResult<Map<String, String>> submit(@RequestBody OrderSubmitModel model) {
        SubmitResponseVO rsp = orderService.submit(model);
        return rsp.isSuccess() ? SmartResult.success(new HashMap<String, String>(){{
            put("eventOrderSeq", rsp.getEventOrderSeq());
        }}) : SmartResult.custom(rsp.getErrorCode(), rsp.getErrorMsg());
    }

}
