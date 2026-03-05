package com.winder.pay.adapter;

import com.winder.pay.domain.enums.PaymentChannelEnum;

import java.math.BigDecimal;

/**
 * 支付渠道适配器接口：放款、还款（代扣）由各渠道实现
 */
public interface PaymentChannelAdapter {

    PaymentChannelEnum getChannelCode();

    /**
     * 放款
     *
     * @param bizOrderNo 业务单号
     * @param amount     金额
     * @param payeeId    收款人 ID
     * @param payeeName  收款人姓名
     * @param payeeAccount 收款账户
     * @param payeeBankCode 银行编码（可选）
     * @return 渠道流水号，失败抛异常或由结果表示
     */
    DisbursementResult disburse(String bizOrderNo, BigDecimal amount,
                               String payeeId, String payeeName, String payeeAccount, String payeeBankCode);

    /**
     * 还款/代扣
     *
     * @param bizRepayNo 业务还款单号
     * @param loanNo     借据号
     * @param amount     金额
     * @param payerId    付款人 ID
     * @param payerAccount 付款账户（卡号等）
     * @return 渠道流水号
     */
    RepaymentResult repay(String bizRepayNo, String loanNo, BigDecimal amount,
                          String payerId, String payerAccount);

    /**
     * 放款结果
     */
    record DisbursementResult(boolean success, String channelSerialNo, String failReason) {
        public static DisbursementResult ok(String serialNo) {
            return new DisbursementResult(true, serialNo, null);
        }
        public static DisbursementResult fail(String reason) {
            return new DisbursementResult(false, null, reason);
        }
    }

    /**
     * 还款结果
     */
    record RepaymentResult(boolean success, String channelSerialNo, String failReason) {
        public static RepaymentResult ok(String serialNo) {
            return new RepaymentResult(true, serialNo, null);
        }
        public static RepaymentResult fail(String reason) {
            return new RepaymentResult(false, null, reason);
        }
    }
}
