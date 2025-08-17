export const Constants = {
  baseUrl:
    process.env.NODE_ENV == 'production'
      ? 'https://miyado.dev'
      : 'http://localhost:3000',
  backendUrl:
    process.env.NEXT_PUBLIC_KOTTAGE_BASE_PATH ?? 'http://localhost:3000',
  atomFilePath: '/feed/atom.xml',
  title: 'miyado.dev',
  copyright: `(C) 2022 miyado`,
  description: 'miyado の日常とときたま開発',
  author: 'miyado',
  keywords: ['日記', 'ITエンジニア'],
  locale: 'ja-JP',
}
