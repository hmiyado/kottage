import { convertCommentToProps } from '../comment/comment'
import styles from './commentlist.module.css'
import { Comments as OpenApiComments } from 'api/openapi/generated'
import CommentComponent from 'components/comment/comment/comment'
import CommentForm from '../commentform/commentform'
import { useState } from 'react'
import Button from 'components/atoms/button/button'
import Plus from '../../../components/atoms/button/plus.svg'

export default function CommentList({
  comments,
  onSubmit,
}: {
  comments: OpenApiComments
  onSubmit: (name: string, body: string) => void
}): JSX.Element {
  const [showCommentForm, updateShowCommentForm] = useState(false)

  const commentForm = (_showCommentForm: boolean) => {
    if (_showCommentForm) {
      return (
        <CommentForm
          onSubmit={(name, body) => {
            onSubmit(name, body)
            updateShowCommentForm(false)
          }}
          onCancel={() => updateShowCommentForm(false)}
        />
      )
    } else {
      return (
        <Button
          text="COMMENT"
          Icon={Plus}
          onClick={() => updateShowCommentForm(true)}
        />
      )
    }
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
      <div className={styles.formContainer}>{commentForm(showCommentForm)}</div>
    </div>
  )
}
