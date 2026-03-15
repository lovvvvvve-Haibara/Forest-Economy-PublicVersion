# Forest Economy 后端 API 接口说明（Web / Android 通用）

本文档描述 Spring Boot 对外提供的 REST 接口，**Web 前端与 Android APP 共用同一套 `/api/v1/*` 接口**，按需鉴权、统一响应格式。

---

## 1. 通用约定

### 1.1 Base URL

- 开发/测试：如 `http://10.0.2.2:8080`（Android 模拟器访问本机）或 `http://<本机IP>:8080`
- 生产：由部署域名决定，如 `https://api.example.com`

### 1.2 统一响应体

所有接口均返回 JSON，结构为：

```json
{
  "code": 1,
  "message": "success",
  "data": { ... }
}
```

- **code**：业务码。`1` 表示成功；非 1 表示失败（见下表）。
- **message**：提示信息，成功多为 `"success"`，失败时为错误说明。
- **data**：成功时的业务数据；无数据时可能为 `null` 或不带该字段。

**常见 code（与 HTTP 状态无强绑定，以 body 为准）**

| code | 含义         |
|------|--------------|
| 1    | 成功         |
| 400  | 请求参数错误 |
| 401  | 未登录/Token 无效 |
| 403  | 无权限       |
| 404  | 资源不存在   |
| 409  | 冲突（如重复注册） |
| 429  | 请求过于频繁（限流） |
| 500  | 服务器内部错误 |
| 503  | 依赖服务不可用（如 RAG 服务） |

### 1.3 鉴权（需登录接口）

除登录、注册等白名单接口外，其余接口需携带 **JWT**：

- 推荐：请求头 `Authorization: Bearer <token>`
- 兼容：请求头 `token: <token>`

登录接口返回的 `token` 即此处使用的 `<token>`。未带或无效时返回 `code: 401`。

### 1.4 请求头建议

- `Content-Type: application/json`（有 JSON body 时）
- `Accept: application/json`
- 鉴权：`Authorization: Bearer <token>` 或 `token: <token>`

---

## 2. 认证相关（白名单，无需 Token）

### 2.1 登录

- **POST** `/api/v1/auth/login` 或 `/login`
- **Body (JSON)**
```json
{
  "usernameOrPhone": "用户名或手机号",
  "password": "密码"
}
```
- **成功 data**
```json
{
  "id": 1,
  "username": "zhangsan",
  "token": "eyJhbGciOiJIUzI1NiIs..."
}
```
- Android 需持久化 `token`，后续请求均带此 token。

### 2.2 注册

- **POST** `/api/v1/auth/register` 或 `/register`
- **Body (JSON)**
```json
{
  "username": "2-20 字符",
  "phoneNumber": "11 位 1 开头手机号",
  "password": "8-32 位"
}
```
- **成功**：`data` 可为 `null`，`message: "success"`。

---

## 3. 用户相关（需 Token）

### 3.1 获取用户简要信息

- **GET** `/api/v1/users/{id}`
- **Path**：`id` 为用户 ID（整数）
- **成功 data**
```json
{
  "id": 1,
  "username": "zhangsan",
  "phoneNumber": "13800138000"
}
```

### 3.2 删除用户

- **DELETE** `/api/v1/users/{id}` 或 `/home/{id}`
- **Path**：`id` 为用户 ID（整数）
- **成功**：`data` 可为 `null`。

---

## 4. 知识问答 RAG（需 Token）

与 RAG 知识问答相关，依赖后端转发的 Python RAG 服务；若 RAG 服务不可用，会返回 `code: 503`。

### 4.1 发起问答

- **POST** `/api/v1/qa/ask`
- **Body (JSON)**
```json
{
  "question": "用户问题文本",
  "topK": 5
}
```
- `topK` 可选，不传由服务端默认。
- **成功 data**
```json
{
  "answer": "基于知识库生成的回答",
  "sources": [
    {
      "title": "文档或片段标题",
      "snippet": "引用内容",
      "sourceId": "可选"
    }
  ]
}
```

### 4.2 上传文档入库

- **POST** `/api/v1/qa/ingest`
- **Content-Type**：`multipart/form-data`
- **Body**：表单字段名 `file`，值为文件（如 PDF、TXT、MD）
- **成功 data**
```json
{
  "document_id": "入库后的文档 ID",
  "chunks": 12
}
```

### 4.3 文档列表

- **GET** `/api/v1/qa/documents`
- **成功 data**：数组
```json
[
  {
    "id": "doc-uuid",
    "title": "文档标题",
    "created_at": "2025-03-15T10:00:00"
  }
]
```

### 4.4 删除文档

- **DELETE** `/api/v1/qa/documents/{id}`
- **Path**：`id` 为文档 ID（字符串）
- **成功**：`data` 可为 `null`。

---

## 5. 接口一览

| 分类     | 方法   | 路径                         | 鉴权 | 说明           |
|----------|--------|------------------------------|------|----------------|
| 认证     | POST   | /api/v1/auth/login           | 否   | 登录           |
| 认证     | POST   | /api/v1/auth/register        | 否   | 注册           |
| 用户     | GET    | /api/v1/users/{id}           | 是   | 用户简要信息   |
| 用户     | DELETE | /api/v1/users/{id}           | 是   | 删除用户       |
| 知识问答 | POST   | /api/v1/qa/ask               | 是   | 发起问答       |
| 知识问答 | POST   | /api/v1/qa/ingest            | 是   | 上传文档入库   |
| 知识问答 | GET    | /api/v1/qa/documents         | 是   | 文档列表       |
| 知识问答 | DELETE | /api/v1/qa/documents/{id}    | 是   | 删除文档       |
