import styles from './avatar.module.css'
import Icon from './miyado_icon.svg'

export default function Avatar({
  classes,
}: {
  classes: { readonly icon: string } | null
}): JSX.Element {
  const classIcon = `${styles.icon} ${classes ? classes.icon : ''}`
  return <Icon className={classIcon} />
}
