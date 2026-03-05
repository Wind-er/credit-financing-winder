package com.winder.pay.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RepaymentRequest {

    @NotBlank(message = "业务还款单号不能为空")
    private String bizRepayNo;

    @NotBlank(message = "借据号不能为空")
    private String loanNo;

    @NotNull
    @DecimalMin(value = "0.01", message = "金额必须大于 0")
    private BigDecimal amount;

    @NotBlank(message = "付款人ID不能为空")
    private String payerId;

    private String payerAccount;

    /** 不传则用默认渠道 */
    private String channelCode;
}
