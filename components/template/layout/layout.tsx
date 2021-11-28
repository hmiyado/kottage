import Head from 'next/head'
import Header from './header/header'
import Footer from './footer/footer'
import styles from './layout.module.css'

export default function Layout({ children }: { children: JSX.Element }) {
  return (
    <>
      <Head>
        <meta charSet="utf-8" />
        <title>miyado.dev</title>
        <meta name="description" content="miyado の日常とときたま開発" />
        <meta name="author" content="miyado" />
        <meta name="keywords" content="日記,ITエンジニア" />

        <meta name="viewport" content="width=device-width,initial-scale=1" />
        <meta name="color-scheme" content="dark light" />
        <link
          rel="apple-touch-icon"
          sizes="180x180"
          href="/favicons/apple-touch-icon.png"
        />
        <link
          rel="icon"
          type="image/png"
          sizes="32x32"
          href="/favicons/favicon-32x32.png"
        />
        <link
          rel="icon"
          type="image/png"
          sizes="16x16"
          href="/favicons/favicon-16x16.png"
        />
        <link rel="manifest" href="/site.webmanifest" />
        <link
          rel="mask-icon"
          href="/favicons/safari-pinned-tab.svg"
          color="#3a4b7c"
        />
        <meta name="msapplication-TileColor" content="#3a4b7c" />
        <meta name="theme-color" content="#ffffff"></meta>

        <meta name="robots" content="index, follow" />
      </Head>
      <div className={styles.container}>
        <Header />
        <div className={styles.main}>{children}</div>
        <Footer />
      </div>
    </>
  )
}
