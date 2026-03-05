-- H2 兼容建表（开发环境）；生产 MySQL 可复用或使用 Flyway
-- 表名、列名小写，与 MyBatis 映射一致

CREATE TABLE IF NOT EXISTS pay_disbursement_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    biz_order_no VARCHAR(64) NOT NULL,
    channel_code VARCHAR(32) NOT NULL,
    channel_serial_no VARCHAR(128),
    amount DECIMAL(18,2) NOT NULL,
    payee_id VARCHAR(64) NOT NULL,
    payee_name VARCHAR(64),
    payee_account VARCHAR(64),
    payee_bank_code VARCHAR(32),
    status VARCHAR(24) NOT NULL,
    fail_reason VARCHAR(256),
    retry_count INT DEFAULT 0
);
CREATE UNIQUE INDEX IF NOT EXISTS idx_disbur_biz_no ON pay_disbursement_order(biz_order_no);

CREATE TABLE IF NOT EXISTS pay_repayment_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    biz_repay_no VARCHAR(64) NOT NULL,
    loan_no VARCHAR(64) NOT NULL,
    channel_code VARCHAR(32) NOT NULL,
    channel_serial_no VARCHAR(128),
    amount DECIMAL(18,2) NOT NULL,
    payer_id VARCHAR(64) NOT NULL,
    payer_account VARCHAR(64),
    status VARCHAR(24) NOT NULL,
    fail_reason VARCHAR(256),
    retry_count INT DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_repay_biz_no ON pay_repayment_order(biz_repay_no);
CREATE INDEX IF NOT EXISTS idx_repay_loan_no ON pay_repayment_order(loan_no);

CREATE TABLE IF NOT EXISTS pay_reconciliation_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    recon_date DATE NOT NULL,
    channel_code VARCHAR(32) NOT NULL,
    payment_type VARCHAR(24) NOT NULL,
    channel_serial_no VARCHAR(128) NOT NULL,
    amount DECIMAL(18,2) NOT NULL,
    biz_order_no VARCHAR(64),
    recon_result VARCHAR(16),
    remark VARCHAR(256)
);
CREATE INDEX IF NOT EXISTS idx_recon_date_channel ON pay_reconciliation_record(recon_date, channel_code);

CREATE TABLE IF NOT EXISTS pay_channel_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    channel_code VARCHAR(32) NOT NULL,
    channel_name VARCHAR(64),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    disbursement_enabled BOOLEAN DEFAULT TRUE,
    repayment_enabled BOOLEAN DEFAULT TRUE,
    single_limit_cents BIGINT,
    extra_config CLOB
);
