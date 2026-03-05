package com.winder.pay.adapter;

import com.winder.pay.domain.enums.PaymentChannelEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 模拟支付渠道：开发/联调用，直接返回成功与模拟流水号
 */
@Slf4j
@Component
public class MockPaymentChannelAdapter implements PaymentChannelAdapter {

    @Override
    public PaymentChannelEnum getChannelCode() {
        return PaymentChannelEnum.MOCK;
    }

    @Override
    public DisbursementResult disburse(String bizOrderNo, BigDecimal amount,
                                       String payeeId, String payeeName, String payeeAccount, String payeeBankCode) {
        log.info("Mock disburse: bizOrderNo={}, amount={}, payeeId={}", bizOrderNo, amount, payeeId);
        String serial = "MOCK-D-" + UUID.randomUUID().toString().replace("-", "").substring(0, 24);
        return DisbursementResult.ok(serial);
    }

    @Override
    public RepaymentResult repay(String bizRepayNo, String loanNo, BigDecimal amount,
                                 String payerId, String payerAccount) {
        log.info("Mock repay: bizRepayNo={}, loanNo={}, amount={}, payerId={}", bizRepayNo, loanNo, amount, payerId);
        String serial = "MOCK-R-" + UUID.randomUUID().toString().replace("-", "").substring(0, 24);
        return RepaymentResult.ok(serial);
    }
}
