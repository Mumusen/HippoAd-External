package com.transmartx.hippo.interceptor;

import com.transmartx.hippo.logger.ApiInvokeLogger;
import com.transmartx.hippo.utils.IPHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
public class UrlFilterInterceptor implements Filter {

    @Autowired
    private ApiInvokeLogger apiInvokeLogger;

    private static Set<String> filtered = new HashSet<String>() {
        private static final long serialVersionUID = -5700492047295615864L;
        {
            add("/api/user");
        }
    };

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        invoke((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private String getUA(HttpServletRequest req) {
        return req == null ? "" : req.getHeader("user-agent");
    }

    private String getReferer(HttpServletRequest req) {
        return req == null ? "" : req.getHeader("referer");
    }

    private void invoke(HttpServletRequest req, HttpServletResponse rsp, FilterChain chain) throws IOException, ServletException {
        String uri = req.getRequestURI();
        logInvoke(uri, req);
        if ("/".equals(uri)) {  //  根路径访问直接return
            return;
        }

        boolean matched = filtered.stream().anyMatch(u -> new AntPathMatcher().match(u, uri));
        if (matched) {
            chain.doFilter(req, rsp);
        }

        chain.doFilter(req, rsp);
    }

    private void logInvoke(String uri, HttpServletRequest req) {
        String clientIp = IPHelper.getIp(req);
        String ua = getUA(req);
        String referer = getReferer(req);
        apiInvokeLogger.info("api-invoke", clientIp, uri, ua, referer);
    }

    @Override
    public void destroy() {

    }
}
