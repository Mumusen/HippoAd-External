package com.transmartx.hippo.logger;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ApiInvokeLogger {

    public void debug(String tag, Object info) {
        if (log.isDebugEnabled()) {
            log.debug("[{}] - [{}]", tag, info);
        }
    }

    public void debug(String tag, Object info1, Object info2) {
        if (log.isDebugEnabled()) {
            log.debug("[{}] - [{}] [{}]", tag, info1, info2);
        }
    }

    public void debug(String tag, Object info1, Object info2, Object info3) {
        if (log.isDebugEnabled()) {
            log.debug("[{}] - [{}] [{}] [{}]", tag, info1, info2, info3);
        }
    }

    public void debug(String tag, Object info1, Object info2, Object info3, Object info4) {
        if (log.isDebugEnabled()) {
            log.debug("[{}] - [{}] [{}] [{}] [{}]", tag, info1, info2, info3, info4);
        }
    }

    public void debug(String tag, Object info1, Object info2, Object info3, Object info4, Object info5) {
        if (log.isDebugEnabled()) {
            log.debug("[{}] - [{}] [{}] [{}] [{}] [{}]", tag, info1, info2, info3, info4, info5);
        }
    }

    public void info(String tag, Object info) {
        if (log.isInfoEnabled()) {
            log.info("[{}] - [{}]", tag, info);
        }
    }

    public void info(String tag, Object info1, Object info2) {
        if (log.isInfoEnabled()) {
            log.info("[{}] - [{}] [{}]", tag, info1, info2);
        }
    }

    public void info(String tag, Object info1, Object info2, Object info3) {
        if (log.isInfoEnabled()) {
            log.info("[{}] - [{}] [{}] [{}]", tag, info1, info2, info3);
        }
    }

    public void info(String tag, Object info1, Object info2, Object info3, Object info4) {
        if (log.isInfoEnabled()) {
            log.info("[{}] - [{}] [{}] [{}] [{}]", tag, info1, info2, info3, info4);
        }
    }

    public void info(String tag, Object info1, Object info2, Object info3, Object info4, Object info5) {
        if (log.isInfoEnabled()) {
            log.info("[{}] - [{}] [{}] [{}] [{}] [{}]", tag, info1, info2, info3, info4, info5);
        }
    }

}
