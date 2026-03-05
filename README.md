# credit-financing-winder

互联网金融信贷系统 — 底层框架与支付/资金服务（Java + Spring Boot）

## 技术栈

- Java 17
- Spring Boot 3.2
- MyBatis（替代 JPA，Mapper + XML）
- H2（开发，schema.sql 建表）/ MySQL（生产可选）
- Lombok

## 模块结构

- **common**：统一响应 `Result`、全局异常处理、基础实体、Web 配置、MyBatis `InstantTypeHandler`
- **pay**：支付/资金服务（MyBatis Mapper + XML）
  - 放款（Disbursement）：创建放款单、调用渠道、状态更新
  - 还款（Repayment）：主动还款/代扣
  - 对账（Reconciliation）：对账记录录入与查询
  - 支付渠道适配器接口 + 模拟渠道实现（Mock）

## 快速启动

```bash
# 编译
mvn clean compile

# 运行（默认 dev 配置，H2 内存库）
mvn spring-boot:run
```

服务根路径：`http://localhost:8080/api`

### 支付/资金 API 示例

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/pay/disbursement` | 发起放款（body: bizOrderNo, amount, payeeId, payeeName 等） |
| GET  | `/api/pay/disbursement/{bizOrderNo}` | 查询放款单 |
| POST | `/api/pay/repayment` | 发起还款（body: bizRepayNo, loanNo, amount, payerId 等） |
| GET  | `/api/pay/repayment/{bizRepayNo}` | 查询还款单 |
| GET  | `/api/pay/repayment/loan/{loanNo}` | 按借据号查还款列表 |
| POST | `/api/pay/reconciliation` | 提交对账记录 |
| GET  | `/api/pay/reconciliation?reconDate=2025-03-05` | 按日期查对账记录 |

### H2 控制台（开发）

`http://localhost:8080/h2-console`，JDBC URL：`jdbc:h2:mem:credit`

## 架构文档

- [ArchTech.md](./ArchTech.md) — 功能需求与核心功能
- [ArchTechDiagram.md](./ArchTechDiagram.md) — 系统架构图
- [DetailedTechStackDiagram.md](./DetailedTechStackDiagram.md) — 技术架构图
