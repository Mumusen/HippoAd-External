/*
 * Copyright (c) 2019 Transsion Corporation. All rights reserved.
 * Created on 2019-12-17.
 */

package com.transmartx.hippo.constant;

import com.transmartx.hippo.utils.EnumFindHelper;
import com.transmartx.hippo.utils.EnumKeyGetter;


/**
 * 周期枚举.
 *
 */
public enum PeriodEnum {

    UNKNOWN(0, "UNKNOWN"), // 未知
    MINUTE(1, "MINUTE"), // 分钟
    HOUR(2, "HOUR"), // 小时
    DAY(3, "DAY"), // 天
    WEEK(4, "WEEK"), // 周
    MONTH(5, "MONTH"), // 月
    QUARTER(6, "QUARTER"), // 季
    YEAR(7, "YEAR"), // 年

    //
    ;

    public int id;
    public String name;

    PeriodEnum(int id, String name) {
        this.id = id;
        this.name = name;
    }

    private static final EnumFindHelper<PeriodEnum, Integer> idHelper = new EnumFindHelper<>(PeriodEnum.class, new EnumKeyGetter<PeriodEnum, Integer>() {

        @Override
        public Integer getKey(PeriodEnum enumValue) {
            return enumValue.id;
        }
    });

    private static final EnumFindHelper<PeriodEnum, String> nameHelper = new EnumFindHelper<>(PeriodEnum.class, new EnumKeyGetter<PeriodEnum, String>() {

        @Override
        public String getKey(PeriodEnum enumValue) {
            return enumValue.name;
        }
    });

    public static PeriodEnum findById(int id) {
        return idHelper.findByKey(id, PeriodEnum.UNKNOWN);
    }

    public static PeriodEnum findByName(String name) {
        return nameHelper.findByKey(name, PeriodEnum.UNKNOWN);
    }
}
