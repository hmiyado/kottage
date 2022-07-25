export const Constants = {
  baseUrl:
    process.env.NODE_ENV == 'production'
      ? 'https://miyado.dev'
      : 'http://localhost:3000',
  title: 'miyado.dev',
  copyright: `(C) ${new Date().getFullYear()} miyado`,
  description: 'miyado の日常とときたま開発',
  author: 'miyado',
  keywords: ['日記', 'ITエンジニア'],
  locale: 'ja-JP',
}
