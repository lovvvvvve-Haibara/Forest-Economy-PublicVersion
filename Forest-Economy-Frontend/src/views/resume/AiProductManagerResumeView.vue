<template>
  <main class="pm-resume">
    <section class="hero">
      <p class="label">AI Product Manager Resume</p>
      <h1>AI 产品经理</h1>
      <p>该方向简历已保留入口。你可以继续在此页补充产品案例与指标成果。</p>
      <router-link class="back" to="/career">返回职业方向选择</router-link>
    </section>
  </main>
</template>

<style scoped>
.pm-resume {
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: 24px;
  background: linear-gradient(140deg, #fff9ef 0%, #fff 50%, #eef7ff 100%);
  font-family: 'Poppins', 'Noto Sans SC', 'Microsoft YaHei', sans-serif;
}

.hero {
  width: min(760px, 100%);
  background: #ffffffee;
  border: 1px solid #f0dfc8;
  border-radius: 20px;
  padding: 34px;
  box-shadow: 0 16px 36px rgba(73, 42, 2, 0.1);
}

.label {
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: #9d5d1a;
  margin-bottom: 12px;
  font-weight: 700;
}

h1 {
  font-size: clamp(30px, 5vw, 50px);
  margin-bottom: 10px;
  color: #2e1f0f;
}

p {
  font-size: 17px;
  line-height: 1.7;
  color: #5b4630;
  margin-bottom: 20px;
}

.back {
  text-decoration: none;
  font-weight: 700;
  color: #1f6f8b;
}
</style>
