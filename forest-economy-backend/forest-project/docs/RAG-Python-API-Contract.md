# Python RAG 服务接口约定

Spring Boot 通过 HTTP 调用 Python RAG 服务，以下为约定请求/响应格式。Python 端需实现这些接口。

**Base URL**：由 `forest.rag.service-url` 配置（如 `http://localhost:8000`）。

---

## 1. 问答 `POST /qa`

**请求**

- Content-Type: `application/json`
- Body:
```json
{
  "question": "用户问题文本",
  "top_k": 5
}
```
- `top_k` 可选，不传时由 Python 端使用默认值。

**响应**

- 200 OK
- Body:
```json
{
  "answer": "基于知识库生成的回答",
  "sources": [
    {
      "title": "文档或片段标题",
      "snippet": "引用片段内容",
      "sourceId": "可选，文档/片段 ID"
    }
  ]
}
```

---

## 2. 文档入库 `POST /ingest`

**请求**

- Content-Type: `multipart/form-data`
- 字段名: `file`，值为文档文件（如 PDF、TXT、MD）。

**响应**

- 200 OK
- Body:
```json
{
  "document_id": "入库后的文档 ID",
  "chunks": 12
}
```

---

## 3. 文档列表 `GET /ingest/documents`

**请求**

- 无 body。

**响应**

- 200 OK
- Body: JSON 数组
```json
[
  {
    "id": "文档 ID",
    "title": "文档标题",
    "created_at": "可选，如 2025-03-15T10:00:00Z"
  }
]
```

---

## 4. 删除文档 `DELETE /ingest/documents/{id}`

**请求**

- 路径参数: `id` 为文档 ID。

**响应**

- 200 或 204，无 body 或任意 JSON。

---

## 超时

- Spring Boot 侧读超时由 `forest.rag.timeout-seconds` 控制（默认 60 秒），连接超时 10 秒。
- Python 端建议在 60 秒内返回，否则会被 Spring 端判定为超时并返回 503。
