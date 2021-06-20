import Head from 'next/head'
import Link from 'next/link'

export default function Layout({ children }: { children: JSX.Element }) {
  return (
    <div>
      <Head>
        <title>Book of Days</title>
      </Head>
      <header className="container bg-primary-700">
        <h1 className="text-4xl ">Book of Days</h1>
      </header>

      <main>{children}</main>

      <footer className="container bg-primary-700">
        <Link href="/contact">contact</Link>
      </footer>
    </div>
  )
}
