import styles from './entry.module.css'
import Sentence from '../../pieces/sentence/sentence'
import { Entry as OpenApiEntry } from 'repository/openapi/generated'
import { dateFormatter } from 'util/dateFormatter'
import Link from 'next/link'
import { utcToZonedTime } from 'date-fns-tz'
import Text from 'components/pieces/text/text'

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
  const zonedDateTime = utcToZonedTime(dateTime, 'Asia/Tokyo')
  return {
    serialNumber: openapiEntry.serialNumber,
    title: openapiEntry.title,
    body: openapiEntry.body,
    time: dateFormatter['YYYY-MM-DDThh:mm:ss+0900'](zonedDateTime),
    author: openapiEntry.author.screenName,
    commentsCount: openapiEntry.commentsTotalCount,
  }
}

export default function Entry({ props }: { props: EntryProps }) {
  const linkToEntry = `/entries/${props.serialNumber}`
  const Title = (
    <Link href={linkToEntry} className={styles.titleLink}>
      {props.title}
    </Link>
  )
  const Comment = () => (
    <Link href={linkToEntry} className={styles.link}>
      <Text>{`コメント ${props.commentsCount}`}</Text>
    </Link>
  )
  return (
    <article className={props.className}>
      <Sentence title={Title}>{props.body}</Sentence>
      <div className={styles.footer}>
        <Text>{`${props.author} ${props.time}`}</Text>
        <Comment />
      </div>
    </article>
  )
}
