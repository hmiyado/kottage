import EntryRepository from 'api/entry/entryRepository'
import Entry from 'components/entry/entry'
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
  console.log(params.serialNumber)

  return {
    props: {
      serialNumber: params.serialNumber,
    },
  }
}

export default function EntriesSerialNumberPage({
  serialNumber,
}: {
  serialNumber: number
}) {
  return (
    <TwoColumn>
      <>
        <Entry title="title" time="time" author="author">
          {serialNumber}
        </Entry>
      </>
    </TwoColumn>
  )
}
