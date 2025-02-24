import Link from 'next/link'

const styles = {
  container: [
    'flex flex-row space-x-1.0 lg:space-x-2.0 justify-center',
    'w-full py-1.0',
    'text-caption',
  ].join(' '),
  currentPage: [
    'text-light-surface-overlay text-opacity-on-surface-medium dark:text-dark-on-surface dark:text-opacity-on-surface-medium',
  ].join(' '),
  link: [
    'underline',
    'text-light-surface-overlay text-opacity-on-surface-medium dark:text-dark-on-surface dark:text-opacity-on-surface-medium',
  ].join(' '),
  disabledLink: [
    'text-light-on-surface-disabled dark:text-dark-on-surface-disabled',
  ].join(' '),
}

function PageLink({
  href,
  text,
  enabled,
}: {
  href: string
  text: string
  enabled?: boolean
}) {
  if (enabled) {
    return (
      <Link href={href} className={styles.link}>
        {text}
      </Link>
    )
  } else {
    return <span className={styles.disabledLink}>{text}</span>
  }
}

export default function PageNavigation({
  totalPages,
  currentPage,
}: {
  totalPages: number
  currentPage: number
}): React.JSX.Element {
  const enableNext = currentPage < totalPages
  const enablePrevious = currentPage > 1
  return (
    <nav className={styles.container}>
      <PageLink href={'/pages/1'} text="|< 最新" enabled={enablePrevious} />
      <PageLink
        href={`/pages/${currentPage - 1}`}
        text="< 前へ"
        enabled={enablePrevious}
      />
      <span className={styles.currentPage}>{currentPage}</span>
      <PageLink
        href={`/pages/${currentPage + 1}`}
        text="次へ >"
        enabled={enableNext}
      />
      <PageLink
        href={`/pages/${totalPages}`}
        text="最終 >|"
        enabled={enableNext}
      />
    </nav>
  )
}
