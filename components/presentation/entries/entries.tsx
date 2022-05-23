import styles from './entries.module.css'

import EntryComponent, { EntryProps } from 'components/plurals/entry/entry'
import CommentList from 'components/plurals/comment/commentlist/commentlist'
import { Suspense } from 'react'
import CommentLoading from 'components/plurals/comment/commentloading/commentloading'

export default function Entries({ entry }: { entry: EntryProps }): JSX.Element {
  return (
    <>
      <EntryComponent props={{ ...entry, className: styles.entry }} />
      <Suspense fallback={<CommentLoading />}>
        <CommentList entrySerialNumber={entry.serialNumber} />
      </Suspense>
    </>
  )
}
