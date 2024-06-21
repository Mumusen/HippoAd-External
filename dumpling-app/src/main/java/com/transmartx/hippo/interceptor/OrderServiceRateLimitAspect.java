package com.transmartx.hippo.interceptor;

import com.transmartx.hippo.constant.Constants;
import com.transmartx.hippo.constant.PeriodEnum;
import com.transmartx.hippo.dto.OrderInvokeRecordDto;
import com.transmartx.hippo.mapper.OrderInvokeRecordMapper;
import com.transmartx.hippo.model.AuthHeaderRequest;
import com.transmartx.hippo.model.BizRuntimeException;
import com.transmartx.hippo.model.ErrorCodeConstant;
import com.transmartx.hippo.model.SubmitResponseVO;
import com.transmartx.hippo.model.request.OrderSubmitModel;
import com.transmartx.hippo.model.request.SendSmsCodeModel;
import com.transmartx.hippo.model.response.SendSmsResponseModel;
import com.transmartx.hippo.service.BaseAuthService;
import com.transmartx.hippo.service.counter.CounterHelper;
import com.transmartx.hippo.service.counter.LocalMinuteCounter;
import com.transmartx.hippo.utils.IPHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;

/**
 * @author: letxig
 */
@Aspect
@Component
@Slf4j
public class OrderServiceRateLimitAspect extends BaseAuthService {

    private static final String RATE_COUNTER_TAG = "order_biz";

    @Value("${dumpling.app.order.service.rateLimit.qpsLimit}")
    private int qpsLimit;

    @Value("${dumpling.app.order.service.rateLimit.ipQpsLimit}")
    private int ipQpsLimit;

    @Value("${dumpling.app.order.service.rateLimit.minuteRateLimit}")
    private int minuteRateLimit;

    @Value("${dumpling.app.order.service.rateLimit.ipMinuteRateLimit}")
    private int ipMinuteRateLimit;

    @Pointcut("execution(* com.transmartx.hippo.service.impl.OrderServiceImpl.*(..))")
    private void pointcut() {}

    @Autowired
    private LocalMinuteCounter localMinuteCounter;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private OrderInvokeRecordMapper orderInvokeRecordMapper;

    @Around(value = "pointcut()")
    public Object around(ProceedingJoinPoint jp) throws Throwable {
        String ip = IPHelper.getIp(request);
        String ipRateKey = RATE_COUNTER_TAG + Constants.COLON + ip;
        String rateKey = RATE_COUNTER_TAG;
        Date now = new Date();
        // 流控
        localMinuteCounter.increment(CounterHelper.genKey(now, rateKey, PeriodEnum.MINUTE.id, LocalMinuteCounter.COUNTER__RATE));
        localMinuteCounter.increment(CounterHelper.genKey(now, ipRateKey, PeriodEnum.MINUTE.id, LocalMinuteCounter.COUNTER__RATE));

        long fragmentInSeconds = DateUtils.getFragmentInSeconds(now, Calendar.MINUTE) + 1;
        long minuteRq = localMinuteCounter.getLong(CounterHelper.genKey(now, rateKey, PeriodEnum.MINUTE.id, LocalMinuteCounter.COUNTER__RATE));
        long ipMinuteRq = localMinuteCounter.getLong(CounterHelper.genKey(now, ipRateKey, PeriodEnum.MINUTE.id, LocalMinuteCounter.COUNTER__RATE));
        long qps = minuteRq / fragmentInSeconds;
        long ipQps = ipMinuteRq / fragmentInSeconds;

        boolean limited = false;
        if (qps >= qpsLimit) {
            limited = true;
            log.warn("[OrderServiceRateLimit] [qps limited] [{}]-[{}]-[{}]-[{}]-[{}]", ip, qps, ipQps, minuteRq, ipMinuteRq);
        } else if (ipQps >= ipQpsLimit) {
            limited = true;
            log.warn("[OrderServiceRateLimit] [ip_qps limited] [{}]-[{}]-[{}]-[{}]-[{}]", ip, qps, ipQps, minuteRq, ipMinuteRq);
        } else if (minuteRq >= minuteRateLimit) {
            limited = true;
            log.warn("[OrderServiceRateLimit] [minute_request limited] [{}]-[{}]-[{}]-[{}]-[{}]", ip, qps, ipQps, minuteRq, ipMinuteRq);
        } else if (ipMinuteRq >= ipMinuteRateLimit) {
            limited = true;
            log.warn("[OrderServiceRateLimit] [ip_minute_request limited] [{}]-[{}]-[{}]-[{}]-[{}]", ip, qps, ipQps, minuteRq, ipMinuteRq);
        }
        if (limited) {
            // 告警
            // 记录
            record(jp, null, new BizRuntimeException(ErrorCodeConstant.CODE_STATUS_SYSTEM_ERROR, "触发流控"));
            throw new BizRuntimeException("系统繁忙，请稍后");
        }
        Object result = jp.proceed();
        // 记录
        record(jp, result, null);
        return result;
    }

    @AfterThrowing(pointcut = "pointcut()", throwing = "e")
    public void afterException(JoinPoint jp, Exception e) {
        // 记录
        record(jp, null, e);
    }

    private void record(JoinPoint jp, Object returnObj, Exception e) {
        try {
            Method method = getMethodName(jp);
            String methodName = method.getName();
            Object[] args = jp.getArgs();
            AuthHeaderRequest authHeaderRequest = getAuthModelFromHead(request);

            OrderInvokeRecordDto orderInvokeRecordDto = OrderInvokeRecordDto.builder()
                    .channelId(authHeaderRequest.getChannelId())
                    .clientIp(authHeaderRequest.getClientIp())
                    .clientUA(authHeaderRequest.getClientUA())
                    .rspCode(200)
                    .build();
            switch (methodName) {
                case "sendConfirmSms": // 验证码下发
                    orderInvokeRecordDto.setBizType("sendConfirmSms");
                    SendSmsCodeModel sendSmsCodeModel = null;
                    for (Object arg : args) {
                        if (arg instanceof SendSmsCodeModel) {
                            sendSmsCodeModel = (SendSmsCodeModel) arg;
                            break;
                        }
                    }
                    if (sendSmsCodeModel != null) {
                        // 请求参数
                        orderInvokeRecordDto.setPhoneNumber(sendSmsCodeModel.getPhoneNumber());
                        orderInvokeRecordDto.setProductId(sendSmsCodeModel.getProductCode());
                    }
                    if (returnObj != null) {
                        // 返回参数
                        if (returnObj instanceof SendSmsResponseModel) {
                            SendSmsResponseModel sendSmsResponse = (SendSmsResponseModel) returnObj;
                            orderInvokeRecordDto.setOrderId(sendSmsResponse.getOrderId());
                        }
                    }
                    break;
                case "submit": // 订单提交
                    orderInvokeRecordDto.setBizType("submit");
                    OrderSubmitModel orderSubmitModel = null;
                    for (Object arg : args) {
                        if (arg instanceof OrderSubmitModel) {
                            orderSubmitModel = (OrderSubmitModel) arg;
                            break;
                        }
                    }
                    if (orderSubmitModel != null) {
                        // 请求参数
                        orderInvokeRecordDto.setOrderId(orderSubmitModel.getOrderId());
                        orderInvokeRecordDto.setSmsCode(orderSubmitModel.getSmsCode());
                    }
                    if (returnObj != null) {
                        // 返回参数
                        if (returnObj instanceof SubmitResponseVO) {
                            SubmitResponseVO submitResponse = (SubmitResponseVO) returnObj;
                            if (!submitResponse.isSuccess()) {
                                orderInvokeRecordDto.setRspCode(submitResponse.getErrorCode());
                                orderInvokeRecordDto.setRspMsg(submitResponse.getErrorMsg());
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
            if (e != null) {
                int rspCode;
                String rspMsg;
                if (e instanceof BizRuntimeException) {
                    BizRuntimeException bizRuntimeException = (BizRuntimeException) e;
                    rspCode = bizRuntimeException.getErrorCode();
                    rspMsg = bizRuntimeException.getErrorMessage();
                } else {
                    rspCode = ErrorCodeConstant.CODE_STATUS_SYSTEM_ERROR;
                    rspMsg = e.getMessage();
                }
                orderInvokeRecordDto.setRspCode(rspCode);
                orderInvokeRecordDto.setRspMsg(rspMsg);
                orderInvokeRecordDto.setException(e.getMessage());
            }
            // 记录db
            orderInvokeRecordMapper.insertOne(orderInvokeRecordDto);
        } catch (Exception e1) {
            log.error("记录订单调用异常: ", e1);
        }

    }

    private Method getMethodName(JoinPoint jp) {
        MethodSignature ms = (MethodSignature) jp.getSignature();
        return ms.getMethod();
    }

    private String getClassName(JoinPoint joinPoint) {
        return joinPoint.getTarget().getClass().getName();
    }

}
