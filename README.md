# Forest Economy（开源整理版）

## 项目简介

本仓库是一个 **林下经济相关的全栈示例项目**，包含：

- `forest-economy-backend/forest-project`：Spring Boot + MyBatis 后端服务。
- `Forest-Economy-Frontend`：Vue 3 + Vite 前端应用。

> ## Sanitization Notice
> This repository is a sanitized version of an internal project.
>
> Certain components such as proprietary datasets, internal APIs,
> and confidential infrastructure details have been removed or simplified.
>
> Some modules may contain placeholders or mock implementations.
> These are intentional and do not represent the full internal system.

仓库中的部分能力（例如外部 AI 能力、内部知识库能力）为占位或简化实现，目的是让外部贡献者能够在不依赖私有系统的前提下完成开发、调试和二次扩展。

---

## 目录结构

```text
.
├── forest-economy-backend/
│   └── forest-project/                 # Java 后端
├── Forest-Economy-Frontend/            # Vue 前端
├── .env.example                        # 统一环境变量示例
├── CONTRIBUTING.md
├── CODE_OF_CONDUCT.md
└── .github/
    ├── PULL_REQUEST_TEMPLATE.md
    └── ISSUE_TEMPLATE/
```

---

## 环境要求

- Node.js 18+
- npm 9+（或 pnpm）
- Java 21
- Maven 3.9+
- MySQL 8.x（可替换为你自己的兼容数据库配置）

---

## 安装与启动

### 1）克隆与环境变量

```bash
git clone <your-fork-or-repo-url>
cd Forest-Economy-PublicVersion
cp .env.example .env
```

根据你的环境编辑 `.env`（尤其是数据库与 JWT 密钥）。

### 2）启动后端

```bash
cd forest-economy-backend/forest-project
mvn spring-boot:run
```

默认读取环境变量（示例见 `.env.example`）。

### 3）启动前端

```bash
cd Forest-Economy-Frontend
npm install
npm run dev
```

---

## 使用示例

1. 打开前端页面并注册/登录用户。
2. 在“知识问答”页面输入问题。
3. 若未配置 `VITE_XAI_API_KEY`，系统将返回内置 mock 响应（用于开源演示）。
4. 配置真实 API Key 后，将调用外部 AI 接口。

---

## 配置说明

### 后端环境变量

- `DB_URL`：数据库连接串
- `DB_USERNAME`：数据库用户名
- `DB_PASSWORD`：数据库密码
- `JWT_SECRET_BASE64`：JWT 签名密钥（Base64 编码）
- `JWT_EXPIRATION_MS`：JWT 过期时间（毫秒）

### 前端环境变量

- `VITE_XAI_API_KEY`：外部模型 API Key（可为空，为空时使用 mock）
- `VITE_XAI_API_ENDPOINT`：模型接口地址
- `VITE_XAI_MODEL`：模型名称

---

## 占位与 Mock 说明

以下能力在开源版本中为刻意简化：

- `TODO: placeholder for internal service`：内部服务调用能力以占位方式保留。
- `Mock implementation for removed internal knowledge base`：内部知识库问答使用前端 mock 响应代替。
- `Example dataset instead of proprietary dataset`：私有数据集已移除，请接入你自己的公开数据源。

---

## 贡献指南

请先阅读：

- [CONTRIBUTING.md](./CONTRIBUTING.md)
- [CODE_OF_CONDUCT.md](./CODE_OF_CONDUCT.md)

欢迎提交 Issue 与 PR 来改进可维护性、测试覆盖率与开源文档。
