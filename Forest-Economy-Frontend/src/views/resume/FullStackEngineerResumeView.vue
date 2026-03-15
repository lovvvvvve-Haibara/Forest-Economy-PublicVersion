<template>
  <main class="fullstack-page">
    <header class="hero card">
      <div class="hero-main">
        <p class="eyebrow">Full-Stack Engineer Resume</p>
        <h1>张三 · 全栈工程师</h1>
        <p class="summary">
          3+ 年全栈开发经验，聚焦 Vue + Node.js + MySQL 技术栈，具备从产品需求拆解到上线运维的完整交付能力。
          关注性能、稳定性和可维护性，能够独立负责中小型系统的架构与落地。
        </p>
      </div>
      <div class="meta">
        <div><span>地点</span>杭州 / 可远程</div>
        <div><span>邮箱</span>hello@example.com</div>
        <div><span>电话</span>138-0000-0000</div>
        <div><span>GitHub</span>github.com/yourname</div>
      </div>
    </header>

    <section class="grid two-cols">
      <article class="card">
        <h2>核心技能</h2>
        <ul class="chips">
          <li>JavaScript / TypeScript</li>
          <li>Vue 3 / Pinia / Vite</li>
          <li>Node.js / Express</li>
          <li>Python / FastAPI</li>
          <li>MySQL / Redis</li>
          <li>Docker / Nginx / Linux</li>
          <li>GitHub Actions / CI-CD</li>
          <li>单元测试 / 接口测试</li>
        </ul>
      </article>

      <article class="card">
        <h2>工程亮点</h2>
        <ul class="highlights">
          <li>主导 0 到 1 开发企业后台系统，3 个月完成 MVP 并上线。</li>
          <li>前端首屏渲染耗时从 2.8s 优化至 1.3s（-53%）。</li>
          <li>设计统一 API 错误码与日志追踪，线上定位效率提升约 40%。</li>
          <li>建立 CI/CD 流程，发布频率从每周 1 次提升到每天多次。</li>
        </ul>
      </article>
    </section>

    <section class="card">
      <h2>项目经历</h2>
      <div class="projects">
        <article class="project">
          <h3>智能运营管理平台</h3>
          <p class="stack">Vue 3 + Element Plus + Node.js + MySQL + Redis</p>
          <p>负责前后端全链路开发，完成用户权限、数据看板、消息中心与报表导出等核心模块。</p>
          <ul>
            <li>重构权限模块为 RBAC，支持细粒度菜单与按钮级权限。</li>
            <li>基于 Redis 缓存热点数据，报表接口平均响应时间降低 48%。</li>
            <li>引入分层日志与链路 ID，线上问题排查时间缩短约 35%。</li>
          </ul>
        </article>

        <article class="project">
          <h3>电商中台服务</h3>
          <p class="stack">TypeScript + Express + MySQL + Docker</p>
          <p>负责订单与库存服务，支持高峰期并发请求处理与数据一致性保障。</p>
          <ul>
            <li>设计订单状态机，减少异常订单回滚逻辑复杂度。</li>
            <li>通过数据库索引优化与慢 SQL 治理，关键查询性能提升 60%+。</li>
            <li>落地容器化部署与自动化发布，降低环境差异带来的故障风险。</li>
          </ul>
        </article>
      </div>
    </section>

    <section class="grid two-cols">
      <article class="card">
        <h2>工作经历</h2>
        <div class="timeline-item">
          <h3>某科技公司 · 全栈工程师</h3>
          <p class="period">2023.03 - 至今</p>
          <p>参与 B 端系统建设，负责需求评审、架构设计、开发上线与迭代优化。</p>
        </div>
        <div class="timeline-item">
          <h3>某互联网公司 · 前端工程师</h3>
          <p class="period">2021.07 - 2023.02</p>
          <p>主导前端组件化建设，逐步扩展到接口设计与服务端开发，转型为全栈角色。</p>
        </div>
      </article>

      <article class="card">
        <h2>教育与证书</h2>
        <div class="timeline-item">
          <h3>XX 大学 · 计算机科学与技术</h3>
          <p class="period">2017 - 2021</p>
        </div>
        <ul class="certs">
          <li>阿里云 ACA 云计算认证</li>
          <li>英语 CET-6</li>
          <li>软考中级（系统集成）</li>
        </ul>
      </article>
    </section>

    <footer class="footer card">
      <p>可根据目标岗位（业务全栈 / 平台工程 / AI 工程支持）进一步定制版本。</p>
      <router-link to="/career" class="back-link">返回职业方向选择</router-link>
    </footer>
  </main>
</template>

<style scoped>
.fullstack-page {
  --text: #14263b;
  --muted: #4a647d;
  --line: #d6e5f2;
  --surface: #ffffffd9;
  min-height: 100vh;
  padding: 36px 18px 56px;
  color: var(--text);
  background:
    radial-gradient(circle at 88% 6%, #b7ecff 0%, transparent 26%),
    radial-gradient(circle at 12% 94%, #ccf7df 0%, transparent 24%),
    linear-gradient(135deg, #f3fbff 0%, #effff7 48%, #f8fbff 100%);
  font-family: 'Space Grotesk', 'Noto Sans SC', 'Microsoft YaHei', sans-serif;
}

.card {
  width: min(1080px, 100%);
  margin: 0 auto 18px;
  background: var(--surface);
  border: 1px solid var(--line);
  border-radius: 18px;
  padding: 24px;
  box-shadow: 0 14px 36px rgba(20, 38, 59, 0.08);
}

.hero {
  display: grid;
  grid-template-columns: 1.8fr 1fr;
  gap: 20px;
}

.eyebrow {
  margin-bottom: 10px;
  color: #0f6f87;
  font-size: 12px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  font-weight: 700;
}

h1 {
  font-size: clamp(30px, 5vw, 48px);
  line-height: 1.1;
  margin-bottom: 12px;
}

.summary {
  color: var(--muted);
  line-height: 1.7;
  font-size: 16px;
}

.meta {
  display: grid;
  gap: 10px;
  align-content: start;
}

.meta div {
  border: 1px dashed #b4d1e6;
  border-radius: 12px;
  padding: 10px 12px;
  font-size: 14px;
}

.meta span {
  color: #0f6f87;
  font-weight: 700;
  margin-right: 10px;
}

.grid {
  width: min(1080px, 100%);
  margin: 0 auto 18px;
  display: grid;
  gap: 18px;
}

.two-cols {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

h2 {
  font-size: 24px;
  margin-bottom: 14px;
}

h3 {
  font-size: 18px;
  margin-bottom: 6px;
}

.chips {
  list-style: none;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.chips li {
  border: 1px solid #abd1eb;
  border-radius: 999px;
  padding: 8px 12px;
  font-size: 14px;
  background: #eef8ff;
}

.highlights,
.project ul,
.certs {
  padding-left: 18px;
  color: var(--muted);
  line-height: 1.7;
}

.projects {
  display: grid;
  gap: 16px;
}

.project {
  border: 1px solid #d6e5f2;
  border-radius: 14px;
  padding: 16px;
  background: #fff;
}

.stack {
  color: #0f6f87;
  font-weight: 700;
  margin-bottom: 8px;
  font-size: 14px;
}

.project > p {
  color: var(--muted);
  margin-bottom: 8px;
  line-height: 1.7;
}

.timeline-item + .timeline-item {
  margin-top: 14px;
  padding-top: 14px;
  border-top: 1px solid #dfeaf3;
}

.period {
  color: #0f6f87;
  font-weight: 700;
  font-size: 14px;
  margin-bottom: 8px;
}

.footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.footer p {
  color: var(--muted);
}

.back-link {
  color: #0f6f87;
  font-weight: 700;
  text-decoration: none;
  white-space: nowrap;
}

@media (max-width: 900px) {
  .hero,
  .two-cols {
    grid-template-columns: 1fr;
  }

  .footer {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
