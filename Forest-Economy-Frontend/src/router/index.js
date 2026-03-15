import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),

  routes: [
    {
      path: '/',
      name: 'layout',
      component: () => import('@/views/layout/LayoutContainer.vue'),
      redirect: '/career',
      children: [
        {
          path: '/home',
          name: 'home',
          component: () => import('@/views/homeView.vue'),
          props: (route) => ({ new: route.query.new, historyId: route.query.historyId })
        },
        {
          path: '/community',
          name: 'community',
          component: () => import('@/views/CommunityView.vue')
        },
        {
          path: '/data_dashboard',
          name: 'data_dashboard',
          component: () => import('@/views/DataView.vue')
        },
        {
          path: '/data_management',
          name: 'data_management',
          component: () => import('@/views/dataManagementView.vue')
        }
      ]
    },
    {
      path: '/career',
      name: 'career',
      component: () => import('@/views/resume/CareerEntryView.vue')
    },
    {
      path: '/resume/ai-pm',
      name: 'resume-ai-pm',
      component: () => import('@/views/resume/AiProductManagerResumeView.vue')
    },
    {
      path: '/resume/fullstack',
      name: 'resume-fullstack',
      component: () => import('@/views/resume/FullStackEngineerResumeView.vue')
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/loginView.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/registerView.vue')
    }
  ]
})

export default router
