import styles from './Button.module.css'

export default function Button({
  text,
  icon,
  ...attributes
}: {
  text: string
  icon: string | null
} & React.ButtonHTMLAttributes<HTMLButtonElement>) {
  return (
    <button className={styles.button} {...attributes}>
      {text}
    </button>
  )
}
