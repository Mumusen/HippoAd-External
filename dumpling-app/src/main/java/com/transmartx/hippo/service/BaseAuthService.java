package com.transmartx.hippo.service;

import com.transmartx.hippo.model.AuthHeaderRequest;
import com.transmartx.hippo.utils.IPHelper;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: letxig
 */
public class BaseAuthService {

    protected AuthHeaderRequest getAuthModelFromHead(HttpServletRequest request) {
        AuthHeaderRequest authModel = new AuthHeaderRequest();
        String channelId = request.getHeader("agentId");
        if (channelId != null) {
            authModel.setChannelId(channelId);
        }
        String nonce = request.getHeader("nonce");
        if (nonce != null) {
            authModel.setNonce(nonce);
        }
        String ts = request.getHeader("ts");
        if (ts != null) {
            authModel.setTs(ts);
        }
        String sign = request.getHeader("sign");
        if (sign != null) {
            authModel.setSign(sign);
        }
        String unitId = request.getHeader("unitId");
        if (unitId != null) {
            authModel.setUnitId(unitId);
        }
        String clientIp = request.getHeader("clientIp");
        if (clientIp == null) {
            clientIp = IPHelper.getIp(request);
        }
        authModel.setClientIp(clientIp);
        String clientUA = request.getHeader("clientUA");
        if (clientUA == null) {
            clientUA = request.getHeader("user-agent");
        }
        if (clientUA != null && clientUA.length() > 1000) {
            // 过长截断
            clientUA = clientUA.substring(0, 1000);
        }
        authModel.setClientUA(clientUA);
        return authModel;
    }

    /**
     * 获取客户端IP
     * @param header
     * @return
     */
    protected String getClientIp(AuthHeaderRequest header, HttpServletRequest request) {
        String clientIp = header.getClientIp();
        if (clientIp == null) {
            clientIp = IPHelper.getIp(request);
        }
        return clientIp;
    }

    protected String getClientUA(AuthHeaderRequest header, HttpServletRequest request) {
        String clientUA = header.getClientUA();
        if (clientUA == null) {
            clientUA = request.getHeader("user-agent");
        }
        if (clientUA != null && clientUA.length() > 1000) {
            // 过长截断
            clientUA = clientUA.substring(0, 1000);
        }
        return clientUA;
    }

}
