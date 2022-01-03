import EntryRepository from 'api/entry/entryRepository'
import { convertEntryToProps, EntryProps } from 'components/plurals/entry/entry'
import { Ogp } from 'components/plurals/template/layout/layout'
import TwoColumn from 'components/plurals/template/twocolumn/twocolumn'
import Entries from 'components/presentation/entries/entries'
import Head from 'next/head'
import { Constants } from 'util/constants'

export async function getStaticPaths() {
  const entries = await EntryRepository.getEntries()
  const serialNumbers = entries.items?.map((v) => {
    return { params: { serialNumber: v.serialNumber.toString() } }
  })

  return {
    paths: serialNumbers ? serialNumbers : [],
    fallback: false,
  }
}

export async function getStaticProps({
  params,
}: {
  params: { serialNumber: number }
}) {
  const entry = await EntryRepository.fetchEntry(params.serialNumber)

  return {
    props: {
      entry: convertEntryToProps(entry),
    },
  }
}

export default function EntriesSerialNumberPage({
  entry,
}: {
  entry: EntryProps
}) {
  return (
    <TwoColumn>
      <>
        <Head>
          {Ogp({
            'og:title': `${entry.title} - ${Constants.title}`,
            'og:url': `${Constants.baseUrl}/entries/${entry.serialNumber}`,
            'og:description': entry.body,
            'og:type': 'article',
            'og:article:published_time': entry.time,
            'og:article:author': Constants.author,
          })}
        </Head>
        <Entries entry={entry} />
      </>
    </TwoColumn>
  )
}
