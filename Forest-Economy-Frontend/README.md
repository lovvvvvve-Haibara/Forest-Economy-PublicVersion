# Forest Economy 前端

Vue 3 + Vite + Element Plus，与后端 `forest-project` 的 `/api/v1/*` 对接，和 Android 共用同一套 API。

## 开发

```bash
pnpm install   # 或 npm install
pnpm dev       # 或 npm run dev
```

## 构建与预览

```bash
pnpm build
pnpm preview   # 本地预览生产构建
```

## 脚本

| 命令 | 说明 |
|------|------|
| `pnpm dev` | 开发服务器 + 热更新 |
| `pnpm build` | 生产构建 |
| `pnpm preview` | 预览生产构建 |
| `pnpm lint` | ESLint |
| `pnpm test:unit` | Vitest 单元测试 |

## 配置

- 接口基地址：默认 `/api`（与 Vite 代理或同源后端配合）。需覆盖时在 `.env` 或 `.env.local` 中设置 `VITE_API_BASE_URL`（如 `http://localhost:8080`），勿提交含真实地址的 `.env`。
