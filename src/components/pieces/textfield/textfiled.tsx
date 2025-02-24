const styles = {
  formContainer: [
    'flex flex-wrap flex-col',
    'bg-background-light-surface-overlay dark:bg-background-dark-surface-overlay',
    'rounded-t',
    'px-1.0 pt-0.5',
  ].join(' '),
  label: [
    'text-caption text-light-on-surface-disabled dark:text-dark-on-surface-disabled',
  ].join(' '),
  textLine: ['flex flex-row flex-wrap'].join(' '),
  input: [
    'text-light-on-surface-high dark:text-dark-on-surface-high',
    'focus:outline-none',
    'flex-grow',
  ].join(' '),
  icon: [
    'fill-light-on-surface text-opacity-on-surface-medium dark:fill-dark-on-surface dark:text-opacity-on-surface-medium',
    'inline-flex flex-none',
    'w-1.5 h-1.5',
  ].join(' '),
  assistiveText: [
    'text-caption text-light-on-surface dark:text-dark-on-surface text-opacity-on-surface-medium dark:text-opacity-on-surface-medium',
    'px-1.0',
    'h-1.0',
  ].join(' '),
}

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
