import EntryComponent, { EntryProps } from '../../plurals/entry/entry'
import CommentList from '../../plurals/comment/commentlist/commentlist'
import { Suspense, useContext } from 'react'
import CommentLoading from '../../plurals/comment/commentloading/commentloading'
import ErrorBoundary from '../../plurals/errorboundary/errorboundary'
import Button from '../../pieces/button/button'
import UserContext from '../../../context/user'
import EntryRepository from '../../../repository/entry/entryRepository'

const styles = {
  entry: [
    'border-b-1 border-light-surface-overlay-border dark:border-dark-surface-overlay-border',
  ].join(' '),
  deleteButtonContainer: ['mt-0.5'].join(' '),
  commentListContainer: ['ml-2.0 lg:ml-4.5'].join(' '),
}

export default function Entries({
  entry,
}: {
  entry: EntryProps
}): React.JSX.Element {
  const { user } = useContext(UserContext)

  const handleDelete = () => {
    const shouldDelete = window.confirm(
      'この記事を削除しますか？ この操作は取り消せません。',
    )
    if (!shouldDelete) {
      return
    }
    EntryRepository.deleteEntry(entry.serialNumber)
    window.location.href = '/'
  }

  return (
    <>
      <EntryComponent props={{ ...entry, className: styles.entry }} />
      {user !== null && (
        <div className={styles.deleteButtonContainer}>
          <Button text="削除する" onClick={handleDelete} />
        </div>
      )}
      <div className={styles.commentListContainer}>
        <ErrorBoundary>
          <Suspense fallback={<CommentLoading />}>
            <CommentList entrySerialNumber={entry.serialNumber} />
          </Suspense>
        </ErrorBoundary>
      </div>
    </>
  )
}
