# 互联网金融信贷系统 — 技术架构图

> 基于 [ArchTech.md](./ArchTech.md) 功能需求与 [ArchTechDiagram.md](./ArchTechDiagram.md) 系统架构，从**技术栈与部署**视角描述组件、中间件与基础设施。  
> 图表使用 Mermaid，可在 GitHub / VS Code / 支持 Mermaid 的 Markdown 预览中直接渲染。

---

## 1. 技术栈总览（分层）

从「前端 → 网关 → 服务 → 数据/中间件 → 基础设施」的分层技术选型示意。

```mermaid
flowchart TB
    subgraph 前端["前端层"]
        Web["Web 管理台\nReact / Vue + TypeScript"]
        H5["H5 / 小程序\nVue / Taro"]
        APP_Native["APP\nReact Native / Flutter / 原生"]
    end

    subgraph 接入层["接入层"]
        API_GW["API 网关\nKong / APISIX / Spring Cloud Gateway"]
        LB["负载均衡\nNginx / SLB / ALB"]
    end

    subgraph 服务层["服务层"]
        BFF["BFF\nNode / Java"]
        Core_SVC["信贷核心服务\nJava / Go\nSpring Boot / Go Frame"]
        Risk_SVC["风控服务\n规则引擎、评分"]
        Pay_SVC["支付/资金服务"]
        Ops_SVC["运营/报表服务"]
    end

    subgraph 数据与中间件["数据与中间件"]
        RDB[(关系库\nMySQL / PostgreSQL)]
        Cache[(缓存\nRedis / Cluster)]
        MQ[消息队列\nKafka / RocketMQ]
        ES[检索/日志\nElasticsearch]
    end

    subgraph 基础设施["基础设施"]
        K8s["容器编排\nK8s / 云托管"]
        Monitor["监控\nPrometheus + Grafana"]
        Trace["链路\nJaeger / SkyWalking"]
    end

    Web & H5 & APP_Native --> LB
    LB --> API_GW
    API_GW --> BFF
    BFF --> Core_SVC & Risk_SVC & Pay_SVC
    Core_SVC --> RDB & Cache & MQ
    Risk_SVC --> RDB & Cache & ES
    Pay_SVC --> RDB & MQ
    Ops_SVC --> RDB & ES
    Core_SVC & Risk_SVC & Pay_SVC --> Trace
    K8s --> Core_SVC & Risk_SVC & Pay_SVC & Ops_SVC
    Monitor --> K8s
```

---

## 2. 组件与职责（技术组件图）

按业务域拆分的技术组件及其主要职责与技术选型倾向。

```mermaid
flowchart TB
    subgraph 网关与聚合
        GW["API 网关\n鉴权、限流、路由、协议转换"]
        BFF["BFF\n聚合、协议适配、会话"]
    end

    subgraph 信贷核心
        Apply["进件服务\n申请单、进件单、补充材料"]
        Credit["授信服务\n额度、审批流、有效期"]
        Draw["支用与放款服务\n借据、合同、放款指令"]
        Repay["还款服务\n还款计划、主动还款、代扣、逾期"]
        Collect["催收服务\n分案、任务、减免、核销"]
    end

    subgraph 风控与合规
        Rule["规则引擎\nDrools / 自研 / 开源规则"]
        Score["评分服务\n模型服务、征信解析"]
        List["名单与限额\n黑灰名单、集中度"]
    end

    subgraph 支付与资金
        Pay["支付网关适配\n银行/三方、放款、代扣"]
        Recon["对账服务\n日终对账、差错处理"]
    end

    subgraph 支撑
        User["用户与客户服务\n注册、实名、档案、授权"]
        Product["产品与定价服务\n产品、费率、额度策略"]
        Ops["运营服务\n工作台、审批、报表、配置"]
    end

    subgraph 数据与消息
        DB[(主库/从库)]
        Redis[(Redis)]
        MQ[Kafka/RocketMQ]
    end

    GW --> BFF
    BFF --> User & Product & Apply & Credit & Draw & Repay & Collect & Rule & Score & Pay & Ops
    Apply --> Credit --> Draw --> Repay
    Repay --> Collect
    Rule & Score & List --> Apply & Credit & Draw
    Pay --> Draw & Repay
    Recon --> Pay
    User & Product & Apply & Credit & Draw & Repay & Collect & Pay & Ops --> DB
    Apply & Credit & Draw & Repay & Collect --> Redis
    Draw & Repay & Pay --> MQ
```

---

## 3. 部署架构（逻辑部署图）

逻辑部署层次：用户 → 边缘/网关 → 应用集群 → 数据与外部。

```mermaid
flowchart TB
    subgraph 用户端
        UA[APP]
        UH[H5/Web]
    end

    subgraph 边缘与网关["边缘 / 网关"]
        CDN["CDN / 静态资源"]
        WAF["WAF"]
        SLB["SLB / 负载均衡"]
        GW["API 网关集群"]
    end

    subgraph 应用集群["应用集群"]
        BFF_Pod["BFF Pod x N"]
        Core_Pod["信贷核心 Pod x N"]
        Risk_Pod["风控服务 Pod x N"]
        Pay_Pod["支付服务 Pod x N"]
        Ops_Pod["运营服务 Pod x N"]
    end

    subgraph 数据层["数据层"]
        MySQL_M[(MySQL 主)]
        MySQL_S[(MySQL 从)]
        Redis_C[(Redis 集群)]
        Kafka_C[Kafka 集群]
    end

    subgraph 外部["外部系统"]
        Bank["银行/支付"]
        Credit_API["征信/数据源"]
        Contract["电子签章"]
    end

    UA & UH --> CDN
    UA & UH --> SLB
    SLB --> WAF --> GW
    GW --> BFF_Pod
    BFF_Pod --> Core_Pod & Risk_Pod & Pay_Pod & Ops_Pod
    Core_Pod & Risk_Pod & Pay_Pod & Ops_Pod --> MySQL_M
    Core_Pod & Risk_Pod & Ops_Pod --> MySQL_S
    Core_Pod & Risk_Pod --> Redis_C
    Core_Pod & Pay_Pod --> Kafka_C
    Pay_Pod --> Bank
    Risk_Pod --> Credit_API
    Core_Pod --> Contract
```

---

## 4. 关键链路技术流（申请 → 放款）

从技术视角看「进件 → 风控 → 授信 → 支用 → 放款」经过的组件与中间件。

```mermaid
sequenceDiagram
    participant C as 客户端
    participant GW as API 网关
    participant BFF as BFF
    participant Apply as 进件服务
    participant Rule as 规则引擎
    participant Score as 评分服务
    participant Credit as 授信服务
    participant Draw as 支用/放款服务
    participant Pay as 支付服务
    participant Redis as Redis
    participant DB as MySQL
    participant MQ as MQ

    C->>GW: 提交进件
    GW->>BFF: 鉴权转发
    BFF->>Apply: 进件请求
    Apply->>Redis: 名单/缓存
    Apply->>Rule: 反欺诈规则
    Rule-->>Apply: 通过/拒绝
    Apply->>Score: 征信+评分
    Score-->>Apply: 评分结果
    Apply->>Credit: 授信审批
    Credit->>DB: 写入额度
    Credit-->>BFF: 授信结果
    BFF-->>C: 授信成功

    C->>GW: 支用申请
    GW->>BFF->>Draw: 支用
    Draw->>Rule: 支用规则
    Draw->>DB: 借据+还款计划
    Draw->>Pay: 放款指令
    Pay->>MQ: 放款消息
    Pay-->>Draw: 放款结果
    Draw-->>C: 放款成功
```

---

## 5. 数据与中间件拓扑

数据库、缓存、消息在业务中的使用场景。

```mermaid
flowchart LR
    subgraph 写入与一致性
        Core["核心服务"] --> MySQL[(MySQL)]
        Core --> Redis[(Redis\n会话/限额/名单缓存)]
    end

    subgraph 异步与解耦
        Core --> MQ_Out[MQ 生产者]
        MQ_Out --> Kafka[RocketMQ/Kafka]
        Kafka --> Consumer1[放款结果消费者]
        Kafka --> Consumer2[还款通知消费者]
        Kafka --> Consumer3[报表/数仓]
    end

    subgraph 查询与检索
        Ops["运营/报表"] --> MySQL_Read[(只读从库)]
        Ops --> ES[Elasticsearch\n日志/检索]
    end

    subgraph 对账与审计
        Recon["对账服务"] --> MySQL
        Recon --> File[对账文件]
    end
```

---

## 6. 安全与高可用（逻辑视图）

身份、网络安全与高可用关键点。

```mermaid
flowchart TB
    subgraph 安全
        A1["传输加密\nTLS"]
        A2["认证\nJWT / OAuth2"]
        A3["敏感数据\n脱敏/加密存储"]
        A4["防刷/限流\n网关 + Redis"]
    end

    subgraph 高可用
        B1["无状态服务\n水平扩展"]
        B2["DB 主从\n读写分离"]
        B3["Redis 集群\n哨兵/Cluster"]
        B4["MQ 集群\n多副本"]
    end

    subgraph 可观测
        C1["日志\nELK / 云日志"]
        C2["指标\nPrometheus"]
        C3["链路\nTrace"]
        C4["告警\nPagerDuty / 钉钉"]
    end

    A1 & A2 & A3 & A4 --> 安全策略
    B1 & B2 & B3 & B4 --> 高可用策略
    C1 & C2 & C3 & C4 --> 可观测策略
```

---

## 7. 技术栈清单（表格）

便于与架构图对照的选型参考（可按实际替换）。

| 层次 | 组件 | 可选技术 | 说明 |
|------|------|----------|------|
| 前端 | 管理台 | React / Vue3 + TypeScript | 运营工作台、报表 |
| 前端 | 用户端 | Vue / Taro / RN / Flutter | H5、小程序、APP |
| 接入 | 网关 | Kong / APISIX / Spring Cloud Gateway | 鉴权、限流、路由 |
| 接入 | 负载均衡 | Nginx / SLB / ALB | 七层/四层 |
| 服务 | 语言/框架 | Java (Spring Boot) / Go | 信贷核心、风控、支付 |
| 服务 | 规则引擎 | Drools / 自研 / Aviator | 反欺诈、授信、定价规则 |
| 数据 | 关系库 | MySQL 8 / PostgreSQL | 主业务库 |
| 数据 | 缓存 | Redis 6+ Cluster | 会话、名单、限额、分布式锁 |
| 数据 | 消息 | Kafka / RocketMQ | 放款、还款、事件驱动 |
| 数据 | 检索/日志 | Elasticsearch | 日志、工单检索 |
| 基础设施 | 容器/编排 | K8s / 云托管 | 部署与弹性 |
| 基础设施 | 监控 | Prometheus + Grafana | 指标与大盘 |
| 基础设施 | 链路 | Jaeger / SkyWalking | 分布式追踪 |
| 外部 | 支付 | 银行直连 / 三方支付 | 放款、代扣 |
| 外部 | 征信 | 人行/百行/多头 API | 信用评估 |
| 外部 | 合同 | 电子签章/存证 | 合同生成与签署 |

---

## 图例与说明

| 图 | 用途 |
|----|------|
| 1 技术栈总览 | 前端→网关→服务→数据→基础设施 分层技术选型 |
| 2 组件与职责 | 各技术组件归属业务域及与 DB/Redis/MQ 关系 |
| 3 部署架构 | 边缘/网关/应用集群/数据/外部 逻辑部署 |
| 4 关键链路技术流 | 进件→风控→授信→支用→放款 时序与组件 |
| 5 数据与中间件拓扑 | MySQL/Redis/MQ/ES 在业务中的角色 |
| 6 安全与高可用 | 安全、高可用、可观测 要点 |
| 7 技术栈清单 | 与图中对应的选型表格，便于落地替换 |

业务/系统边界与域划分见 [ArchTechDiagram.md](./ArchTechDiagram.md)，功能需求见 [ArchTech.md](./ArchTech.md)。
