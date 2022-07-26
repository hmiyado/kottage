import { Feed } from 'feed'
import { GetServerSidePropsContext } from 'next'
import { entryPerPage } from '../pages/[currentPage]'
import { Constants } from 'util/constants'
import { Entry as OpenApiEntry } from 'repository/openapi/generated/models'
import EntryRepository from 'repository/entry/entryRepository'

function createAtomFeed(entries: OpenApiEntry[]): string {
  const feed = new Feed({
    id: Constants.baseUrl,
    link: Constants.baseUrl,
    title: Constants.title,
    description: Constants.description,
    copyright: Constants.copyright,
    author: { name: Constants.author },
    favicon: `${Constants.baseUrl}/favicons/favicon-16x16.png`,
    feedLinks: {
      atom: `${Constants.baseUrl}${Constants.atomFilePath}`,
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
  return feed.atom1()
}

export async function getServerSideProps({ res }: GetServerSidePropsContext) {
  const openapiEntries = await EntryRepository.getEntries(entryPerPage)
  const atom = createAtomFeed(openapiEntries.items)

  res.statusCode = 200
  res.setHeader('Cache-Control', 's-maxage=86400, stale-while-revalidate')
  res.setHeader('Content-Type', 'text/xml')
  res.end(atom)

  return {
    props: {},
  }
}

const Pages = () => null
export default Pages
