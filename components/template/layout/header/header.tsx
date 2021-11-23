import Link from 'next/link'
import styles from './header.module.css'

export default function Header() {
  return (
    <header className={styles.header}>
      <h1 className={styles.title}>
        <Link href={'/'}>
          <a>miyado.dev</a>
        </Link>
      </h1>
    </header>
  )
}
