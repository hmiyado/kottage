import styles from '../styles/Entry.module.css'
import Sentence from './sentence'

export default function Entry({
  title,
  children,
}: {
  title: string
  children: JSX.Element | string
}) {
  return (
    <>
      <Sentence title={title}>{children}</Sentence>
      <div className={styles.footer}>
        <div>time</div>
        <div>author</div>
      </div>
    </>
  )
}
