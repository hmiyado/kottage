import EntryRepository from 'api/entry/entryRepository'
import { Entry } from 'api/openapi/generated'
import EntryComponent from 'components/entry/entry'
import TwoColumn from 'components/template/twocolumn/twocolumn'
import { dateFormatter } from 'util/dateFormatter'

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
  const { dateTime, ...rest } = entry
  const entryToShow = {
    dateTime: dateFormatter['YYYY-MM-DDThh:mm:ss'](new Date(dateTime)),
    ...rest,
  }

  return {
    props: {
      entry: entryToShow,
    },
  }
}

export default function EntriesSerialNumberPage({ entry }: { entry: Entry }) {
  return (
    <TwoColumn>
      <>
        <EntryComponent
          title={entry.title}
          time={entry.dateTime.toString()}
          author={entry.author.screenName}
        >
          {entry.body}
        </EntryComponent>
      </>
    </TwoColumn>
  )
}
