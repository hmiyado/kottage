import Sentence from '../../pieces/sentence/sentence'

const styles = {
  section: [
    'bg-light-surface dark:bg-dark-surface px-2.0 py-1.0',
    'h-full',
  ].join(' '),
  link: ['text-primary-500 dark:text-primary-200'].join(' '),
}

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
