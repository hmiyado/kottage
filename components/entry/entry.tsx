import styles from './entry.module.css'
import Sentence from '../atoms/sentence/sentence'

export default function Entry({
  title,
  children,
  time,
  author,
}: {
  title: string
  children: JSX.Element | string
  time: string
  author: string
}) {
  return (
    <article>
      <Sentence title={title}>{children}</Sentence>
      <div className={styles.footer}>
        <div className={styles.text}>{time}</div>
        <div className={styles.text}>{author}</div>
      </div>
    </article>
  )
}
