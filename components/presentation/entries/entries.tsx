import styles from './entries.module.css'

import EntryComponent, { EntryProps } from 'components/plurals/entry/entry'
import CommentList from 'components/plurals/comment/commentlist/commentlist'
import { Comments as OpenApiComments } from 'repository/openapi/generated'
import { useEffect, useState } from 'react'
import EntryRepository from 'repository/entry/entryRepository'

export default function Entries({ entry }: { entry: EntryProps }): JSX.Element {
  return (
    <>
      <EntryComponent props={{ ...entry, className: styles.entry }} />
      <CommentList entrySerialNumber={entry.serialNumber} />
    </>
  )
}
