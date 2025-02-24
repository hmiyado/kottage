import Link from 'next/link'
import { Constants } from '../../../../../util/constants'

const styles = {
  container: [
    'flex flex-row flex-wrap space-x-2.0 items-center',
    'bg-primary-700 dark:bg-primary-900',
    'h-2.0 lg:h-4.5 pl-2.5 lg:px-4.5',
  ].join(' '),
  text: ['text-body1 text-dark-on-surface dark:text-dark-on-surface'].join(' '),
  link: ['text-body1 text-primary-200 dark:text-primary-200'].join(' '),
}

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
