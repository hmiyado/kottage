import Button from '../../../../pieces/button/button'
import styles from './signoutform.module.css'

export type SignOutFormProps = {
  screenName: string
  onSignOutClicked: () => void
}

export default function SignOutForm({
  screenName,
  onSignOutClicked,
}: SignOutFormProps): JSX.Element {
  return (
    <div className={styles.container}>
      <dl className={styles.account}>
        <dt className={styles.label}>Name</dt>
        <dd className={styles.body}>{screenName}</dd>
      </dl>
      <Button text="SIGN OUT" onClick={onSignOutClicked}></Button>
    </div>
  )
}
