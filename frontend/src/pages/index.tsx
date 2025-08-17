import EntryRepository from '../repository/entry/entryRepository'
import {
  convertEntryToProps,
  EntryProps,
} from '../components/plurals/entry/entry'
import { entryPerPage, getPageCount } from './pages/[currentPage]'
import Root from 'components/presentation/root/root'

export async function getStaticProps() {
  try {
    const openapiEntries = await EntryRepository.getEntries(entryPerPage)
    const pageCount = getPageCount(openapiEntries.totalCount)
    const entries = openapiEntries.items
      ?.map((v) => {
        return convertEntryToProps(v)
      })
      .sort((a, b) => b.serialNumber - a.serialNumber)
    return {
      props: {
        pageCount,
        entries: entries ? entries : [],
      },
    }
  } catch (e) {
    return {
      props: {
        entries: [],
      },
      // revalidate every 12 hours
      revalidate: 12 * 60 * 60,
    }
  }
}

export default function RootPage({
  pageCount,
  entries,
}: {
  pageCount: number
  entries: EntryProps[]
}) {
  return <Root pageCount={pageCount} entries={entries} />
}
