/*
 * Copyright (c) 2018 Pxene Corporation. All rights reserved.
 * Created on 2018-2-24.
 */

package com.transmartx.hippo.service.counter;

import com.transmartx.hippo.constant.Constants;
import com.transmartx.hippo.constant.PeriodEnum;
import com.transmartx.hippo.utils.DateHelper;
import java.util.Date;

/**
 * 计数器工具类.
 *
 */
public class CounterHelper {

    private static final String PERIOD_FORMAT__MINUTE = "yyyyMMddHHmm";
    private static final String PERIOD_FORMAT__HOUR = "yyyyMMddHH";
    private static final String PERIOD_FORMAT__DAY = "yyyyMMdd";
    private static final String PERIOD_FORMAT__MONTH = "yyyyMM";
    private static final String PERIOD_FORMAT__YEAR = "yyyy";

    private static final String W = "W";
    private static final String Q = "Q";

    private CounterHelper() {
    }

    public static String genKey(Date now, String key, int period, String counter) {
        StringBuilder sb = new StringBuilder();
        if (PeriodEnum.UNKNOWN.id == period) {
            sb.append(counter).append(period).append(Constants.COLON).append(key);
        } else if (PeriodEnum.MINUTE.id == period) {
            sb.append(counter).append(period).append(Constants.COLON).append(DateHelper.format(now, PERIOD_FORMAT__MINUTE)).append(Constants.COLON).append(key);
        } else if (PeriodEnum.HOUR.id == period) {
            sb.append(counter).append(period).append(Constants.COLON).append(DateHelper.format(now, PERIOD_FORMAT__HOUR)).append(Constants.COLON).append(key);
        } else if (PeriodEnum.DAY.id == period) {
            sb.append(counter).append(period).append(Constants.COLON).append(DateHelper.format(now, PERIOD_FORMAT__DAY)).append(Constants.COLON).append(key);
        } else if (PeriodEnum.WEEK.id == period) {
            sb.append(counter).append(period).append(Constants.COLON).append(DateHelper.format(now, PERIOD_FORMAT__MONTH)).append(W).append(DateHelper.getWeekOfMonth(now)).append(Constants.COLON).append(key);
        } else if (PeriodEnum.MONTH.id == period) {
            sb.append(counter).append(period).append(Constants.COLON).append(DateHelper.format(now, PERIOD_FORMAT__MONTH)).append(Constants.COLON).append(key);
        } else if (PeriodEnum.QUARTER.id == period) {
            sb.append(counter).append(period).append(Constants.COLON).append(DateHelper.format(now, PERIOD_FORMAT__YEAR)).append(Q).append(DateHelper.getQuarter(now)).append(Constants.COLON).append(key);
        } else if (PeriodEnum.YEAR.id == period) {
            sb.append(counter).append(period).append(Constants.COLON).append(DateHelper.format(now, PERIOD_FORMAT__YEAR)).append(Constants.COLON).append(key);
        }
        return sb.toString();
    }

}
