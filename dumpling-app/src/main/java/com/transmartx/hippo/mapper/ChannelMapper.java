package com.transmartx.hippo.mapper;

import com.transmartx.hippo.dto.ChannelDto;
import org.apache.ibatis.annotations.Param;

/**
 * @author: letxig
 */
public interface ChannelMapper {

    int insertOne(ChannelDto dto);

    ChannelDto selectByChannelId(@Param("channelId") String channelId);

}
