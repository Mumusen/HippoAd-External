/*
 * Copyright (c) 2019 Transsion Corporation. All rights reserved.
 * Created on 2019-12-16.
 */

package com.transmartx.hippo.utils;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * 枚举查找工具类.
 *
 */
public class EnumFindHelper<T extends Enum<T>, K> {

    private Map<K, T> map = new HashMap<>();

    public EnumFindHelper(Class<T> clazz, EnumKeyGetter<T, K> keyGetter) {
        try {
            for (T enumValue : EnumSet.allOf(clazz)) {
                map.put(keyGetter.getKey(enumValue), enumValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查找.
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public T findByKey(K key, T defaultValue) {
        T value = map.get(key);
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }
}
