package com.winder.pay.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ReconciliationRequest {

    @NotNull(message = "对账日期不能为空")
    private LocalDate reconDate;

    @NotBlank(message = "渠道编码不能为空")
    private String channelCode;

    @NotBlank(message = "支付类型不能为空")
    private String paymentType; // DISBURSEMENT / REPAYMENT

    @NotBlank(message = "渠道流水号不能为空")
    private String channelSerialNo;

    @NotNull
    private BigDecimal amount;

    private String bizOrderNo;
    private String reconResult; // MATCH / SHORT / LONG
    private String remark;
}
