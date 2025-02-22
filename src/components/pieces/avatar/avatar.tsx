import React from 'react'
import styles from './avatar.module.css'

export default function Avatar({
  classes,
}: {
  classes: { readonly icon: string } | null
}): React.JSX.Element {
  const classIcon = `${styles.icon} ${classes ? classes.icon : ''}`
  return (
    // eslint-disable-next-line @next/next/no-img-element
    <img
      src="/components/pieces/avatar/miyado_icon.svg"
      alt="avatar"
      className={classIcon}
    />
  )
}
