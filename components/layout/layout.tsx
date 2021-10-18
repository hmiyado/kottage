import Head from 'next/head'
import Header from './header/header'
import Footer from './footer/footer'
import styles from './layout.module.css'

export default function Layout({ children }: { children: JSX.Element }) {
  return (
    <>
      <Head>
        <title>Book of Days</title>
      </Head>
      <div className={styles.container}>
        <Header />
        <main className={styles.main}>{children}</main>
        <Footer />
      </div>
    </>
  )
}
