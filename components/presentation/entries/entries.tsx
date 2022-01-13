import styles from './entries.module.css'

import EntryComponent, { EntryProps } from 'components/plurals/entry/entry'
import CommentList from 'components/plurals/comment/commentlist/commentlist'
import { Comments as OpenApiComments } from 'repository/openapi/generated'
import { useEffect, useState } from 'react'
import EntryRepository from 'repository/entry/entryRepository'

export default function Entries({ entry }: { entry: EntryProps }): JSX.Element {
  const [comments, updateComments] = useState<OpenApiComments>({
    totalCount: 0,
    items: [],
  })

  useEffect(() => {
    EntryRepository.fetchComments(entry.serialNumber)
      .then((fetchedComments) => {
        updateComments(fetchedComments)
      })
      .catch(() => {
        /* do nothing */
      })
  }, [entry.serialNumber, updateComments])

  const onSubmit = (name: string, body: string) => {
    EntryRepository.createComment(entry.serialNumber, name, body)
      .then((comment) =>
        updateComments({
          totalCount: comments.totalCount + 1,
          items: comments.items.concat([comment]),
        })
      )
      .catch(() => {
        /* do nothing */
      })
  }

  return (
    <>
      <EntryComponent props={{ ...entry, className: styles.entry }} />
      <CommentList comments={comments} onSubmit={onSubmit} />
    </>
  )
}
