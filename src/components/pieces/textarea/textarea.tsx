const styles = {
  formContainer: [
    'flex flex-wrap flex-col',
    'bg-background-light-surface-overlay dark:bg-background-dark-surface-overlay',
    'rounded-t',
    'px-1.0 pt-0.5',
    'min-h-10',
  ].join(' '),
  label: [
    'text-caption text-light-on-surface-disabled dark:text-dark-on-surface-disabled',
  ].join(' '),
  textarea: [
    'text-light-on-surface-high dark:text-dark-on-surface-high',
    'focus:outline-none',
    'resize-y',
    'flex-grow',
  ].join(' '),
}

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
