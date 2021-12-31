import styles from './entry.module.css'
import Sentence from '../../pieces/sentence/sentence'
import { Entry as OpenApiEntry } from 'api/openapi/generated'
import { dateFormatter } from 'util/dateFormatter'
import Link from 'next/link'
import { utcToZonedTime } from 'date-fns-tz'

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
  const Title = (
    <Link href={`/entries/${props.serialNumber}`}>
      <a className={styles.titleLink}>{props.title}</a>
    </Link>
  )
  return (
    <article className={props.className}>
      <Sentence title={Title}>{props.body}</Sentence>
      <div className={styles.footer}>
        <div className={styles.text}>
          {props.author} {props.time}
        </div>
        <div className={styles.text}>コメント({props.commentsCount})</div>
      </div>
    </article>
  )
}
