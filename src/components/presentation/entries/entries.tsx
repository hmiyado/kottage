import EntryComponent, { EntryProps } from 'components/plurals/entry/entry'
import CommentList from 'components/plurals/comment/commentlist/commentlist'
import { Suspense } from 'react'
import CommentLoading from 'components/plurals/comment/commentloading/commentloading'
import ErrorBoundary from 'components/plurals/errorboundary/errorboundary'

const styles = {
  entry: [
    'border-b-1 border-light-surface-overlay-border dark:border-dark-surface-overlay-border',
  ].join(' '),
  commentListContainer: ['ml-2.0 lg:ml-4.5'].join(' '),
}

export default function Entries({
  entry,
}: {
  entry: EntryProps
}): React.JSX.Element {
  return (
    <>
      <EntryComponent props={{ ...entry, className: styles.entry }} />
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
