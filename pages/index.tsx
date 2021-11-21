import UserContext, { User } from '../context/user'
import { useContext, useState } from 'react'
import Button from '../components/atoms/button/button'
import Plus from '../components/atoms/button/plus.svg'
import EntryForm from '../components/entryform/entryform'
import EntryRepository from '../api/entry/entryRepository'
import { Entries } from '../api/openapi/generated'
import Entry from '../components/entry/entry'
import { dateFormatter } from '../util/dateFormatter'
import TwoColumn from '../components/template/twocolumn/twocolumn'

export async function getStaticProps() {
  try {
    const entries = await EntryRepository.getEntries()
    return {
      props: {
        entries: {
          items: entries.items
            ?.map((v) => {
              const { dateTime, ...rest } = v
              return {
                dateTime: dateFormatter['YYYY-MM-DDThh:mm:ss'](
                  new Date(dateTime)
                ),
                ...rest,
              }
            })
            .sort((a, b) => b.serialNumber - a.serialNumber),
        },
      },
    }
  } catch (e) {
    return {
      props: {
        entries: {
          items: [],
        },
      },
    }
  }
}

export default function RootPage({ entries }: { entries: Entries }) {
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
        {entries.items?.map((entry, index) => {
          return (
            <Entry
              key={index}
              title={entry.title}
              time={String(entry.dateTime)}
              author={entry.author.screenName}
            >
              {entry.body}
            </Entry>
          )
        })}
      </>
    </TwoColumn>
  )
}
