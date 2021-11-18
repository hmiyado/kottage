import Head from 'next/head'
import Header from './header/header'
import Footer from './footer/footer'
import styles from './layout.module.css'

export default function Layout({ children }: { children: JSX.Element }) {
  return (
    <>
      <Head>
        <title>Book of Days</title>
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
      </Head>
      <div className={styles.container}>
        <Header />
        <main className={styles.main}>{children}</main>
        <Footer />
      </div>
    </>
  )
}
