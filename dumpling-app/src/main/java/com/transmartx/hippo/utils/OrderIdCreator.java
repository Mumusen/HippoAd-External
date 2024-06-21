/*
 * Copyright (c) 2019 Transsion Corporation. All rights reserved.
 * Created on 2019-12-16.
 */

package com.transmartx.hippo.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 订单号生成器.
 * [PREFIX:9][TAG:2][SITE_CODE:3][yyMMddHHmmss][IDX:6]
 *
 * @author James Shen
 */
public class OrderIdCreator {

    private static final int PREFIX_LEN = 9;
    private static final int TAG_LEN = 2;
    private static final int SITE_CODE_LEN = 3;
    private static final int IDX_LEN = 6;
    private static final String ZERO = "0";

    /**
     * 前缀, 最长9位(取右侧).
     */
    private String prefix;

    /**
     * 特征码, 最长2位(取右侧).
     */
    private String tag;

    /**
     * 位置代码, 最长3位(取右侧).
     */
    private String siteCode;

    /**
     * 本机IP自动生成的位置代码, 最长3位(取右侧).
     */
    private String siteCodeFromIp;

    /**
     * 是否用本机IP生成的位置代码, 默认 true.
     */
    private boolean useSiteCodeFromIp;

    private String savedTime; // 上次请求的时间
    private int savedIndex = 0; // 每一秒内的重复请求数

    private Lock lock = new ReentrantLock(false);

    public OrderIdCreator() {
        this.prefix = StringUtils.leftPad("", PREFIX_LEN, ZERO);
        this.tag = StringUtils.leftPad("", TAG_LEN, ZERO);
        this.siteCode = StringUtils.leftPad("", SITE_CODE_LEN, ZERO);
        this.siteCodeFromIp = StringUtils.leftPad(StringUtils.right(createSiteCodeFromIp(""), SITE_CODE_LEN), SITE_CODE_LEN, ZERO);
        this.useSiteCodeFromIp = true;
    }

    /**
     * 生成订单号.
     *
     * @param prefix 前缀, 最长9位(取右侧)
     * @param tag    特征码, 最长2位(取右侧)
     * @return
     */
    public String createOrderId(String prefix, String tag) {
        StringBuffer sb = new StringBuffer();

        lock.lock();
        try {
            String now = getNow();
            if (now.equals(savedTime)) {
                savedIndex++;
            } else {
                savedTime = now;
                savedIndex = 1;
            }
            if (prefix != null) {
                sb.append(StringUtils.leftPad(StringUtils.right(prefix, PREFIX_LEN), PREFIX_LEN, ZERO));
            } else {
                sb.append(this.prefix);
            }
            if (tag != null) {
                sb.append(StringUtils.leftPad(StringUtils.right(tag, TAG_LEN), TAG_LEN, ZERO));
            } else {
                sb.append(this.tag);
            }
            if (useSiteCodeFromIp) {
                sb.append(siteCodeFromIp);
            } else {
                sb.append(this.siteCode);
            }
            sb.append(now);
            sb.append(StringUtils.leftPad(String.valueOf(savedIndex), IDX_LEN, ZERO));
        } finally {
            lock.unlock();
        }

        return sb.toString();
    }

    private static final String DOT = ".";
    private static final int IPS_LEN = 4;
    private static final int IPP_LEN = 3;

    private static String createSiteCodeFromIp(String siteCode) {
        String localHostIp = IPHelper.getLocalHostIp();
        if (!IPHelper.DEFAULT_LOCAL_IP.equals(localHostIp)) {
            String[] codes = StringUtils.split(localHostIp, DOT);
            if (codes.length == IPS_LEN) {
                StringBuilder sb = new StringBuilder();
                for (String code : codes) {
                    sb.append(StringUtils.leftPad(code, IPP_LEN, ZERO));
                }
                return sb.toString();
            }
        }
        return siteCode;
    }

    private static final String NOW_PATTERN = "yyMMddHHmmss";

    private static String getNow() {
        return DateHelper.format(new Date(), NOW_PATTERN);
    }

}
