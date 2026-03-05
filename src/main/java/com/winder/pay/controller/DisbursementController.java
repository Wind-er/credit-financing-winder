package com.winder.pay.controller;

import com.winder.common.result.Result;
import com.winder.pay.domain.entity.DisbursementOrder;
import com.winder.pay.dto.DisbursementRequest;
import com.winder.pay.service.DisbursementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 放款 API：创建并执行放款
 */
@RestController
@RequestMapping("/pay/disbursement")
@RequiredArgsConstructor
public class DisbursementController {

    private final DisbursementService disbursementService;

    @PostMapping
    public Result<DisbursementOrder> create(@RequestBody @Valid DisbursementRequest request) {
        DisbursementOrder order = disbursementService.createAndExecute(request);
        return Result.ok(order);
    }

    @GetMapping("/{bizOrderNo}")
    public Result<DisbursementOrder> get(@PathVariable String bizOrderNo) {
        return Result.ok(disbursementService.getByBizOrderNo(bizOrderNo));
    }
}
