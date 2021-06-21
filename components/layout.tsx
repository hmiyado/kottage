import Head from 'next/head'
import Link from 'next/link'

export default function Layout({ children }: { children: JSX.Element }) {
  return (
    <div>
      <Head>
        <title>Book of Days</title>
      </Head>
      <header className="container bg-primary-700 px-x2l h-x6l">
        <h1 className="text-4xl text-white">Book of Days</h1>
      </header>

      <main>{children}</main>

      <footer className="container bg-primary-700 text-white px-x2l h-x2l">
        <Link href="/contact">contact</Link>
      </footer>
    </div>
  )
}
