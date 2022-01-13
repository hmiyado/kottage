import EntryRepository from 'repository/entry/entryRepository'
import { convertEntryToProps, EntryProps } from 'components/plurals/entry/entry'
import { rangeArray } from 'util/rangeArray'
import Root from 'components/presentation/root/root'

export const entryPerPage = 5

export function getPageCount(totalCount: number) {
  return Math.floor(totalCount / entryPerPage) + 1
}

export async function getStaticPaths() {
  const entries = await EntryRepository.getEntries(1, 0)
  const pageCount = getPageCount(entries.totalCount)

  return {
    paths: rangeArray(1, pageCount).map((currentPage) => {
      return {
        params: { currentPage: currentPage.toString() },
      }
    }),
    fallback: 'blocking',
  }
}

export async function getStaticProps({
  params,
}: {
  params: {
    currentPage: string
  }
}) {
  const currentPage = parseInt(params.currentPage)
  try {
    const openapiEntries = await EntryRepository.getEntries(
      entryPerPage,
      entryPerPage * (currentPage - 1)
    )
    const pageCount = getPageCount(openapiEntries.totalCount)
    const entries = openapiEntries.items
      ?.map((v) => {
        return convertEntryToProps(v)
      })
      .sort((a, b) => b.serialNumber - a.serialNumber)
    return {
      props: {
        pageCount,
        currentPage,
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

export default function PagesPage({
  pageCount,
  currentPage,
  entries,
}: {
  pageCount: number
  currentPage: number
  entries: EntryProps[]
}) {
  return (
    <Root pageCount={pageCount} entries={entries} currentPage={currentPage} />
  )
}
