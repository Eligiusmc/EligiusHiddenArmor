import { defineConfig } from 'vitepress'

export default defineConfig({
  base: '/EligiusHiddenArmor/',
  title: "EligiusHiddenArmor",
  srcDir: 'src',
  sitemap: {
    hostname: 'https://eligiusmc.github.io/EligiusHiddenArmor/'
  },
  transformHead({ siteConfig, pageData }) {
    const head = []
    const locales = Object.keys(siteConfig.site.locales)
    
    // Add hreflang for all locales
    for (const locale of locales) {
      if (locale === 'root') continue
      head.push(['link', { rel: 'alternate', hreflang: locale, href: `https://eligiusmc.github.io/EligiusHiddenArmor/${locale}/${pageData.relativePath.replace('index.md', '').replace('.md', '')}` }])
    }
    // Add x-default
    head.push(['link', { rel: 'alternate', hreflang: 'x-default', href: `https://eligiusmc.github.io/EligiusHiddenArmor/${pageData.relativePath.replace('index.md', '').replace('.md', '')}` }])
    
    return head
  },
  head: [
    ['link', { rel: 'icon', href: '/EligiusHiddenArmor/assets/angry.png' }],
    ['script', {}, `
      (function() {
        if (typeof window === 'undefined') return;
        if (localStorage.getItem('lang-redirect-done')) return;
        var lang = navigator.language || navigator.userLanguage;
        var prefix = lang.split('-')[0];
        var supported = ['es', 'fr', 'de', 'pt', 'ru'];
        var currentPath = window.location.pathname;
        if (supported.includes(prefix) && (currentPath === '/EligiusHiddenArmor/' || currentPath === '/EligiusHiddenArmor/index.html')) {
          localStorage.setItem('lang-redirect-done', 'true');
          window.location.href = '/EligiusHiddenArmor/' + prefix + '/';
        }
      })();
    `]
  ],
      locales: {
    root: {
      label: 'EN',
      lang: 'en',
      themeConfig: {
        nav: [
          { text: '🏠 Home', link: '/' },
          { text: '📖 Docs', link: '/installation' }
        ],
        sidebar: [
          {
            text: '🚀 Installation',
            items: [
              { text: '🚀 Installation', link: '/installation' },
              { text: '💻 Commands', link: '/commands' }
            ]
          },
          {
            text: '⚙️ Global Config',
            items: [
              { text: '⚙️ Global Config', link: '/config/global' },
              { text: '💽 Database', link: '/config/database' },
              { text: '🌐 Redis Sync', link: '/config/redis' }
            ]
          },
          {
            text: '🛡️ Support',
            items: [
              { text: '🛡️ Troubleshooting', link: '/troubleshooting' }
            ]
          }
        ]
      }
    },
    es: {
      label: 'ES',
      lang: 'es',
      themeConfig: {
        nav: [
          { text: '🏠 Home', link: '/es/' },
          { text: '📖 Docs', link: '/es/installation' }
        ],
        sidebar: [
          {
            text: '🚀 Instalación',
            items: [
              { text: '🚀 Instalación', link: '/es/installation' },
              { text: '💻 Comandos', link: '/es/commands' }
            ]
          },
          {
            text: '⚙️ Config. Global',
            items: [
              { text: '⚙️ Config. Global', link: '/es/config/global' },
              { text: '💽 Base de Datos', link: '/es/config/database' },
              { text: '🌐 Redis Sync', link: '/es/config/redis' }
            ]
          },
          {
            text: '🛡️ Support',
            items: [
              { text: '🛡️ Solución de Errores', link: '/es/troubleshooting' }
            ]
          }
        ]
      }
    },
    fr: {
      label: 'FR',
      lang: 'fr',
      themeConfig: {
        nav: [
          { text: '🏠 Home', link: '/fr/' },
          { text: '📖 Docs', link: '/fr/installation' }
        ],
        sidebar: [
          {
            text: '🚀 Installation',
            items: [
              { text: '🚀 Installation', link: '/fr/installation' },
              { text: '💻 Commands', link: '/fr/commands' }
            ]
          },
          {
            text: '⚙️ Global Config',
            items: [
              { text: '⚙️ Global Config', link: '/fr/config/global' },
              { text: '💽 Database', link: '/fr/config/database' },
              { text: '🌐 Redis Sync', link: '/fr/config/redis' }
            ]
          },
          {
            text: '🛡️ Support',
            items: [
              { text: '🛡️ Troubleshooting', link: '/fr/troubleshooting' }
            ]
          }
        ]
      }
    },
    de: {
      label: 'DE',
      lang: 'de',
      themeConfig: {
        nav: [
          { text: '🏠 Home', link: '/de/' },
          { text: '📖 Docs', link: '/de/installation' }
        ],
        sidebar: [
          {
            text: '🚀 Installation',
            items: [
              { text: '🚀 Installation', link: '/de/installation' },
              { text: '💻 Commands', link: '/de/commands' }
            ]
          },
          {
            text: '⚙️ Global Config',
            items: [
              { text: '⚙️ Global Config', link: '/de/config/global' },
              { text: '💽 Database', link: '/de/config/database' },
              { text: '🌐 Redis Sync', link: '/de/config/redis' }
            ]
          },
          {
            text: '🛡️ Support',
            items: [
              { text: '🛡️ Troubleshooting', link: '/de/troubleshooting' }
            ]
          }
        ]
      }
    },
    pt: {
      label: 'PT',
      lang: 'pt',
      themeConfig: {
        nav: [
          { text: '🏠 Home', link: '/pt/' },
          { text: '📖 Docs', link: '/pt/installation' }
        ],
        sidebar: [
          {
            text: '🚀 Installation',
            items: [
              { text: '🚀 Installation', link: '/pt/installation' },
              { text: '💻 Commands', link: '/pt/commands' }
            ]
          },
          {
            text: '⚙️ Global Config',
            items: [
              { text: '⚙️ Global Config', link: '/pt/config/global' },
              { text: '💽 Database', link: '/pt/config/database' },
              { text: '🌐 Redis Sync', link: '/pt/config/redis' }
            ]
          },
          {
            text: '🛡️ Support',
            items: [
              { text: '🛡️ Troubleshooting', link: '/pt/troubleshooting' }
            ]
          }
        ]
      }
    },
    ru: {
      label: 'RU',
      lang: 'ru',
      themeConfig: {
        nav: [
          { text: '🏠 Home', link: '/ru/' },
          { text: '📖 Docs', link: '/ru/installation' }
        ],
        sidebar: [
          {
            text: '🚀 Installation',
            items: [
              { text: '🚀 Installation', link: '/ru/installation' },
              { text: '💻 Commands', link: '/ru/commands' }
            ]
          },
          {
            text: '⚙️ Global Config',
            items: [
              { text: '⚙️ Global Config', link: '/ru/config/global' },
              { text: '💽 Database', link: '/ru/config/database' },
              { text: '🌐 Redis Sync', link: '/ru/config/redis' }
            ]
          },
          {
            text: '🛡️ Support',
            items: [
              { text: '🛡️ Troubleshooting', link: '/ru/troubleshooting' }
            ]
          }
        ]
      }
    }
  },
  themeConfig: {
    socialLinks: [
      { icon: 'github', link: 'https://github.com/Eligiusmc/EligiusHiddenArmor' }
    ],
    footer: {
      message: 'Released under the MIT License.',
      copyright: 'Copyright © 2026 Eligius MC'
    }
  }
})
