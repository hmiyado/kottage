import { convertCommentToProps } from '../comment/comment'
import styles from './commentlist.module.css'
import { Comments as OpenApiComments } from 'repository/openapi/generated'
import CommentComponent from 'components/plurals/comment/comment/comment'
import CommentForm from '../commentform/commentform'
import { Suspense, useEffect, useState } from 'react'
import entry from 'components/plurals/entry/entry'
import EntryRepository from 'repository/entry/entryRepository'
import useSWR from 'swr'
import ErrorBoundary from 'components/plurals/errorboundary/errorboundary'
import CommentLoading from '../commentloading/commentloading'

export default function CommentList({
  entrySerialNumber,
}: {
  entrySerialNumber: number
}): JSX.Element {
  const { data: fetchedComments } = useSWR(
    `GET entries/${entrySerialNumber}/comments`,
    () => EntryRepository.fetchComments(entrySerialNumber),
    {
      shouldRetryOnError: false,
      suspense: false,
      fallbackData: {
        totalCount: 0,
        items: [],
      },
    }
  )
  const comments = fetchedComments ?? {
    totalCount: 0,
    items: [],
  }
  const items = comments.items
  items.sort((a, b) => a.id - b.id)

  return (
    <div className={styles.container}>
      {items
        .map((comment) => convertCommentToProps(comment))
        .map((comment, index) => {
          return <CommentComponent key={index} comment={comment} />
        })}
      <div className={styles.formContainer}>
        <ErrorBoundary>
          <Suspense fallback={<CommentLoading />}>
            <CommentForm entrySerialNumber={entrySerialNumber} />
          </Suspense>
        </ErrorBoundary>
      </div>
    </div>
  )
}
