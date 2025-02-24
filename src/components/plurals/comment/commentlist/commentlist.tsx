import { convertCommentToProps } from '../comment/comment'
import CommentComponent from 'components/plurals/comment/comment/comment'
import CommentForm from '../commentform/commentform'
import { Suspense } from 'react'
import EntryRepository from 'repository/entry/entryRepository'
import useSWR from 'swr'
import ErrorBoundary from 'components/plurals/errorboundary/errorboundary'
import CommentLoading from '../commentloading/commentloading'

const styles = {
  container: [
    'flex flex-col',
    'pt-1.0',
    'divide-y-1 divide-light-surface-overlay-border dark:divide-dark-surface-overlay-border',
  ].join(' '),
  formContainer: ['py-1.0'].join(' '),
}

export default function CommentList({
  entrySerialNumber,
}: {
  entrySerialNumber: number
}): React.JSX.Element {
  const { data: fetchedComments } = useSWR(
    `GET entries/${entrySerialNumber}/comments`,
    () => EntryRepository.fetchComments(entrySerialNumber),
    {
      shouldRetryOnError: false,
    },
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
