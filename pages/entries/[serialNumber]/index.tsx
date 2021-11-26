import EntryRepository from 'api/entry/entryRepository'
import EntryComponent, {
  convertEntryToProps,
  EntryProps,
} from 'components/entry/entry'
import TwoColumn from 'components/template/twocolumn/twocolumn'

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
        <EntryComponent props={entry} />
      </>
    </TwoColumn>
  )
}
