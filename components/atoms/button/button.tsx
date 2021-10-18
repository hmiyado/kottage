import styles from './button.module.css'
import { SVG } from '*.svg'

export default function Button({
  text,
  Icon,
  ...attributes
}: {
  text: string
  Icon: SVG | null
} & React.ButtonHTMLAttributes<HTMLButtonElement>) {
  if (Icon === null) {
    return (
      <button className={styles.button} {...attributes}>
        {text}
      </button>
    )
  }
  return (
    <button className={styles.imageButton} {...attributes}>
      <Icon class={styles.image} />
      {text}
    </button>
  )
}
