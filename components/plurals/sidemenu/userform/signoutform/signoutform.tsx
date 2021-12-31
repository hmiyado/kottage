import Button from '../../../../pieces/button/button'
import styles from './signoutform.module.css'

export type SignOutFormProps = {
  screenName: string
  onSignOutClicked: () => Promise<void>
}

export default function SignOutForm({
  screenName,
  onSignOutClicked,
}: SignOutFormProps): JSX.Element {
  return (
    <div className={styles.container}>
      <div className={styles.account}>
        <div className={styles.label}>Name</div>
        <div className={styles.body}>{screenName}</div>
      </div>
      <Button text="SIGN OUT" onClick={onSignOutClicked}></Button>
    </div>
  )
}
