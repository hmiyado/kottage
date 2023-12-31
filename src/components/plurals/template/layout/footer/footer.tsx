import Link from 'next/link'
import { Constants } from '../../../../../util/constants'
import styles from './footer.module.css'

export default function footer() {
  return (
    <footer className={styles.container}>
      <div className={styles.text}>{Constants.copyright}</div>

      <Link href="/contact" className={styles.link}>
        contact
      </Link>
    </footer>
  )
}
