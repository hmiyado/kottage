const styles = {
  container: ['animate-pulse flex flex-col space-y-0.5'].join(' '),
  body: ['h-4.0 w-full bg-primary-100 rounded'].join(' '),
  footer: ['h-1.0 w-10 bg-primary-100 rounded'].join(' '),
}

export default function CommentLoading(): React.JSX.Element {
  return (
    <article className={styles.container}>
      <section className={styles.body} />
      <div className={styles.footer} />
    </article>
  )
}
