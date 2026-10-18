module.exports = {
  reactStrictMode: true,
  experimental: {
    useTypeScriptCli: true,
  },
  i18n: {
    locales: ['ja-JP'],
    defaultLocale: 'ja-JP',
  },
  async redirects() {
    return []
  },
}
