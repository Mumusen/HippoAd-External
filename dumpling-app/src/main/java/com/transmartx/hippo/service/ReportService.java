package com.transmartx.hippo.service;

import java.util.List;

/**
 * @author: letxig
 */
public interface ReportService {

    void getAccessCode(String email);

    void downloadOrderReport(String agentId, String from, String to);

}
