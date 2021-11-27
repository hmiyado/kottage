import styles from './entry.module.css'
import Sentence from '../atoms/sentence/sentence'
import { Entry as OpenApiEntry } from 'api/openapi/generated'
import { dateFormatter } from 'util/dateFormatter'
import Link from 'next/link'
export interface EntryProps {
  serialNumber: number
  title: string
  body: string
  time: string
  author: string
}

export function convertEntryToProps(openapiEntry: OpenApiEntry): EntryProps {
  const { dateTime } = openapiEntry
  return {
    serialNumber: openapiEntry.serialNumber,
    title: openapiEntry.title,
    body: openapiEntry.body,
    time: dateFormatter['YYYY-MM-DDThh:mm:ss'](new Date(dateTime)),
    author: openapiEntry.author.screenName,
  }
}

export default function Entry({ props }: { props: EntryProps }) {
  const Title = (
    <Link href={`/entries/${props.serialNumber}`}>
      <a>{props.title}</a>
    </Link>
  )
  return (
    <article>
      <Sentence title={Title}>{props.body}</Sentence>
      <div className={styles.footer}>
        <div className={styles.text}>{props.time}</div>
        <div className={styles.text}>{props.author}</div>
      </div>
    </article>
  )
}
