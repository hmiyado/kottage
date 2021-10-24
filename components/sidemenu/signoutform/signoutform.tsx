import Button from '../../atoms/button/button'
import styles from './signoutform.module.css'

export default function SignOutForm({
  screenName,
  onSignOutClicked,
}: {
  screenName: string
  onSignOutClicked: () => void
}): JSX.Element {
  return (
    <div className={styles.container}>
      <div className={styles.account}>
        <div className={styles.label}>Name</div>
        <div className={styles.body}>{screenName}</div>
      </div>
      <Button text="SIGN OUT" Icon={null} onClick={onSignOutClicked}></Button>
    </div>
  )
}
