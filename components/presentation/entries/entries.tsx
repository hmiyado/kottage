import styles from './entries.module.css'

import EntryComponent, { EntryProps } from 'components/plurals/entry/entry'
import CommentList from 'components/plurals/comment/commentlist/commentlist'
import { Suspense } from 'react'
import CommentLoading from 'components/plurals/comment/commentloading/commentloading'
import ErrorBoundary from 'components/plurals/errorboundary/errorboundary'
import Button from 'components/pieces/button/button'

export default function Entries({ entry }: { entry: EntryProps }): JSX.Element {
  return (
    <>
      <EntryComponent props={{ ...entry, className: styles.entry }} />
      <ErrorBoundary>
        <Suspense fallback={<CommentLoading />}>
          <CommentList entrySerialNumber={entry.serialNumber} />
        </Suspense>
      </ErrorBoundary>
    </>
  )
}
