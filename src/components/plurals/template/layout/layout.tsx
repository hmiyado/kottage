import Head from 'next/head'
import Header from './header/header'
import Footer from './footer/footer'
import { Constants } from '../../../../util/constants'
import Script from 'next/script'

const styles = {
  container: ['min-h-screen', 'flex flex-col flex-wrap'].join(' '),
  main: [
    'h-full w-full',
    'px-1.0 lg:px-9.0',
    'flex-1',
    'bg-clip-content bg-light-surface dark:bg-dark-surface',
  ].join(' '),
}

// https://ogp.me/#types
export type OgpKey =
  | 'og:type'
  | 'og:url'
  | 'og:title'
  | 'og:description'
  | 'og:image'
  | 'og:locale'
  | 'og:article:published_time'
  | 'og:article:author'
  | 'og:article:tag'

export function Ogp(ogp: Partial<Record<OgpKey, string>>): React.JSX.Element[] {
  return Object.entries(ogp).map(([k, v]) => {
    return <meta property={k} content={v} key={k}></meta>
  })
}

const rootOgp: Partial<Record<OgpKey, string>> = {
  'og:type': 'website',
  'og:url': Constants.baseUrl,
  'og:title': Constants.title,
  'og:description': Constants.description,
  'og:image': `${Constants.baseUrl}/favicons/favicon-32x32.png`,
  'og:locale': Constants.locale,
}

export default function Layout({ children }: { children: React.JSX.Element }) {
  return (
    <>
      <Head>
        <meta charSet="utf-8" />
        <title>{Constants.title}</title>
        <meta name="description" content={Constants.description} />
        <meta name="author" content={Constants.author} />
        <meta name="keywords" content={Constants.keywords.join(',')} />

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
        {Ogp(rootOgp)}
      </Head>
      <div className={styles.container}>
        <Header />
        <div className={styles.main}>{children}</div>
        <Footer />
        <Script id="link-switch-id" type="text/javascript">
          {'var vc_pid = "887831942";'}
        </Script>
        <Script
          type="text/javascript"
          src="//aml.valuecommerce.com/vcdal.js"
          async
        ></Script>
      </div>
    </>
  )
}
