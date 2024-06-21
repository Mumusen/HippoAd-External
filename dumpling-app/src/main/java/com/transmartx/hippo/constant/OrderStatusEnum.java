package com.transmartx.hippo.constant;

import com.transmartx.hippo.utils.EnumFindHelper;
import com.transmartx.hippo.utils.EnumKeyGetter;

/**
 * @author: letxig
 */
public enum OrderStatusEnum {

    UNKNOWN(-1, "未知"),
    INIT(10, "进行中"),
    SUBMIT(20, "提交"),
    FINISH(30, "完成"),
    FAIL(40, "失败");

    public int value;
    public String desc;

    OrderStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    private static final EnumFindHelper<OrderStatusEnum, Integer> valueHelper = new EnumFindHelper<>(OrderStatusEnum.class, new EnumKeyGetter<OrderStatusEnum, Integer>() {

        @Override
        public Integer getKey(OrderStatusEnum enumValue) {
            return enumValue.value;
        }
    });

    private static final EnumFindHelper<OrderStatusEnum, String> descHelper = new EnumFindHelper<>(OrderStatusEnum.class, new EnumKeyGetter<OrderStatusEnum, String>() {

        @Override
        public String getKey(OrderStatusEnum enumValue) {
            return enumValue.desc;
        }
    });

    public static OrderStatusEnum findByValue(int value) {
        return valueHelper.findByKey(value, OrderStatusEnum.UNKNOWN);
    }

    public static OrderStatusEnum findByDesc(String desc) {
        return descHelper.findByKey(desc, OrderStatusEnum.UNKNOWN);
    }

}
