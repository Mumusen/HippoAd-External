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
public class SubmitRequest {

    private String morecommorder;

    private UserInfo userinfo;

    private ProductInfo productinfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserInfo {
        private String servernum;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductInfo {
        private String productid;
        private String productgroup;
        private String producttype;
        private String ordertype;
    }

}
