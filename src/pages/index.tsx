import EntryRepository from '../repository/entry/entryRepository'
import {
  convertEntryToProps,
  EntryProps,
} from '../components/plurals/entry/entry'
import { entryPerPage, getPageCount } from './pages/[currentPage]'
import { Entry as OpenApiEntry } from 'repository/openapi/generated/models'
import { Feed } from 'feed'
import fs from 'fs'
import { Constants } from 'util/constants'
import Root from 'components/presentation/root/root'

function createAtomFeed(entries: OpenApiEntry[]) {
  const atomFilePath = '/feed/atom.xml'
  const feed = new Feed({
    id: Constants.baseUrl,
    link: Constants.baseUrl,
    title: Constants.title,
    description: Constants.description,
    copyright: Constants.copyright,
    author: { name: Constants.author },
    favicon: `${Constants.baseUrl}/favicons/favicon-16x16.png`,
    feedLinks: {
      atom: `${Constants.baseUrl}${atomFilePath}`,
    },
  })
  for (const keyword of Constants.keywords) {
    feed.addCategory(keyword)
  }

  for (const entry of entries) {
    const url = `${Constants.baseUrl}/entries/${entry.serialNumber}`
    feed.addItem({
      title: entry.title,
      id: url,
      link: url,
      content: entry.body,
      date: entry.dateTime,
      author: [
        {
          name: entry.author.screenName,
        },
      ],
    })
  }

  fs.mkdirSync('../src/public/feed', { recursive: true })
  fs.writeFileSync(`../src/public${atomFilePath}`, feed.atom1())
}

export async function getStaticProps() {
  try {
    const openapiEntries = await EntryRepository.getEntries(entryPerPage)
    const pageCount = getPageCount(openapiEntries.totalCount)
    createAtomFeed(openapiEntries.items)
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
