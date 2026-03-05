package com.winder.pay.controller;

import com.winder.common.result.Result;
import com.winder.pay.domain.entity.RepaymentOrder;
import com.winder.pay.dto.RepaymentRequest;
import com.winder.pay.service.RepaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 还款 API：主动还款/代扣
 */
@RestController
@RequestMapping("/pay/repayment")
@RequiredArgsConstructor
public class RepaymentController {

    private final RepaymentService repaymentService;

    @PostMapping
    public Result<RepaymentOrder> create(@RequestBody @Valid RepaymentRequest request) {
        RepaymentOrder order = repaymentService.createAndExecute(request);
        return Result.ok(order);
    }

    @GetMapping("/{bizRepayNo}")
    public Result<RepaymentOrder> get(@PathVariable String bizRepayNo) {
        return Result.ok(repaymentService.getByBizRepayNo(bizRepayNo));
    }

    @GetMapping("/loan/{loanNo}")
    public Result<List<RepaymentOrder>> listByLoan(@PathVariable String loanNo) {
        return Result.ok(repaymentService.listByLoanNo(loanNo));
    }
}
