import Link from 'next/link'
import { Constants } from '../../../../../util/constants'

const styles = {
  header: [
    'bg-primary-700 dark:bg-primary-900',
    'w-full pl-2.0 lg:px-4.5',
    'h-4.5 lg:h-9.0 lg:py-3.0',
  ].join(' '),
  title: ['text-headline3 text-dark-on-surface'].join(' '),
}

export default function Header() {
  return (
    <header className={styles.header}>
      <h1 className={styles.title}>
        <Link href={'/'}>{Constants.title}</Link>
      </h1>
    </header>
  )
}
