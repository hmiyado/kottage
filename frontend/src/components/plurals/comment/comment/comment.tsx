import { Comment as OpenApiComment } from 'repository/openapi/generated'
import Sentence from '../../../pieces/sentence/sentence'
import Text from '../../../pieces/text/text'
import { dateFormatter } from '../../../../util/dateFormatter'

const styles = {
  footer: ['flex justify-between mt-0.5'].join(' '),
}

export interface CommentProps {
  name: string
  body: string
  createdAt: string
}

export function convertCommentToProps(
  openapiComment: OpenApiComment,
): CommentProps {
  const { createdAt } = openapiComment
  return {
    name: openapiComment.name,
    body: openapiComment.body,
    createdAt: dateFormatter['YYYY-MM-DDThh:mm:ss+09:00'](createdAt),
  }
}

export default function Comment({
  comment,
}: {
  comment: CommentProps
}): React.JSX.Element {
  return (
    <article>
      <Sentence title={''}>{comment.body}</Sentence>
      <div className={styles.footer}>
        <Text size={'caption'}>{`${comment.name} ${comment.createdAt}`}</Text>
      </div>
    </article>
  )
}
