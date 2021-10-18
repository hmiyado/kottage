import Avatar from '../../atoms/avatar/avatar'
import styles from './profile.module.css'

export default function Profile(): JSX.Element {
  return (
    <div className={styles.container}>
      <Avatar classes={{ icon: styles.icon }} />
      <div className={styles.textContainer}>
        <div className={styles.handlename}>miyado20th</div>
        <div className={styles.description}>アノマロカリス</div>
      </div>
    </div>
  )
}
