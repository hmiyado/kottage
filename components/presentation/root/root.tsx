import EntryRepository from 'repository/entry/entryRepository'
import Button from 'components/pieces/button/button'
import Entry, { EntryProps } from 'components/plurals/entry/entry'
import EntryForm from 'components/plurals/entryform/entryform'
import PageNavigation from 'components/plurals/page/pagenavigation/pagenavigation'
import TwoColumn from 'components/plurals/template/twocolumn/twocolumn'
import styles from './root.module.css'
import { useContext, useState } from 'react'
import UserContext, { User } from 'context/user'

export default function Root({
  pageCount,
  currentPage,
  entries,
}: {
  pageCount: number
  currentPage?: number
  entries: EntryProps[]
}): JSX.Element {
  const { user } = useContext(UserContext)
  const [showEntryForm, updateShowEntryForm] = useState(false)

  const entryForm = (_user: User, _showEntryForm: boolean) => {
    if (currentPage !== undefined) {
      // don't show entry form if current page is present
      return null
    }
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
        <Button text="作成する" onClick={() => updateShowEntryForm(true)} />
      )
    }
  }

  return (
    <TwoColumn mainColumnClassName={styles.mainColumn}>
      <>
        {entryForm(user, showEntryForm)}
        {entries.map((entry, index) => {
          return <Entry key={index} props={entry} />
        })}
        <PageNavigation
          totalPages={pageCount}
          currentPage={currentPage ? currentPage : 1}
        />
      </>
    </TwoColumn>
  )
}
