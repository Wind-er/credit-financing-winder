# 互联网金融信贷系统 — 系统架构图

> 基于 [ArchTech.md](./ArchTech.md) 功能需求，从**系统/业务架构**视角描述各域边界、分层与协作关系。  
> 图表使用 Mermaid，可在 GitHub / VS Code / 支持 Mermaid 的 Markdown 预览中直接渲染。

---

## 1. 系统上下文（C4 Context）

系统与外部用户、合作方、监管的交互关系。

```mermaid
C4Context
    title 系统上下文 — 信贷系统与外部关系

    Person(customer, "借款用户", "APP/ H5 申请、还款、查看借据")
    Person(ops, "运营/审批人员", "工作台审批、报表、配置")
    Person(collector, "催收人员", "分案、外呼、减免登记")

    System_Boundary(credit_system, "互联网金融信贷系统") {
        System(credit, "信贷核心", "进件、授信、放款、还款、催收全流程")
    }

    System_Ext(payment, "支付/银行通道", "放款出金、还款代扣、鉴权")
    System_Ext(credit_data, "征信/数据源", "人行征信、百行、多头、反欺诈")
    System_Ext(contract, "电子签章/存证", "合同生成、签署、存证")
    System_Ext(sms_push, "短信/推送", "验证码、还款提醒、营销触达")
    System_Ext(regulator, "监管报送", "合规报表、披露")

    Rel(customer, credit, "申请、支用、还款、查询")
    Rel(ops, credit, "审批、配置、报表")
    Rel(collector, credit, "催收任务、减免、核销")
    Rel(credit, payment, "放款/还款指令、对账")
    Rel(credit, credit_data, "征信查询、名单校验")
    Rel(credit, contract, "合同生成与签署")
    Rel(credit, sms_push, "发送短信/推送")
    Rel(credit, regulator, "报送、披露")
```

---

## 2. 业务分层架构（Layered View）

按「接入层 → 应用层 → 领域层 → 基础设施层」划分，对应 ArchTech 中的功能域。

```mermaid
flowchart TB
    subgraph 接入层["接入层"]
        APP["APP / H5"]
        PC["运营工作台 PC"]
        API["开放 API / 渠道"]
    end

    subgraph 应用层["应用层（用例/流程）"]
        A1["进件与授信应用"]
        A2["支用与放款应用"]
        A3["贷中与还款应用"]
        A4["催收与清收应用"]
        A5["用户与客户应用"]
        A6["运营与报表应用"]
    end

    subgraph 领域层["领域层（核心域）"]
        D1["用户与客户域"]
        D2["产品与定价域"]
        D3["授信与风控域"]
        D4["借据与还款域"]
        D5["支付与资金域"]
        D6["催收与清收域"]
    end

    subgraph 基础设施层["基础设施层"]
        I1["支付网关适配"]
        I2["征信/数据源适配"]
        I3["合同/签章适配"]
        I4["消息/通知"]
        I5["存储/缓存"]
    end

    APP --> A1 & A3 & A5
    PC --> A1 & A4 & A6
    API --> A1 & A2 & A3

    A1 --> D1 & D2 & D3
    A2 --> D2 & D3 & D4 & D5
    A3 --> D4 & D5
    A4 --> D4 & D6
    A5 --> D1
    A6 --> D1 & D2 & D3 & D4 & D6

    D3 --> I2
    D4 & D5 --> I1 & I4
    D5 --> I3
    D1 & D2 & D3 & D4 & D6 --> I5
```

---

## 3. 核心业务流程（端到端）

从「申请 → 授信 → 支用 → 放款 → 还款 → 催收」的主流程与涉及系统模块。

```mermaid
flowchart LR
    subgraph 申请与授信
        S1[进件] --> S2[反欺诈]
        S2 --> S3[信用评估]
        S3 --> S4[授信审批]
        S4 --> S5[额度生效]
    end

    subgraph 支用与放款
        S5 --> T1[支用申请]
        T1 --> T2[放款审核]
        T2 --> T3[合同签署]
        T3 --> T4[资金路由]
        T4 --> T5[放款执行]
    end

    subgraph 贷后
        T5 --> R1[借据/还款计划]
        R1 --> R2[主动还款/代扣]
        R2 --> R3{是否逾期}
        R3 -->|否| R4[结清/正常]
        R3 -->|是| R5[催收分案]
        R5 --> R6[催收执行]
        R6 --> R7[减免/核销]
    end

    style S1 fill:#e1f5fe
    style S4 fill:#e1f5fe
    style T4 fill:#e1f5fe
    style R2 fill:#e1f5fe
    style R5 fill:#e1f5fe
```

---

## 4. 功能域与模块划分（模块图）

与 ArchTech 十大功能域一一对应，标出域间主要依赖。

```mermaid
flowchart TB
    subgraph 用户侧
        U["用户与客户管理\n注册/登录、实名、档案、授权"]
    end

    subgraph 业务核心
        P["产品与定价\n产品配置、费率引擎、额度策略"]
        I["进件与授信\n进件、反欺诈、评分、审批、额度"]
        L["支用与放款\n支用、合同、资金路由、放款执行"]
        R["贷中与还款\n借据、还款计划、主动/代扣、逾期"]
        C["催收与清收\n分案、执行、减免、委外、核销"]
    end

    subgraph 资金与风控
        PAY["支付与资金\n渠道、放款/还款通道、对账"]
        RISK["风控与合规\n规则引擎、征信、名单、合规审计"]
    end

    subgraph 支撑
        OPS["运营与支撑\n工作台、报表、配置、客服"]
        INFRA["系统与基础设施\n身份权限、高可用、安全、监控"]
    end

    U --> I
    P --> I & L
    I --> L
    L --> R
    R --> C
    PAY --> L & R
    RISK --> I & L
    OPS --> I & L & R & C
    INFRA --> U & P & I & L & R & C & PAY & RISK & OPS
```

---

## 5. 数据流概览（简化）

核心业务对象在域间的产生与流转方向。

```mermaid
flowchart LR
    subgraph 用户与客户域
        A[客户/账户]
    end

    subgraph 授信域
        B[授信额度]
    end

    subgraph 交易域
        C[借据]
        D[还款计划]
    end

    subgraph 资金域
        E[放款记录]
        F[还款记录]
    end

    A -->|进件| B
    B -->|支用| C
    C --> D
    C -->|放款| E
    D -->|还款| F
    F --> C
```

---

## 6. 部署与边界（逻辑部署）

从「谁访问谁」看各部分的逻辑部署边界（不涉及具体技术栈，技术栈见 DetailedTechStackDiagram.md）。

```mermaid
flowchart TB
    subgraph 互联网区["互联网区"]
        Client["APP / H5 / 第三方"]
        GW["接入网关\n鉴权、限流、路由"]
    end

    subgraph DMZ["DMZ / 前置"]
        GW
    end

    subgraph 应用区["应用区"]
        BFF["BFF / 聚合服务"]
        Core["信贷核心服务\n进件/授信/支用/还款/催收"]
        OPS_SVC["运营服务\n工作台、报表、配置"]
    end

    subgraph 数据与外部["数据与外部"]
        DB[(业务库)]
        MQ[消息队列]
        Ext["征信/支付/合同/短信"]
    end

    Client --> GW
    GW --> BFF
    BFF --> Core & OPS_SVC
    Core --> DB & MQ & Ext
    OPS_SVC --> DB
```

---

## 图例与说明

| 图 | 用途 |
|----|------|
| 1 系统上下文 | 系统与用户、支付、征信、监管等外部系统关系 |
| 2 业务分层 | 接入/应用/领域/基础设施分层，对应功能域 |
| 3 核心业务流程 | 申请→授信→支用→放款→还款→催收 主流程 |
| 4 功能域与模块 | 十大功能域及域间依赖，与 ArchTech 一致 |
| 5 数据流概览 | 客户→授信→借据→还款计划/放款/还款 数据流向 |
| 6 部署与边界 | 互联网区 / DMZ / 应用区 / 数据与外部 逻辑边界 |

如需按**技术栈与组件**展开（语言、框架、中间件、部署方式），请参见 [DetailedTechStackDiagram.md](./DetailedTechStackDiagram.md)。
