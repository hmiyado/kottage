import styles from './error.module.css'
import Sentence from '../atoms/sentence/sentence'

export default function Error({
  title,
  description,
}: {
  title: string
  description: string
}) {
  return (
    <section className={styles.section}>
      <Sentence title={title}>{description}</Sentence>
    </section>
  )
}
