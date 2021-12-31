import EntryRepository from 'api/entry/entryRepository'
import { convertEntryToProps, EntryProps } from 'components/plurals/entry/entry'
import TwoColumn from 'components/plurals/template/twocolumn/twocolumn'
import Entries from 'components/presentation/entries/entries'

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
      <Entries entry={entry} />
    </TwoColumn>
  )
}
