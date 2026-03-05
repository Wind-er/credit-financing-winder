package com.winder.pay.mapper;

import com.winder.pay.domain.entity.RepaymentOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 还款订单 Mapper
 */
@Mapper
public interface RepaymentOrderMapper {

    int insert(RepaymentOrder order);

    int updateById(RepaymentOrder order);

    RepaymentOrder selectByBizRepayNo(@Param("bizRepayNo") String bizRepayNo);

    List<RepaymentOrder> selectByLoanNoOrderByCreatedAtDesc(@Param("loanNo") String loanNo);
}
