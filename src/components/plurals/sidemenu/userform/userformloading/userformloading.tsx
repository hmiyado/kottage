import styles from './userformloading.module.css'

export default function UserFormLoading({}: {}): JSX.Element {
  return (
    <div className={styles.container}>
      <div className={styles.content}></div>
      <div className={styles.content}></div>
    </div>
  )
}
