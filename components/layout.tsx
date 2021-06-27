import Head from 'next/head'
import styles from '../styles/Layout.module.css'

export default function Layout({ children }: { children: JSX.Element }) {
  return (
    <>
      <Head>
        <title>Book of Days</title>
      </Head>
      <header className={styles.header}>
        <h1 className={styles.title}>Book of Days</h1>
      </header>

      <main className={styles.main}>{children}</main>
    </>
  )
}
