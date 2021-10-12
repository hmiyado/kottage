import styles from './Sentence.module.css'

export default function Sentence({
  title,
  children,
}: {
  title: string
  children: JSX.Element | string
}) {
  return (
    <>
      <h2 className={styles.headline}>{title}</h2>
      <p className={styles.body}>{children}</p>
    </>
  )
}
