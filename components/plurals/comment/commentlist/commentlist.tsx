import { convertCommentToProps } from '../comment/comment'
import styles from './commentlist.module.css'
import { Comments as OpenApiComments } from 'repository/openapi/generated'
import CommentComponent from 'components/plurals/comment/comment/comment'
import CommentForm from '../commentform/commentform'

export default function CommentList({
  comments,
  onSubmit,
}: {
  comments: OpenApiComments
  onSubmit: (name: string, body: string) => Promise<void>
}): JSX.Element {
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
        <CommentForm onSubmit={(name, body) => onSubmit(name, body)} />
      </div>
    </div>
  )
}
