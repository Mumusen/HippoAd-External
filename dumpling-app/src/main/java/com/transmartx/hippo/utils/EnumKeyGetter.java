/*
 * Copyright (c) 2019 Transsion Corporation. All rights reserved.
 * Created on 2019-12-16.
 */

package com.transmartx.hippo.utils;

/**
 * 枚举Key获取接口.
 *
 */
public interface EnumKeyGetter<T extends Enum<T>, K> {

    /**
     * 获取Key.
     *
     * @param enumValue
     * @return
     */
    K getKey(T enumValue);
}
