package com.transmartx.hippo.service.upstream;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author: letxig
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendSmsCodeRequest {

    private String tel;

    private String orderseq;

    private List<Comm> commlist;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Comm {
        private String commcode;
    }

}
