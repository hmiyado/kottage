import Head from 'next/head'
import Link from 'next/link'

export default function Layout({ children }: { children: JSX.Element }) {
  return (
    <div>
      <Head>
        <title>Book of Days</title>
      </Head>
      <header className="container bg-primary-700 px-x2l h-x6l">
        <h1 className="text-headline1 text-white">Book of Days</h1>
      </header>

      <main>{children}</main>

      <footer className="container bg-primary-700 px-x2l py-l h-x2l">
        <Link href="/contact">
          <a className="text-white text-body1">contact</a>
        </Link>
      </footer>
    </div>
  )
}
