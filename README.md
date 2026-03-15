# Forest Economy

林下经济一体化平台：Web 与 Android 共用后端 API，支持用户与权限、知识问答（RAG）等能力。

## 仓库结构

| 目录 | 说明 |
|------|------|
| `Forest-Economy-Frontend` | Vue 3 前端（Vite + Element Plus） |
| `forest-economy-backend/forest-project` | Spring Boot 后端（Java 21） |
| `forest-economy-backend/forest-project/docs` | 后端文档（API 约定、架构、RAG 对接） |

## 快速开始

### 后端

- 依赖：JDK 21、Maven、MySQL
- 配置：复制 `forest-project/env.example` 为 `.env` 或设置环境变量，**必须设置** `JWT_SECRET_BASE64`、`DB_PASSWORD`；其余见 `application.yml` 中 `forest` 前缀配置项
- 运行：`mvn spring-boot:run`（在 `forest-project` 目录下）

### 前端

- 依赖：Node 18+
- 安装与运行：`pnpm install` 或 `npm install`，随后 `pnpm dev` 或 `npm run dev`
- 构建：`pnpm build` / `npm run build`

### 知识问答（RAG）

- 后端通过 HTTP 调用独立 Python RAG 服务；未部署时问答接口返回 503
- Python 接口约定与配置见：`forest-economy-backend/forest-project/docs/RAG-Python-API-Contract.md`
- 后端 RAG 配置：`application.yml` 中 `forest.rag.service-url`、`forest.rag.timeout-seconds`

## 文档

- [API 接口说明（Web / Android 通用）](forest-economy-backend/forest-project/docs/API-For-Android-And-Web.md)
- [系统架构](forest-economy-backend/forest-project/docs/ARCHITECTURE.md)
- [RAG Python 服务接口约定](forest-economy-backend/forest-project/docs/RAG-Python-API-Contract.md)
