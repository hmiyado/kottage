/** @type {import('next-sitemap').IConfig} */
module.exports = {
    siteUrl: 'https://miyado.dev',
    sitemapSize: 7000,
    exclude: [
        '/500',
        '/pages/*',
    ],
    generateRobotsTxt: true,
    robotsTxtOptions: {
        policies: [
            {
                userAgent: '*',
                allow: '/',
            }
        ],
    },
}
