package com.transmartx.hippo.service.impl;

import com.transmartx.hippo.dto.ChannelDto;
import com.transmartx.hippo.mapper.ChannelMapper;
import com.transmartx.hippo.service.ChannelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: letxig
 */
@Service
@Slf4j
public class ChannelServiceImpl implements ChannelService {

    @Autowired
    private ChannelMapper channelMapper;

    @Override
    public ChannelDto getByChannelId(String channelId) {
        return channelMapper.selectByChannelId(channelId);
    }

}
