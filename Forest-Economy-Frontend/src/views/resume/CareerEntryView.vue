<template>
  <main class="career-entry">
    <section class="hero">
      <p class="eyebrow">Career Portfolio</p>
      <h1>请选择你要查看的简历方向</h1>
      <p class="subtitle">同一份经历，不同的表达重点。你可以从产品视角或工程视角进入。</p>
    </section>

    <section class="path-grid">
      <article class="path-card pm">
        <div class="badge">Strategy</div>
        <h2>AI 产品经理</h2>
        <p>关注产品战略、用户增长、数据闭环与 AI 场景落地。</p>
        <router-link class="entry-btn" to="/resume/ai-pm">进入 AI 产品经理简历</router-link>
      </article>

      <article class="path-card fe">
        <div class="badge">Engineering</div>
        <h2>全栈工程师</h2>
        <p>关注系统架构、前后端实现、性能优化与工程质量。</p>
        <router-link class="entry-btn" to="/resume/fullstack">进入全栈工程师简历</router-link>
      </article>
    </section>
  </main>
</template>

<style scoped>
.career-entry {
  min-height: 100vh;
  color: #132238;
  padding: 72px 24px;
  background:
    radial-gradient(circle at 10% 10%, #d6f4ff 0%, transparent 35%),
    radial-gradient(circle at 90% 90%, #dff5e8 0%, transparent 40%),
    linear-gradient(135deg, #f5fbff 0%, #f8fff6 100%);
  font-family: 'Poppins', 'Noto Sans SC', 'Microsoft YaHei', sans-serif;
}

.hero {
  max-width: 840px;
  margin: 0 auto;
  text-align: center;
}

.eyebrow {
  letter-spacing: 0.1em;
  text-transform: uppercase;
  font-weight: 700;
  color: #0f6f87;
  margin-bottom: 10px;
}

h1 {
  font-size: clamp(30px, 5vw, 52px);
  line-height: 1.08;
  margin-bottom: 12px;
}

.subtitle {
  font-size: 18px;
  color: #34506a;
}

.path-grid {
  max-width: 1080px;
  margin: 48px auto 0;
  display: grid;
  grid-template-columns: repeat(2, minmax(260px, 1fr));
  gap: 24px;
}

.path-card {
  border-radius: 24px;
  padding: 28px;
  background: #ffffffcc;
  border: 1px solid #d7e5ee;
  box-shadow: 0 20px 40px rgba(12, 51, 83, 0.08);
  backdrop-filter: blur(2px);
  transition: transform 0.25s ease, box-shadow 0.25s ease;
}

.path-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 24px 50px rgba(12, 51, 83, 0.15);
}

.badge {
  display: inline-block;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  padding: 5px 10px;
  border-radius: 999px;
  margin-bottom: 16px;
}

.pm .badge {
  color: #894b10;
  background: #fff1e4;
}

.fe .badge {
  color: #155d54;
  background: #e6fff7;
}

h2 {
  font-size: 30px;
  margin-bottom: 10px;
}

.path-card p {
  color: #446178;
  min-height: 52px;
  margin-bottom: 24px;
}

.entry-btn {
  display: inline-flex;
  justify-content: center;
  align-items: center;
  text-decoration: none;
  font-weight: 700;
  width: 100%;
  padding: 13px 16px;
  border-radius: 12px;
  color: #fff;
  background: linear-gradient(90deg, #1f6f8b 0%, #22867b 100%);
}

.entry-btn:hover {
  filter: brightness(0.95);
}

@media (max-width: 840px) {
  .path-grid {
    grid-template-columns: 1fr;
  }
}
</style>
