import styles from './commentloading.module.css'

export default function CommentLoading(): React.JSX.Element {
  return (
    <article className={styles.container}>
      <section className={styles.body} />
      <div className={styles.footer} />
    </article>
  )
}
