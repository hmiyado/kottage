import Sentence from '../../pieces/sentence/sentence'
import { Entry as OpenApiEntry } from 'repository/openapi/generated'
import { dateFormatter } from '../../../util/dateFormatter'
import Link from 'next/link'
import Text from '../../pieces/text/text'

const styles = {
  titleLink: ['hover:underline'].join(' '),
  footer: [
    'flex flex-col lg:flex-row justify-between mt-0.5 text-light-on-surface-medium dark:text-dark-on-surface-medium',
  ].join(' '),
  link: [
    'underline decoration-light-on-surface-medium dark:decoration-dark-on-surface-medium',
  ].join(' '),
}

export type EntryProps = {
  serialNumber: number
  title: string
  body: string
  time: string
  author: string
  commentsCount: number
} & React.DetailedHTMLProps<React.HTMLAttributes<HTMLElement>, HTMLElement>

export function convertEntryToProps(openapiEntry: OpenApiEntry): EntryProps {
  const { dateTime } = openapiEntry
  return {
    serialNumber: openapiEntry.serialNumber,
    title: openapiEntry.title,
    body: openapiEntry.body,
    time: dateFormatter['YYYY-MM-DDThh:mm:ss+09:00'](dateTime),
    author: openapiEntry.author.screenName,
    commentsCount: openapiEntry.commentsTotalCount,
  }
}

function CommentLink({
  linkToEntry,
  commentsCount,
}: {
  linkToEntry: string
  commentsCount: number
}) {
  return (
    <Link href={linkToEntry} className={styles.link}>
      <Text color="onSurfaceMedium">{`コメント ${commentsCount}`}</Text>
    </Link>
  )
}

export default function Entry({ props }: { props: EntryProps }) {
  const linkToEntry = `/entries/${props.serialNumber}`
  const Title = (
    <Link href={linkToEntry} className={styles.titleLink}>
      {props.title}
    </Link>
  )
  return (
    <article className={props.className}>
      <Sentence title={Title}>{props.body}</Sentence>
      <div className={styles.footer}>
        <Text color="onSurfaceMedium">{`${props.author} ${props.time}`}</Text>
        <CommentLink
          linkToEntry={linkToEntry}
          commentsCount={props.commentsCount}
        />
      </div>
    </article>
  )
}
