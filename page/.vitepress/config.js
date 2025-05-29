export default {
  title: 'TimeFit Dev Docs',
  description: '예약 시스템 개발 문서',
  base: '/timefit/',
  
  themeConfig: {
    nav: [
      { text: 'Home', link: '/' },
      { text: 'Tech Stack', link: '/tech-stack' },
      { text: 'Architecture', link: '/architecture' },
      { text: 'API', link: '/api' }
    ],
    
    sidebar: [
      {
        text: '시작하기',
        items: [
          { text: '프로젝트 소개', link: '/' },
          { text: '기술 스택', link: '/tech-stack' }
        ]
      },
      {
        text: '개발 가이드',
        items: [
          { text: '시스템 아키텍처', link: '/architecture' },
          { text: 'API 문서', link: '/api' }
        ]
      }
    ],

    socialLinks: [
      { icon: 'github', link: 'https://github.com/your-username/timefit' }
    ],

    search: {
      provider: 'local'
    }
  }
} 