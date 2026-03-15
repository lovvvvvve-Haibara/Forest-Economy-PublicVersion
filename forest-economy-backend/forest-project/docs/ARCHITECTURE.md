# 系统架构说明

## 整体架构

```
┌─────────────────┐     ┌──────────────────────┐     ┌─────────────────────┐
│  Web / Android  │────▶│  Spring Boot         │────▶│  Python RAG 服务     │
│  客户端          │     │  鉴权、限流、聚合     │     │  （可选）             │
└─────────────────┘     └──────────────────────┘     └─────────────────────┘
                                    │
                                    ▼
                           ┌───────────────┐
                           │  MySQL         │
                           └───────────────┘
```

- **后端**：Spring Boot 3.4，提供 REST API（`/api/v1/*`），JWT 鉴权、IP 限流、统一异常与响应体。
- **RAG**：知识问答由外部 Python RAG 服务实现，后端通过 HTTP 转发；接口约定见 [RAG-Python-API-Contract.md](RAG-Python-API-Contract.md)。
- **客户端**：Web（Vue 3）与 Android 共用同一套 API，见 [API-For-Android-And-Web.md](API-For-Android-And-Web.md)。

## 技术栈

| 模块     | 技术 |
|----------|------|
| 后端     | Java 21, Spring Boot 3.4, MyBatis, MySQL, Caffeine, JWT |
| 前端     | Vue 3, Vite, Element Plus |
| RAG 服务 | Python FastAPI + LangChain + 向量库（由独立服务实现） |

## RAG 流程简述

1. **入库**：文档上传 → 解析与分块 → 向量化 → 写入向量库。
2. **问答**：用户问题 → 向量检索 Top-K → 拼 Prompt → LLM 生成 → 返回答案与引用来源。
