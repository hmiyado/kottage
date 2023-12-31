import Link from 'next/link'
import { Constants } from '../../../../../util/constants'
import styles from './header.module.css'

export default function Header() {
  return (
    <header className={styles.header}>
      <h1 className={styles.title}>
        <Link href={'/'}>{Constants.title}</Link>
      </h1>
    </header>
  )
}
