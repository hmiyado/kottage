import styles from './textfield.module.css'
import { SVG } from '*.svg'

export default function TextField({
  label,
  Icon,
  assistiveText,
  ...attributes
}: {
  label: string
  Icon: SVG | null
  assistiveText: string | null
} & React.InputHTMLAttributes<HTMLInputElement>) {
  const { type = 'text' } = attributes
  const realType = ['text', 'password', 'number', 'email'].includes(type)
    ? type
    : 'text'
  return (
    <div>
      <div className={styles.formContainer}>
        <label htmlFor={label} className={styles.label}>
          {label}
        </label>
        <div className={styles.textLine}>
          <input
            id={label}
            name={label}
            className={styles.input}
            type={realType}
            {...attributes}
          />
          {Icon ? <Icon className={styles.icon} /> : null}
        </div>
      </div>
      <div className={styles.assistiveText}>{assistiveText}</div>
    </div>
  )
}
