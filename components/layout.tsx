import Head from 'next/head'
import Link from 'next/link'

export default function Layout({ children }: { children: JSX.Element }) {
  return (
    <div>
      <Head>
        <title>Book of Days</title>
      </Head>
      <header>
        <h1>Book of Days</h1>
      </header>

      <main>{children}</main>

      <footer>
        <Link href="/contact">contact</Link>
      </footer>
    </div>
  )
}
