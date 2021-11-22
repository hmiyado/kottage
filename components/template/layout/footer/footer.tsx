import Link from 'next/link'
import styles from './footer.module.css'

export default function footer() {
  return (
    <footer className={styles.container}>
      <div className={styles.text}>(C) 2022 miyado</div>

      <Link href="/contact">
        <a className={styles.link}>contact</a>
      </Link>
    </footer>
  )
}
