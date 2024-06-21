package com.transmartx.hippo.utils;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * @author: letxig
 */
@Slf4j
public class IPHelper {

    private static final char DOT = '.';

    public static final String DEFAULT_LOCAL_IP = "127.0.0.1";

    private static InetAddress localHost;

    static {
        try {
            localHost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
//            e.printStackTrace();
        }
    }

    private IPHelper() {
    }

    /**
     * 获取本机IP.
     *
     * @return
     */
    public static String getLocalHostIp() {
        if (localHost != null && !(DEFAULT_LOCAL_IP.equals(localHost.getHostAddress()))) {
            return localHost.getHostAddress();
        }

        Enumeration netInterfaces = null;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
//            e.printStackTrace();
        }

        InetAddress inetAddress = null;
        String localIp = DEFAULT_LOCAL_IP; // 内网IP
        for (; netInterfaces.hasMoreElements(); inetAddress = null) {
            NetworkInterface netInterface = (NetworkInterface) netInterfaces.nextElement();
            inetAddress = (InetAddress) netInterface.getInetAddresses().nextElement();
            if (!inetAddress.isLoopbackAddress() && inetAddress.getHostAddress().indexOf(":") == -1) {
                if (!inetAddress.isSiteLocalAddress()) {  // 外网IP
                    break;
                } else {  // 内网IP
                    localIp = inetAddress.getHostAddress();
                }
            }
        }

        return (inetAddress != null ? inetAddress.getHostAddress() : localIp);
    }


    public static String getIp(HttpServletRequest request){
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null) {
            if (ip.indexOf(',') >= 0) {
                // 真实ip取第一个
                ip = ip.split(",")[0];
            }
        }
        return ip;
    }

}
