package com.transmartx.hippo.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author: letxig
 */
@Data
public class ProductDto {

    /**
     * id
     */
    private int id;

    /**
     * 商品CODE
     */
    private String productCode;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 上游来源
     */
    private String upstream;

    /**
     * 商品状态
     */
    private int productStatus;

    /**
     * 是否有效
     */
    private int isValid;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}
