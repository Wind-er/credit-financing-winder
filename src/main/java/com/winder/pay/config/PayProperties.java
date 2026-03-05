package com.winder.pay.config;

import com.winder.pay.domain.enums.PaymentChannelEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 支付/资金服务配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "winder.pay")
public class PayProperties {

    /** 默认使用的渠道 */
    private PaymentChannelEnum defaultChannel = PaymentChannelEnum.MOCK;

    private Retry retry = new Retry();

    @Data
    public static class Retry {
        private int maxAttempts = 3;
        private long delayMs = 1000;
    }
}
