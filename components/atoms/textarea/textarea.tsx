import styles from './textArea.module.css'

export default function TextArea({
  label,
  ...attributes
}: {
  label: string
} & React.TextareaHTMLAttributes<HTMLTextAreaElement>) {
  return (
    <div className={styles.formContainer}>
      <label htmlFor={label} className={styles.label}>
        {label}
      </label>
      <textarea
        id={label}
        name={label}
        className={styles.textarea}
        {...attributes}
      />
    </div>
  )
}
