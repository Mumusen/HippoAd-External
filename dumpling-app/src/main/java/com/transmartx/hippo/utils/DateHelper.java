/*
 * Copyright (c) 2019 Transsion Corporation. All rights reserved.
 * Created on 2019-12-16.
 */

package com.transmartx.hippo.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类.
 *
 */
public final class DateHelper {

    private DateHelper() {
    }

    public static Date parse(String source, String pattern, Date defaultDate) {
        try {
            if (StringUtils.isNotBlank(source)) {
                return FastDateFormat.getInstance(pattern).parse(source);
            }
        } catch (ParseException e) {
//            e.printStackTrace();
        }
        return defaultDate;
    }

    /**
     * 时间转换, 字符串(pattern) 转换成 Date.
     *
     * @param source
     * @param pattern
     * @return
     */
    public static Date parse(String source, String pattern) {
        return parse(source, pattern, null);
    }

    public static String format(Date date, String pattern, String defaultString) {
        if (date != null) {
            return FastDateFormat.getInstance(pattern).format(date);
        } else {
            return defaultString;
        }
    }

    /**
     * 时间转换, Date 转换成 字符串(pattern).
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String format(Date date, String pattern) {
        return format(date, pattern, null);
    }

    /**
     * 获取当前时间.
     *
     * @return
     */
    public static Date getNow() {
        return new Date();
    }

    public static Date addYears(final Date date, final int amount) {
        return DateUtils.addYears(date, amount);
    }

    public static Date addMonths(final Date date, final int amount) {
        return DateUtils.addMonths(date, amount);
    }

    public static Date addWeeks(final Date date, final int amount) {
        return DateUtils.addWeeks(date, amount);
    }

    public static Date addDays(final Date date, final int amount) {
        return DateUtils.addDays(date, amount);
    }

    public static Date addHours(final Date date, final int amount) {
        return DateUtils.addHours(date, amount);
    }

    public static Date addMinutes(final Date date, final int amount) {
        return DateUtils.addMinutes(date, amount);
    }

    public static Date addSeconds(final Date date, final int amount) {
        return DateUtils.addSeconds(date, amount);
    }

    /**
     * 获取周(每周的第几天).
     * SUNDAY = 1
     * MONDAY = 2
     * TUESDAY = 3
     * WEDNESDAY = 4
     * THURSDAY = 5
     * FRIDAY = 6
     * SATURDAY = 7
     *
     * @param date
     * @return
     */
    public static int getDayOfWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return getDayOfWeek(cal);
    }

    /**
     * 获取周(每周的第几天).
     * SUNDAY = 1
     * MONDAY = 2
     * TUESDAY = 3
     * WEDNESDAY = 4
     * THURSDAY = 5
     * FRIDAY = 6
     * SATURDAY = 7
     *
     * @param calendar
     * @return
     */
    public static int getDayOfWeek(Calendar calendar) {
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 获取周(每月的第几周: 1~6).
     *
     * @param date
     * @return
     */
    public static int getWeekOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return getWeekOfMonth(cal);
    }

    /**
     * 获取周(每月的第几周: 1~6).
     *
     * @param calendar
     * @return
     */
    public static int getWeekOfMonth(Calendar calendar) {
        return calendar.get(Calendar.WEEK_OF_MONTH);
    }

    private static int getQuarter(int month) {
        int quarter = -1;
        if (month >= 0 && month <= 2) {
            quarter = 0;
        } else if (month >= 3 && month <= 5) {
            quarter = 1;
        } else if (month >= 6 && month <= 8) {
            quarter = 2;
        } else if (month >= 9 && month <= 11) {
            quarter = 3;
        }
        return quarter;
    }

    /**
     * 获取季.
     * 第一季 = 0
     * 第二季 = 1
     * 第三季 = 2
     * 第四季 = 3
     *
     * @param date
     * @return
     */
    public static int getQuarter(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return getQuarter(cal);
    }

    /**
     * 获取季.
     * 第一季 = 0
     * 第二季 = 1
     * 第三季 = 2
     * 第四季 = 3
     *
     * @param calendar
     * @return
     */
    public static int getQuarter(Calendar calendar) {
        return getQuarter(calendar.get(Calendar.MONTH));
    }

    public static boolean isSameHour(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                cal1.get(Calendar.HOUR_OF_DAY) == cal2.get(Calendar.HOUR_OF_DAY));
    }

    public static boolean isSameHour(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameHour(cal1, cal2);
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameDay(cal1, cal2);
    }

    public static boolean isSameMonth(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH));
    }

    public static boolean isSameMonth(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameMonth(cal1, cal2);
    }

    public static boolean isSameQuarter(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                getQuarter(cal1.get(Calendar.MONTH)) == getQuarter(cal2.get(Calendar.MONTH)));
    }

    public static boolean isSameQuarter(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameQuarter(cal1, cal2);
    }

    public static boolean isSameYear(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR));
    }

    public static boolean isSameYear(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameYear(cal1, cal2);
    }

    /**
     * 是否有效.
     *
     * @param now       当前时间
     * @param beginTime 生效时间, 空值不检查
     * @param endTime   失效时间, 空值不检查
     * @return
     */
    public static boolean checkDateRange(Date now, Date beginTime, Date endTime) {
        if (beginTime != null && beginTime.after(now)) {
            return false;
        }
        if (endTime != null && endTime.before(now)) {
            return false;
        }
        return true;
    }

    public static int time2seconds(Date time, Date now) {
        int seconds = (int) ((time.getTime() - now.getTime()) / 1000);
        return (seconds > 0 ? seconds : 0);
    }
}
