import UserContext, { User } from '../context/user'
import { useContext, useState } from 'react'
import Button from '../components/atoms/button/button'
import Plus from '../components/atoms/button/plus.svg'
import EntryForm from '../components/entryform/entryform'
import EntryRepository from '../api/entry/entryRepository'
import Entry, {
  convertEntryToProps,
  EntryProps,
} from '../components/entry/entry'
import TwoColumn from '../components/template/twocolumn/twocolumn'
import Pageavigation from 'components/page/pagenavigation/pagenavigation'
import { entryPerPage, getPageCount } from './pages/[currentPage]'
import { Entry as OpenApiEntry } from 'api/openapi/generated/models'
import { Feed } from 'feed'
import fs from 'fs'

function createAtomFeed(entries: OpenApiEntry[]) {
  const feed = new Feed({
    id: 'https://miyado.dev',
    title: 'miyado.dev',
    copyright: '',
    feedLinks: {
      atom: 'https://miyado.dev/feed/atom.xml',
    },
  })

  for (const entry of entries) {
    const url = `https://miyado.dev/entries/${entry.serialNumber}`
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

  fs.mkdirSync('./public/feed', { recursive: true })
  fs.writeFileSync('./public/feed/atom.xml', feed.atom1())
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
  const { user } = useContext(UserContext)
  const [showEntryForm, updateShowEntryForm] = useState(false)

  const entryForm = (_user: User, _showEntryForm: boolean) => {
    if (_user === null) {
      return null
    }
    if (_showEntryForm) {
      return (
        <EntryForm
          onSubmit={(title, body) => {
            EntryRepository.createEntry(title, body)
            updateShowEntryForm(false)
          }}
          onCancel={() => updateShowEntryForm(false)}
        />
      )
    } else {
      return (
        <Button
          text="Entry"
          Icon={Plus}
          onClick={() => updateShowEntryForm(true)}
        />
      )
    }
  }

  return (
    <TwoColumn>
      <>
        {entryForm(user, showEntryForm)}
        {entries.map((entry, index) => {
          return <Entry key={index} props={entry} />
        })}
        <Pageavigation totalPages={pageCount} currentPage={1} />
      </>
    </TwoColumn>
  )
}
