import styles from './Avatar.module.css'
import Icon from './miyado_icon.svg'

export default function Avatar({
  classes,
}: {
  classes: { readonly icon: string } | null
}): JSX.Element {
  const classIcon = `${styles.icon} ${classes ? classes.icon : ''}`
  return <Icon class={classIcon} />
}
