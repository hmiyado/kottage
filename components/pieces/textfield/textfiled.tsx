import styles from './textfield.module.css'

export default function TextField({
  label,
  assistiveText,
  ...attributes
}: {
  label: string
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
        </div>
      </div>
      <div className={styles.assistiveText}>{assistiveText}</div>
    </div>
  )
}
