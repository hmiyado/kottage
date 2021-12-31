import { utcToZonedTime } from 'date-fns-tz'
import { dateFormatter } from 'util/dateFormatter'
import { Comment as OpenApiComment } from 'api/openapi/generated'
import styles from './comment.module.css'
import Sentence from '../../pieces/sentence/sentence'

export interface CommentProps {
  name: string
  body: string
  createdAt: string
}

export function convertCommentToProps(
  openapiComment: OpenApiComment
): CommentProps {
  const { createdAt } = openapiComment
  const zonedDateTime = utcToZonedTime(createdAt, 'Asia/Tokyo')
  return {
    name: openapiComment.name,
    body: openapiComment.body,
    createdAt: dateFormatter['YYYY-MM-DDThh:mm:ss+0900'](zonedDateTime),
  }
}

export default function Comment({
  comment,
}: {
  comment: CommentProps
}): JSX.Element {
  return (
    <article>
      <Sentence title={''}>{comment.body}</Sentence>
      <div className={styles.footer}>
        <div className={styles.text}>
          {comment.name} {comment.createdAt}
        </div>
      </div>
    </article>
  )
}
