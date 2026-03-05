package com.winder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 互联网金融信贷系统 - 启动类
 * 当前实现：底层框架 + 支付/资金服务模块
 */
@SpringBootApplication
public class CreditFinancingApplication {

    public static void main(String[] args) {
        SpringApplication.run(CreditFinancingApplication.class, args);
    }
}
