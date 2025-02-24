const styles = {
  formContainer: [
    'flex flex-wrap flex-col',
    'bg-background-light-surface-overlay dark:bg-background-dark-surface-overlay',
    'rounded-t',
    'px-1.0 pt-0.5',
    'min-h-10',
  ].join(' '),
  label: [
    'text-caption text-light-on-surface dark:text-dark-on-surface text-opacity-on-surface-disabled dark:text-opacity-on-surface-disabled',
  ].join(' '),
  textarea: [
    'bg-light-surface opacity-(--background-opacity-transparent)',
    'text-light-on-surface text-opacity-on-surface-high dark:text-dark-on-surface dark:text-opacity-on-surface-high',
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
