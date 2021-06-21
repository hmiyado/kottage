module.exports = {
  reactStrictMode: true,
  async redirects() {
    return [
      {
        source: '/',
        destination: '/contact',
        permanent: true,
      },
    ]
  },
}
