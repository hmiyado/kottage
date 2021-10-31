import styles from './button.module.css'
import { SVG } from '*.svg'

export default function Button({
  text,
  Icon,
  ...attributes
}: {
  text: string
  Icon?: SVG
} & React.ButtonHTMLAttributes<HTMLButtonElement>) {
  if (Icon) {
    return (
      <button className={styles.imageButton} {...attributes}>
        <Icon className={styles.image} />
        {text}
      </button>
    )
  }
  return (
    <button className={styles.button} {...attributes}>
      {text}
    </button>
  )
}
