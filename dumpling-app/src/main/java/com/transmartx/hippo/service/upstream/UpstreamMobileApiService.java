package com.transmartx.hippo.service.upstream;

import com.alibaba.fastjson.JSONObject;
import com.transmartx.hippo.logger.UpstreamApiInvokeLogger;
import com.transmartx.hippo.utils.UuidUtil;
import com.transmartx.hippo.utils.sign.AsiainfoHashMap;
import com.transmartx.hippo.utils.sign.AsiainfoHeader;
import com.transmartx.hippo.utils.sign.RSASignature;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.asynchttpclient.Dsl.asyncHttpClient;
import static org.asynchttpclient.Dsl.config;

/**
 * 上游运营商API调用服务
 *
 * @author: letxig
 */
@Service
@Slf4j
public class UpstreamMobileApiService {

    @Autowired
    private UpstreamApiInvokeLogger upstreamApiInvokeLogger;

    private AsyncHttpClient asyncHttpClient;

    @Value("${upstream.mobile.api.appId}")
    private String appId;

    @Value("${upstream.mobile.api.signPrivateKey}")
    private String SIGN_PRIVATE_KEY;

    @Value("${upstream.mobile.api.httpClient.threadPoolName}")
    private String threadPoolName;

    @Value("${upstream.mobile.api.httpClient.maxConnections}")
    private int maxConnections;

    @Value("${upstream.mobile.api.httpClient.connectTimeout}")
    private int connectTimeout;

    @Value("${upstream.mobile.api.httpClient.readTimeout}")
    private int readTimeout;

    @Value("${upstream.mobile.api.httpClient.requestTimeout}")
    private int requestTimeout;

    @Value("${upstream.mobile.api.httpClient.userAgent}")
    private String userAgent;

    @Value("${upstream.mobile.api.httpClient.ioThreadsCount}")
    private int ioThreadsCount;

    @Value("${upstream.mobile.api.timeout}")
    private int apiExecuteTimeout;

    @Value("${upstream.mobile.api.sendOrderConfirmSms.url}")
    private String sendOrderConfirmSmsUrl;

    @Value("${upstream.mobile.api.submitOrder.url}")
    private String submitOrderUrl;

    @PostConstruct
    public void start() {
//        SSLContext sc = null;
//        try {
//            TrustManager[] trustAllCerts = new TrustManager[]{new TrustAllCertsManager()};
//            sc = SSLContext.getInstance("SSL");
//            sc.init(null, trustAllCerts, new java.security.SecureRandom());
//        } catch (Exception e) {
//            throw new RuntimeException("init TrustManager e");
//        }
        asyncHttpClient = asyncHttpClient(config()
                .setThreadPoolName(threadPoolName)
                .setMaxConnections(maxConnections)
                .setConnectTimeout(connectTimeout)
                .setReadTimeout(readTimeout)
                .setRequestTimeout(requestTimeout)
                .setUserAgent(userAgent)
                .setKeepAlive(true)
//                .setSslContext(new JdkSslContext(sc, false, ClientAuth.NONE))
                .setIoThreadsCount(ioThreadsCount));

        if (log.isInfoEnabled()) {
            log.info(this.getClass().getSimpleName() + " is started");
        }
    }

    /**
     * 停止.
     */
    @PreDestroy
    public void stop() {
        try {
            if (asyncHttpClient != null) {
                asyncHttpClient.close();
            }
        } catch (IOException e) {
            log.error("stop error [" + e.getMessage() + "]", e);
        } finally {
            asyncHttpClient = null;
        }

        if (log.isInfoEnabled()) {
            log.info(this.getClass().getSimpleName() + " is stopped");
        }
    }

    public boolean sendOrderConfirmSms(String orderId, String phoneNumber, String productCode) {
        boolean res = false;
        // 请求头
        AsiainfoHeader head = new AsiainfoHeader();
        head.setAppId(appId);
        head.setNonce(RandomStringUtils.randomAlphabetic(32));
        head.setTimestamp(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
        head.setSign_method("hmac");
        head.setBusiSerial(UuidUtil.getUUID());

        // 构造消息请求
        SendSmsCodeRequest sendSmsCodeRequest = SendSmsCodeRequest.builder().tel(phoneNumber).orderseq(orderId).commlist(Arrays.asList(new SendSmsCodeRequest.Comm(productCode))).build();
        String body = JSONObject.toJSONString(sendSmsCodeRequest);

        AsiainfoHashMap headMap = AsiainfoHashMap.toAsiainfoHashMap(head);
        String content = RSASignature.getSignContent(RSASignature.getSortedMap(headMap)) + body;
        String signStr = RSASignature.sign(content, SIGN_PRIVATE_KEY);
        HttpHeaders headers = new DefaultHttpHeaders();
        Set keys = headMap.keySet();
        Iterator it = keys.iterator();
        while (it.hasNext()) {
            String key = (String)it.next();
            headers.add(key, headMap.get(key));
        }
        headers.add("Content-type", "application/json;charset=UTF-8");
        headers.add("sign", signStr);
        // 发送请求
        Response rsp = doSend(sendOrderConfirmSmsUrl, headers, body);
        if (rsp != null) {
            JSONObject rspJson = JSONObject.parseObject(rsp.getResponseBody());
            String respCode = rspJson.getString("respcode");
            res = "0".equals(respCode);
        }
        return res;
    }

    public JSONObject sendOrderConfirmSmsV2(String orderId, String phoneNumber, String productCode) {
        // 请求头
        AsiainfoHeader head = new AsiainfoHeader();
        head.setAppId(appId);
        head.setNonce(RandomStringUtils.randomAlphabetic(32));
        head.setTimestamp(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
        head.setSign_method("hmac");
        head.setBusiSerial(UuidUtil.getUUID());

        // 构造消息请求
        SendSmsCodeRequest sendSmsCodeRequest = SendSmsCodeRequest.builder().tel(phoneNumber).orderseq(orderId).commlist(Arrays.asList(new SendSmsCodeRequest.Comm(productCode))).build();
        String body = JSONObject.toJSONString(sendSmsCodeRequest);

        AsiainfoHashMap headMap = AsiainfoHashMap.toAsiainfoHashMap(head);
        String content = RSASignature.getSignContent(RSASignature.getSortedMap(headMap)) + body;
        String signStr = RSASignature.sign(content, SIGN_PRIVATE_KEY);
        HttpHeaders headers = new DefaultHttpHeaders();
        Set keys = headMap.keySet();
        Iterator it = keys.iterator();
        while (it.hasNext()) {
            String key = (String)it.next();
            headers.add(key, headMap.get(key));
        }
        headers.add("Content-type", "application/json;charset=UTF-8");
        headers.add("sign", signStr);
        // 发送请求
        Response rsp = doSend(sendOrderConfirmSmsUrl, headers, body);
        return rsp != null ? JSONObject.parseObject(rsp.getResponseBody()) : null;
    }

    private Response doSend(String url, HttpHeaders headers, String body) {
        long start = System.currentTimeMillis();
        try {
            upstreamApiInvokeLogger.info("[upstream-api] send:", url, headers, body);
            return asyncHttpClient.preparePost(url).setHeaders(headers).setBody(body).execute(new AsyncCompletionHandler<Response>() {
                @Override
                public Response onCompleted(Response response) throws Exception {
                    String responseBody = response.getResponseBody();
                    long span = System.currentTimeMillis() - start;
                    upstreamApiInvokeLogger.info("[upstream-api] response: ", url, headers, body, responseBody, span);
                    return response;
                }
            }).get(apiExecuteTimeout, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            upstreamApiInvokeLogger.error("[upstream-api] error:", url, headers, body, (System.currentTimeMillis() - start), e);
            return null;
        }
    }

    public SubmitResponse submit(String orderId, String phoneNumber, String productId, String smsCode) {
        SubmitResponse submitResponse = null;
        // 请求头
        AsiainfoHeader head = new AsiainfoHeader();
        head.setAppId(appId);
        head.setNonce(RandomStringUtils.randomAlphabetic(32));
        head.setTimestamp(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
        head.setSign_method("hmac");
        // 短信验证码
        head.setAuthCode(smsCode);
        head.setBusiSerial(orderId);

        // 构造消息请求
        SubmitRequest submitRequest = SubmitRequest.builder().morecommorder("0")
                .userinfo(SubmitRequest.UserInfo.builder().servernum(phoneNumber).build())
                .productinfo(SubmitRequest.ProductInfo.builder().productid(productId).productgroup("0").producttype("0").ordertype("1").build())
                .build();
        String body = JSONObject.toJSONString(submitRequest);

        AsiainfoHashMap headMap = AsiainfoHashMap.toAsiainfoHashMap(head);
        String content = RSASignature.getSignContent(RSASignature.getSortedMap(headMap)) + body;
        String signStr = RSASignature.sign(content, SIGN_PRIVATE_KEY);
        HttpHeaders headers = new DefaultHttpHeaders();
        Set keys = headMap.keySet();
        Iterator it = keys.iterator();
        while (it.hasNext()) {
            String key = (String)it.next();
            headers.add(key, headMap.get(key));
        }
        headers.add("Content-type", "application/json;charset=UTF-8");
        headers.add("sign", signStr);
        // 发送请求
        Response rsp = doSend(submitOrderUrl, headers, body);
        if (rsp != null) {
            JSONObject rspJson = JSONObject.parseObject(rsp.getResponseBody());
            String respCode = rspJson.getString("respcode");
            if ("0".equals(respCode)) {
                submitResponse = JSONObject.parseObject(rsp.getResponseBody(), SubmitResponse.class);
            }
        }
        return submitResponse;
    }

    public JSONObject submitV2(String orderId, String phoneNumber, String productId, String smsCode) {
        // 请求头
        AsiainfoHeader head = new AsiainfoHeader();
        head.setAppId(appId);
        head.setNonce(RandomStringUtils.randomAlphabetic(32));
        head.setTimestamp(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
        head.setSign_method("hmac");
        // 短信验证码
        head.setAuthCode(smsCode);
        head.setBusiSerial(orderId);

        // 构造消息请求
        SubmitRequest submitRequest = SubmitRequest.builder().morecommorder("0")
                .userinfo(SubmitRequest.UserInfo.builder().servernum(phoneNumber).build())
                .productinfo(SubmitRequest.ProductInfo.builder().productid(productId).productgroup("0").producttype("0").ordertype("1").build())
                .build();
        String body = JSONObject.toJSONString(submitRequest);

        AsiainfoHashMap headMap = AsiainfoHashMap.toAsiainfoHashMap(head);
        String content = RSASignature.getSignContent(RSASignature.getSortedMap(headMap)) + body;
        String signStr = RSASignature.sign(content, SIGN_PRIVATE_KEY);
        HttpHeaders headers = new DefaultHttpHeaders();
        Set keys = headMap.keySet();
        Iterator it = keys.iterator();
        while (it.hasNext()) {
            String key = (String)it.next();
            headers.add(key, headMap.get(key));
        }
        headers.add("Content-type", "application/json;charset=UTF-8");
        headers.add("sign", signStr);
        // 发送请求
        Response rsp = doSend(submitOrderUrl, headers, body);
        return rsp != null ? JSONObject.parseObject(rsp.getResponseBody()) : null;
    }

}
