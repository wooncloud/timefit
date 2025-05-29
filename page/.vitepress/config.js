export default {
  title: 'TimeFit',
  description: '예약 시스템 개발 문서',
  base: '/timefit/',
  
  themeConfig: {
    nav: [
      { text: 'Home', link: '/' },
      { text: 'Architecture', link: '/architecture' },
      { text: 'API', link: '/api' }
    ],
    
    sidebar: [
      {
        text: 'Getting Started',
        items: [
          { text: 'Introduction', link: '/' },
          { text: 'Tech Stack', link: '/tech-stack' }
        ]
      },
      {
        text: 'Development',
        items: [
          { text: 'Architecture', link: '/architecture' },
          { text: 'API Documentation', link: '/api' }
        ]
      }
    ],

    socialLinks: [
      { icon: 'github', link: 'https://github.com/your-username/timefit' }
    ]
  }
} 