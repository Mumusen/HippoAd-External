package com.transmartx.hippo.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author: letxig
 */
@Data
public class ChannelDto {

    /**
     * id
     */
    private int id;

    /**
     * 渠道ID
     */
    private String channelId;

    /**
     * 域名
     */
    private String domains;

    /**
     * 联系人
     */
    private String contact;

    /**
     * 是否有效
     */
    private int isValid;

    /**
     * 渠道秘钥
     */
    private String channelSecretKey;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}
