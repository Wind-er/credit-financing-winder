package com.winder.pay.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DisbursementRequest {

    @NotBlank(message = "业务单号不能为空")
    private String bizOrderNo;

    @NotNull
    @DecimalMin(value = "0.01", message = "金额必须大于 0")
    private BigDecimal amount;

    @NotBlank(message = "收款人ID不能为空")
    private String payeeId;

    private String payeeName;
    private String payeeAccount;
    private String payeeBankCode;

    /** 不传则用默认渠道 */
    private String channelCode;
}
