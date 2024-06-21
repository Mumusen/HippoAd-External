/*
 * Copyright (c) 2018 Pxene Corporation. All rights reserved.
 * Created on 2018-1-16.
 */

package com.transmartx.hippo.service.counter;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

/**
 * 本地分钟计数器.
 *
 */
@Component
@Slf4j
public class LocalMinuteCounter {

    public static final String COUNTER__FRQ_IP = "FI:"; // IP频次控制
    public static final String COUNTER__RATE = "R:"; // 流控

    /**
     * 最大容量.
     */
    @Value("${dumpling.app.counter.localMinute.maxSize}")
    private int maxSize;

    /**
     * 过期分钟数.
     */
    @Value("${dumpling.app.counter.localMinute.expireMinutes}")
    private int expireMinutes;

    private LoadingCache<String, LongAdder> COUNTER;

    @PostConstruct
    public void init() {
        COUNTER = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireMinutes, TimeUnit.MINUTES)
                .recordStats()
                .build(key -> new LongAdder());

        if (log.isInfoEnabled()) {
            log.info("LocalMinuteCounter [{}][{}] inited!", maxSize, expireMinutes);
        }
    }

    @PreDestroy
    public void close() {
        COUNTER.invalidateAll();
        COUNTER = null;

        if (log.isInfoEnabled()) {
            log.info("LocalMinuteCounter closed!");
        }
    }

    public long getLong(String counter) {
        return COUNTER.get(counter).longValue();
    }

    public void increment(String counter) {
        COUNTER.get(counter).increment();
    }

    public long incrementAndGet(String counter) {
        LongAdder longAdder = COUNTER.get(counter);
        longAdder.increment();
        return longAdder.sum();
    }
}
