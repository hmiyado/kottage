const styles = {
  container: ['animate-pulse flex flex-col space-y-0.5'].join(' '),
  content: ['bg-primary-100 h-2.0 rounded'].join(' '),
}

export default function UserFormLoading({}: {}): React.JSX.Element {
  return (
    <div className={styles.container}>
      <div className={styles.content}></div>
      <div className={styles.content}></div>
    </div>
  )
}
