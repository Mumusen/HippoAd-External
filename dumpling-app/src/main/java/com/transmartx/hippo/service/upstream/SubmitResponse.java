package com.transmartx.hippo.service.upstream;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: letxig
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitResponse {

    private OrderInfo orderinfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderInfo {
        private String orderseq;
    }

}
